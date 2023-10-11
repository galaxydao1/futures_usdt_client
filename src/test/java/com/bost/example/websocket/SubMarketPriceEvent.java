package com.bost.example.websocket;

import com.bost.futures.BostFuturesUSDTClient;

public class SubMarketPriceEvent {

    public static void main(String[] args) {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        String symbol = "BTC_USDT";
        client.subscribeMarketPrice(symbol, x -> {
            x.forEach(System.out::println);
            // ...
        });
        // client.unSubscribeMarketPriceEvent(symbol);
    }

}
