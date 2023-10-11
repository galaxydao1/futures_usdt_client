package com.bost.example.trade;

import com.bost.futures.BostFuturesUSDTClient;
import com.bost.futures.Pager;
import com.bost.futures.model.PositionUpdate;

public class GetPositionUpdates {

    public static void main(String[] args) throws Throwable {
        String apiKey = "834542863b26007e21025sadasd112312155d0b6";
        String secretKey = "1f7e7b9a7b45c45cca768c9198d97e0252fd64ab";
        BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
        Pager<PositionUpdate> positionEntries = client.getPositionUpdates("ETH_USDT", 1, 10);
        System.out.println(positionEntries);
    }

}
