package com.bost.example.market;

import com.bost.futures.BostFuturesUSDTClient;

public class GetTime {
    public static void main(String[] args) throws Throwable {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        System.out.println(client.getTime());
    }
}
