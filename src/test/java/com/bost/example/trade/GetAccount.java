package com.bost.example.trade;

import com.bost.futures.BostFuturesUSDTClient;

public class GetAccount {

    public static void main(String[] args) throws Throwable {
        String apiKey = "834542863b26007e21025sadasd112312155d0b6";
        String secretKey = "1f7e7b9a7b45c45cca768c9198d97e0252fd64ab";
        BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
        System.out.println(client.getAccount("USDT"));
        // System.out.println(client.getAccounts());
    }

}
