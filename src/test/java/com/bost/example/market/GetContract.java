package com.bost.example.market;

import com.bost.futures.BostFuturesUSDTClient;

public class GetContract {
    public static void main(String[] args) throws Throwable {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        System.out.println(client.getContract("BTC_USDT"));
        System.out.println(client.getContracts());
    }
}
