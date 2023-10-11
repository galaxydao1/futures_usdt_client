package com.bost.example.trade;

import com.bost.futures.BostFuturesUSDTClient;
import com.bost.futures.model.enums.MarginMode;
import com.bost.futures.model.enums.TradeSide;

public class ChangeMargin {

    public static void main(String[] args) throws Throwable {
        String apiKey = "834542863b26007e21025sadasd112312155d0b6";
        String secretKey = "1f7e7b9a7b45c45cca768c9198d97e0252fd64ab";
        BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
//        changeMarginMode(client);
         changeMargeLeverage(client);
    }

    private static void changeMarginMode(BostFuturesUSDTClient client) throws Throwable {
        client.changeMarginMode("BTC_USDT", MarginMode.CROSS);
        System.out.println("changeMarginMode succ");
    }

    private static void changeMargeLeverage(BostFuturesUSDTClient client) throws Throwable {
        client.changeLeverage("BTC_USDT", TradeSide.LONG, 49);
        System.out.println("changeMarginMode succ");
    }

}
