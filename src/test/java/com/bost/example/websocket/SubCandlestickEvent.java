package com.bost.example.websocket;

import com.bost.futures.BostFuturesUSDTClient;
import com.bost.futures.model.enums.TimeInterval;

public class SubCandlestickEvent {

    public static void main(String[] args) {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        String symbol = "BTC_USDT";
        client.subscribeCandlestick(symbol, TimeInterval.ONE_MINUTE, (x) -> {
            x.forEach(System.out::println);
            // ...
        });
        // client.unsubscribeCandlestick(symbol,TimeInterval.ONE_MINUTE);
    }

}
