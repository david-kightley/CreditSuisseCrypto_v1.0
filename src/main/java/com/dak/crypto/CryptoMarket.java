package com.dak.crypto;

import com.dak.crypto.order.CryptoOrder;

import java.util.HashMap;

public class CryptoMarket extends HashMap<Cryptocurrency, OrderBoard> {

    public CryptoMarket() {
        // Initialize markets
        for (Cryptocurrency crypto : Cryptocurrency.values()) {
            put(crypto, new OrderBoard(crypto));
        }
    }

    public void submitOrder(CryptoOrder order) {
        get(order.getCoinType()).submitOrder(order);
    }

    public String[] getOrderSummary(Cryptocurrency crypto, Side side) {
        return get(crypto).getOrderSummary(side);
    }
}
