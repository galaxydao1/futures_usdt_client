<h1 align="center">Welcome to Bost Futures USDT Client 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-v1.0.0-blue.svg?cacheSeconds=2592000" />
  <a href="#" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg" />
  </a>
</p>

> Bost平台U本位合约Java版本SDK

### 🏠 [Homepage](https://www.trade-planets.com/en/futures)

## Dependency

```sh
需要 jdk1.8+
```

## Usage

```sh
// 如果出现网络问题,你可能需要代理或者重设client的相关地址参数

// 公开的api 获取kline
BostFuturesUSDTUSDTClient client = new BostFuturesUSDTClient();
List<Candlestick> res = client.getCandlestick("BTC_USDT", CandlestickInterval.WEEKLY,10);
System.out.println(res);
        
// 用户的api 转入合约账户
String apiKey = "use your apiKey";
String secretKey = "use your secretKey";
BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
client.transferOut("ETH", BigDecimal.valueOf(0.0001));
client.transferIn("ETH", BigDecimal.valueOf(0.0001));
        
// 用户的api 下单
String apiKey = "use your apiKey";
String secretKey = "use your secretKey";
BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
LimitOrder limitOrder = new LimitOrder();
limitOrder.setSymbol("ETH_USDT");
limitOrder.setQuantity(BigDecimal.valueOf(0.01));
limitOrder.setAction(TradeAction.EXIT);
limitOrder.setSide(TradeSide.LONG);
limitOrder.setPrice(BigDecimal.valueOf(1648));
String orderId = client.placeOrder(limitOrder);
System.out.println("the limit order_id: " + orderId);
        
// 公开的订阅 订阅kline
BostFuturesUSDTClient client = new BostFuturesUSDTClient();
String symbol = "BTC_USDT";
client.subscribeCandlestick(symbol, TimeInterval.ONE_MINUTE, (x) -> {
    x.forEach(System.out::println);
    // ...
});
        
// 用户的订阅 用户资产数据
String apiKey = "use your apiKey";
String secretKey = "use your secretKey";
BostFuturesUSDTClient client = new BostFuturesUSDTClient(apiKey, secretKey);
client.subscribeAccount(x -> {
     x.forEach(System.out::println);
     // ...
});
        
// 更多的可以参考测试用例
```

## Author

👤 **galaxydao1**

* Website: https://github.com/galaxydao1
* Github: [@galaxydao1](https://github.com/galaxydao1)

## Show your support

Give a ⭐️ if this project helped you!


