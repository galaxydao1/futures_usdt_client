/*
 * Copyright (C) 2020, Bost.com. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.bost.futures.model;

import com.alibaba.fastjson.JSONObject;
import com.bost.futures.model.enums.MarginMode;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString
public class Account {

    // 资产名称
    private String asset;

    // 可用余额
    private BigDecimal available;

    // 全仓保证金
    private MarginAccount cross;

    // 逐仓保证金
    private MarginAccount isolated;

    @ToString
    public static class MarginAccount {

        // 仓位保证金
        private BigDecimal positionMargin;

        // 委托保证金
        private BigDecimal orderMargin;

        // 仓位模式
        private MarginMode mode;
    }



    public static Account parseResult(JSONObject obj) {
        Account a = new Account();
        a.setAsset(obj.getString("c"));
        a.setAvailable(obj.getBigDecimal("b"));
        a.cross = new MarginAccount();
        a.isolated = new MarginAccount();
        a.cross.positionMargin = obj.getBigDecimal("mc");
        a.isolated.positionMargin = obj.getBigDecimal("mf");
        a.cross.orderMargin = obj.getBigDecimal("fc");
        a.isolated.orderMargin = obj.getBigDecimal("ff");
        a.isolated.mode = MarginMode.ISOLATED;
        a.cross.mode = MarginMode.CROSS;
        return a;
    }

}
