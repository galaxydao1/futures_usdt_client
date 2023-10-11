package com.bost.example.market;

import com.bost.futures.BostFuturesUSDTClient;

public class GetContractInfo {
    public static void main(String[] args) throws Throwable {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        System.out.println(client.getContractInfos());
        // System.out.println(client.getContractInfo("BTC_USDT"));
    }
}
