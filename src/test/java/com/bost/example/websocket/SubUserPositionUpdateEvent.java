package com.bost.example.websocket;

import com.bost.futures.BostFuturesUSDTClient;

public class SubUserPositionUpdateEvent {

    public static void main(String[] args) {
        String apiKey = "your apiKey";
        String secretKey = "your secretKey";
        BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
        client.subscribePositionUpdate(x -> {
            x.forEach(System.out::println);
            // ...
        });
    }

}
