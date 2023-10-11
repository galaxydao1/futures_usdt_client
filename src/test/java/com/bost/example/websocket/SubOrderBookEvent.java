package com.bost.example.websocket;

import com.bost.futures.BostFuturesUSDTClient;

public class SubOrderBookEvent {

    public static void main(String[] args) {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        String symbol = "BTC_USDT";
        client.subscribeOrderBook(symbol, x -> {
            System.out.println(x.getAskBook().getPriceLevel(0));
            System.out.println(x.getBidBook().getPriceLevel(0));
            // ...
        });
        // client.unSubscribeOrderBookEvent(symbol);
    }

}
