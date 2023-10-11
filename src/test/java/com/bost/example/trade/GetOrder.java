package com.bost.example.trade;

import com.bost.futures.BostFuturesUSDTClient;
import com.bost.futures.OrderIdSet;
import com.bost.futures.OrderQuery;
import com.bost.futures.Pager;
import com.bost.futures.model.Order;
import com.bost.futures.model.enums.TradeAction;
import com.bost.futures.model.enums.TradeSide;

import java.util.List;

public class GetOrder {

    public static void main(String[] args) throws Throwable {
        String apiKey = "834542863b26007e21025sadasd112312155d0b6";
        String secretKey = "1f7e7b9a7b45c45cca768c9198d97e0252fd64ab";
        BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
        getOneOrder(client);
        getOrders(client);
        getOpenOrders(client);
        getCloseOrders(client);

    }

    private static void getOpenOrders(BostFuturesUSDTClient client) throws Throwable {
        OrderQuery query = new OrderQuery();
//        query.setSymbol("BTC_USDT");
        query.setPage(1);
        query.setSize(10);
        query.setAction(TradeAction.EXIT);
        query.setSide(TradeSide.LONG);
        Pager<Order> orders = client.getOpenOrders(query);
        System.out.println(orders);
    }

    private static void getCloseOrders(BostFuturesUSDTClient client) throws Throwable {
        OrderQuery query = new OrderQuery();
        // you can set page,size,orderStatus ...
        query.setPage(1);
        query.setSize(10);
        // query.setAction(TradeAction.ENTRY);
        // query.setSide(TradeSide.LONG);
        // query.setStatus(OrderStatus.CANCELED);
        Pager<Order> orders = client.getOrders(query);
        System.out.println(orders);
    }

    private static void getOneOrder(BostFuturesUSDTClient client) throws Throwable {
        System.out.println(client.getOrder("24189263983888"));
    }

    private static void getOrders(BostFuturesUSDTClient client) throws Throwable {
        OrderIdSet idSet = new OrderIdSet();
        idSet.add("8796093026216");
        // idSet.add(...)
        List<Order> orders = client.getOrders(idSet);
        orders.forEach(System.out::println);
    }

}
