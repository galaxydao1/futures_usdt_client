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

package com.bost.futures;

import com.alibaba.fastjson.JSONObject;
import com.bost.futures.model.Position;
import com.bost.util.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PositionSubscription extends PrivateSubscription<List<Position>> {

    private final Map<String, Position> items = new HashMap<>();

    PositionSubscription(BostFuturesUSDTClient client, Listener<List<Position>> listener) {
        super(client, listener);
    }

    @Override
    String getDataName() {
        return "contract_order";
    }

    @Override
    public List<Position> doDecode(JSONObject json) {
        List<Position> a = new ArrayList<>();
        a.add(JSONUtils.parsePosition(json));
        return a;
    }

    @Override
    void onData(List<Position> data) {
        data.forEach(item -> items.put(item.getSymbol() + item.getSide(), item));
    }

    @Override
    List<Position> getData() {
        List<Position> a = new ArrayList<>(items.values());
        items.clear();
        return a;
    }

}
