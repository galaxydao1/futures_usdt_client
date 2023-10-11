package com.bost.example.websocket;

import com.bost.futures.BostFuturesUSDTClient;

public class SubTradeEvent {

    public static void main(String[] args) {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        String symbol = "BTC_USDT";
        client.subscribeTrade(symbol, (x) -> {
            x.forEach(System.out::println);
            // ...
        });
        // client.unSubscribeTradeEvent(symbol);
    }

}
