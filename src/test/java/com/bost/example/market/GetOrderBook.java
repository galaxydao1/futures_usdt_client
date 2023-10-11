package com.bost.example.market;

import com.bost.futures.BostFuturesUSDTClient;
import com.bost.futures.model.OrderBook;

public class GetOrderBook {
    public static void main(String[] args) throws Throwable {
        BostFuturesUSDTClient client = new BostFuturesUSDTClient();
        OrderBook orderBook = client.getOrderBook("BTC_USDT");

        // ask1->askN
        orderBook.getAskBook().iterator().forEachRemaining(priceLevel ->
                System.out.println("the ask: " + priceLevel)
        );
        // bid1->bidN
        orderBook.getBidBook().iterator().forEachRemaining(priceLevel ->
                System.out.println("the bid: " + priceLevel)
        );

        // ...
    }
}
