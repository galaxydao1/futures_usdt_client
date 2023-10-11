package com.bost.example.websocket;

import com.bost.futures.BostFuturesUSDTClient;

public class SubTickerEvent {

    public static void main(String[] args) {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        // 处理业务逻辑
        String symbol = "BTC_USDT";
        client.subscribeTicker(symbol, x -> {
            System.out.println(x);
            // ...
        });
        // client.unSubscribeTickerEvent(symbol);
    }

}
