/*
 * Copyright (C) 2020, Bost.com. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.bost.futures;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bost.futures.model.*;
import com.bost.futures.model.enums.TransferDirection;
import com.bost.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Slf4j
abstract class BostFuturesUSDTClientBase {

    public String restHost = "https://api.trade-planets.com";
    public String urlWss = "wss://push.trade-planets.com/cbu";

    public void setRestHost(String restHost) {
        this.restHost = restHost;
    }

    public void setUrlWss(String urlWss) {
        this.urlWss = urlWss;
    }

    String rest(String uri) {
        return restHost + uri;
    }

    // URL for Private Data
    public static final String URL_TRANSFER = "/api/v1/ac/transfer";
    public static final String URL_PLACE_ORDER = "/api/v1/tc/order/open";
    public static final String URL_CANCEL_ORDER = "/api/v1/tc/order/close";
    public static final String URL_QUERY_OPEN_ORDER = "/api/v1/tc/order/list";
    public static final String URL_QUERY_ORDER = "/api/v1/qc/order/by/id";
    public static final String URL_QUERY_ORDER_HISTORY = "/api/v1/qc/order/history";
    public static final String URL_CANCEL_ORDERS = "/api/v1/tc/order/closeBatch";
    public static final String URL_CANCEL_ALL_ORDERS = "/api/v1/tc/order/closeAll";
    public static final String URL_TRANSFER_MARGIN = "/api/v1/tc/changeMargin";
    public static final String URL_CHANGE_MARGIN_MODE = "/api/v1/tc/changeMode";
    public static final String URL_ACCOUNT = "/api/v1/tc/assets";
    public static final String URL_POSITION = "/api/v1/tc/position";
    public static final String URL_POSITION_CHANGE_HISTORY = "/api/v1/qc/deal/log";
    public static final String URL_TRADES = "/api/v1/qc/order/detail";

    // URL for Market Data
    public static final String URL_CONTRACT = "/api/v1/qc/value";
    public static final String URL_CANDLESTICK = "/api/v1/mudata/kline";
    public static final String URL_ORDER_BOOK = "/api/v1/mudata/depth";
    public static final String URL_SYMBOL_PRECISION = "/api/v1/qc/unit";
    public static final String URL_MARK_PRICE = "/api/v1/qc/tag/price";
    public static final String URL_FUNDING_RATE = "/api/v1/qc/fund/rate";
    public static final String URL_TIME = "/api/v1/tc/timestamp";

    // Headers
    public static final String HEADER_SIGN = "api-sign";
    public static final String HEADER_API_KEY = "api-key";
    public static final String HEADER_TIMESTAMP = "api-timestamp";

    private final String apiKey;
    private final String secretKey;
    private final Map<String, Subscription> subscriptions = new HashMap<>();
    private final Map<String, PrivateSubscription> privateSubscriptions = new HashMap<>();

    private Handler messageHandler;
    private Handler subscriptionHandler;
    private HttpUtils.WebSocket webSocket;
    private Runnable mScheduledReconnect;
    private boolean mInitialized;

    /**
     * Initialize an instance
     */
    public BostFuturesUSDTClientBase() {
        apiKey = "";
        secretKey = "";
    }

    public BostFuturesUSDTClientBase(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    String getApiKey() {
        return apiKey;
    }

    Handler getMessageHandler() {
        return messageHandler;
    }

    Handler getSubscriptionHandler() {
        return subscriptionHandler;
    }

    protected void subscribe(Subscription sub) {
        ensureInitialized();
        messageHandler.post(() -> {
            String channel = sub.getChannel();
            if (sub instanceof PrivateSubscription) {
                if (!privateSubscriptions.containsKey(channel)) {
                    privateSubscriptions.put(channel, (PrivateSubscription) sub);
                    sub.start();
                }
            } else {
                if (!subscriptions.containsKey(channel)) {
                    subscriptions.put(channel, sub);
                    sub.start();
                }
            }
        });
    }

    public void unsubscribe(String channel) {
        if (messageHandler!=null) {
            messageHandler.post(() -> {
                Subscription sub = subscriptions.remove(channel);
                if (sub != null) {
                    sub.stop();
                }
            });
        }
    }

    public void unsubscribeAllPrivateSubscriptions() {
        messageHandler.post(() -> {
            sendUnsubscribeMessage(PrivateSubscription.CHANNEL_PREFIX, apiKey);
            privateSubscriptions.clear();
        });
    }

    private void sendPing() {
        JSONObject json = new JSONObject();
        json.put("ping", System.currentTimeMillis()/1000);
        sendMessage(json.toJSONString());
    }

    private void sendPong(long id) {
        JSONObject json = new JSONObject();
        json.put("pong", id);
        sendMessage(json.toJSONString());
    }

    void sendSubscribeMessage(Subscription sub) {
        sendMessage(sub.toString());
    }

    void sendUnsubscribeMessage(Subscription sub) {
        sendUnsubscribeMessage(sub.getChannel());
    }

    void sendUnsubscribeMessage(String channel) {
        JSONObject json = new JSONObject();
        json.put("unsub", channel);
        sendMessage(json.toJSONString());
    }

    void sendUnsubscribeMessage(String channel,String apiKey) {
        JSONObject json = new JSONObject();
        json.put("unsub", channel);
        json.put("apikey", apiKey);
        sendMessage(json.toJSONString());
    }

    void sendMessage(String text) {
        connectWebSocket();
        webSocket.send(text);
    }

    protected void ensureInitialized() {
        if (!mInitialized) {
            init();
        }
    }

    private synchronized void init() {
        if (mInitialized) {
            return;
        }

        if (Looper.myQueue() == null) {
            Future<RuntimeException> future = new Future<>();
            Executors.newSingleThreadExecutor().execute(() -> {
                Thread.currentThread().setName("bibox-websocket-executor");
                Looper.prepare();
                messageHandler = new Handler();
                messageHandler.post(() -> future.set(null));
                Looper.loop();
            });
            future.get();
        } else {
            messageHandler = new Handler();
        }

        Future<RuntimeException> future = new Future<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            Thread.currentThread().setName("bibox-subscription-executor");
            Looper.prepare();
            subscriptionHandler = new Handler();
            subscriptionHandler.post(() -> future.set(null));
            Looper.loop();
        });

        future.get();
        mInitialized = true;
    }

    private void connectWebSocket() {
        if (webSocket == null) {
            log.info("Connecting to '{}'", urlWss);

            webSocket = HttpUtils.createWebSocket(urlWss,
                    new HttpUtils.WebSocketListener() {
                        @Override
                        public void onOpen(HttpUtils.WebSocket socket) {
                            messageHandler.post(() -> onWebSocketOpen());
                        }

                        @Override
                        public void onMessage(HttpUtils.WebSocket socket, byte[] bytes) {
                            String text = UncompressUtils.decodeBytes(bytes);
                            if (text == null) {
                                return;
                            }
                            messageHandler.post(() -> onWebSocketMessage(text));
                        }

                        @Override
                        public void onFailure(HttpUtils.WebSocket socket, Throwable error) {
                            onWebSocketFailure(error);
                        }
                    });
        }
    }

    private void onWebSocketOpen() {
        log.info("Connected to '{}'", urlWss);
        for (Subscription e : subscriptions.values()) {
            e.start();
        }
        for (Subscription e : privateSubscriptions.values()) {
            e.start();
        }
    }

    private void onWebSocketMessage(String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }

        scheduleReconnect();

        if (!isArrMsg(text)) {
            // Ping
            JSONObject json = JSON.parseObject(text);
            long pingId = JSONUtils.parsePing(json);
            if (pingId >= 0) {
                log.info("ping [{}]", pingId);
                sendPong(pingId);
                return;
            }
            // sub msg
            String channel = json.getString("topic");
            if (StringUtils.isEmpty(channel)) {
                return;
            }
            if (PrivateSubscription.CHANNEL_PREFIX.equals(channel)) {
                JSONObject data = json.getJSONObject("d");
                for (PrivateSubscription sub : privateSubscriptions.values()) {
                    if (data.containsKey(sub.getDataName())) {
                        sub.onMessage(data);
                        return;
                    }
                }
                return;
            }

            Subscription sub = subscriptions.get(channel);
            if (sub != null) {
                sub.onMessage(json);
            }
        }
    }

    private void onWebSocketFailure(Throwable error) {
        log.warn("", error);

        if (error instanceof IOException) {
            messageHandler.postDelayed(() -> {
                log.error("Cannot connect to '{}'", urlWss, error);
                reconnectWebSocket();
                subscriptions.values().forEach(this::subscribe);
                privateSubscriptions.values().forEach(this::subscribe);
            }, 2_000);
        }
    }

    private void scheduleReconnect() {

        // 删除计划任务
        if (mScheduledReconnect != null) {
            messageHandler.removeCallbacks(mScheduledReconnect);
            mScheduledReconnect = null;
        }

        // 为稍后重连创建计划任务
        messageHandler.postDelayed(mScheduledReconnect = () -> {
            mScheduledReconnect = null;
            try {
                sendPing();
            }catch (Exception e) {
                reconnectWebSocket();
            }
        }, 30_000);
    }

    private void reconnectWebSocket() {
        if (webSocket != null) {
            try {
                webSocket.close();
            }catch (Exception e){
                // 关闭失败
                log.warn(e.getMessage());
            }
            webSocket = null;
            connectWebSocket();
        }
    }

    protected String buildSignature() {
        String oriStr = String.format("{\"apikey\":\"%s\",\"sub\":\"%s\"}",
                apiKey, PrivateSubscription.CHANNEL_PREFIX);
        return Hex.encodeHexString(MacUtils.buildMAC(
                oriStr, "HmacMD5", this.secretKey)).toLowerCase();
    }

    private boolean isArrMsg(String text) {
        return text.startsWith("[");
    }

    protected HttpHeader buildHeader(String text) {
        long timestamp = System.currentTimeMillis();
        String sign = buildSignature(timestamp + text);
        return new HttpHeader()
                .put(HEADER_TIMESTAMP, String.valueOf(timestamp))
                .put(HEADER_API_KEY, apiKey)
                .put(HEADER_SIGN, sign);
    }

    protected HttpQuery newQuery() {
        return new HttpQuery();
    }

    protected String buildSignature(String text) {
        return Hex.encodeHexString(
                MacUtils.buildMAC(text, "HmacMD5", secretKey)).toLowerCase();
    }

    protected String convertSymbol(String symbol) {
        return SymbolConverter.convert(symbol);
    }

    /**
     * @param asset     账户
     * @param amount    金额
     * @param direction 转入/转出
     */
    protected void transfer(String asset,
                            BigDecimal amount, TransferDirection direction) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("amount", amount.toString());
        json.put("symbol", asset);
        json.put("type", direction.getValue());

        JSONUtils.parseError(doPost(URL_TRANSFER, json.toJSONString()));
    }

    /**
     * @param order 限价
     */
    protected String placeOrder(LimitOrder order) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(order.getSymbol()));
        json.put("amount", order.getQuantity());
        json.put("price", order.getPrice().toString());
        json.put("order_side", ApiOrderSide.lookupOrderSide(order).getValue());
        json.put("order_type", 2);
        json.put("order_from", 6);
        if (!StringUtils.isEmpty(order.getClientOrderId())) {
            json.put("client_oid", order.getClientOrderId());
        }
        return JSONUtils.parseOrderId(doPost(URL_PLACE_ORDER, json.toJSONString()));
    }

    /**
     * @param order 市价
     */
    protected String placeOrder(MarketOrder order) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(order.getSymbol()));
        json.put("amount", order.getQuantity());
        json.put("order_side", ApiOrderSide.lookupOrderSide(order).getValue());
        json.put("order_type", 1);
        json.put("order_from", 6);
        json.put("price", 1);
        if (!StringUtils.isEmpty(order.getClientOrderId())) {
            json.put("client_oid", order.getClientOrderId());
        }
        return JSONUtils.parseOrderId(doPost(URL_PLACE_ORDER, json.toJSONString()));
    }

    protected String doPost(String url, String bodyString) throws Throwable {
        return HttpUtils.doPost(rest(url), buildHeader(bodyString), HttpUtils.MT_JSON, bodyString);
    }

    protected List<Contract> contractsWrapper(List<Contract> contracts) throws Throwable {
        HashMap<String, BigDecimal> symbolPrecisions =
                JSONUtils.parseSymbolPrecisions(HttpUtils.doGet(rest(URL_SYMBOL_PRECISION),
                        newQuery()));
        HashMap<String, BigDecimal> fundingRates =
                JSONUtils.parseFundingRates(HttpUtils.doGet(rest(URL_FUNDING_RATE), newQuery()));
        contracts.forEach(item -> Contract.wrapper(item,fundingRates.get(item.getSymbol()),
                symbolPrecisions.get(item.getSymbol())));
        return contracts;
    }

    protected List<Position> positionsWrapper(List<Position> positions) throws Throwable {
        HashMap<String, BigDecimal> units =
                JSONUtils.parseUnits(HttpUtils.doGet(rest(URL_CONTRACT), newQuery()));
        positions.forEach(item->Position.wrapper(item,units.get(item.getSymbol())));
        return positions;
    }

    protected List<ContractInfo> contractInfosWrapper(
            List<ContractInfo> contractInfos) throws Throwable {
        HashMap<String, BigDecimal> fundingRates =
                JSONUtils.parseFundingRates(HttpUtils.doGet(rest(URL_FUNDING_RATE), newQuery()));
        contractInfos.forEach(item -> ContractInfo.wrapper(item, fundingRates.get(item.getSymbol())));
        return contractInfos;
    }

}
