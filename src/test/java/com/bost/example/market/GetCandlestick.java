package com.bost.example.market;

import com.bost.futures.BostFuturesUSDTClient;
import com.bost.futures.model.Candlestick;
import com.bost.futures.model.enums.TimeInterval;

import java.util.List;

public class GetCandlestick {
    public static void main(String[] args) throws Throwable {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        List<Candlestick> haveLimit = client.getCandlestick("BTC_USDT",
                TimeInterval.WEEKLY, 10);
        List<Candlestick> noLimit = client.getCandlestick("BTC_USDT", TimeInterval.DAILY);
        System.out.println(haveLimit);
        System.out.println(noLimit);
    }
}
