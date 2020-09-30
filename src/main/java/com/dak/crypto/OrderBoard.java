package com.dak.crypto;

import com.dak.crypto.order.CancelOrder;
import com.dak.crypto.order.CryptoOrder;
import com.dak.crypto.order.Order;

public class OrderBoard {

    private final OrderBook buySideOrderBook;
    private final OrderBook sellSideorderBook;
    private final Cryptocurrency coinType;

    public OrderBoard(Cryptocurrency type) {
        this.coinType = type;
        // Initialise the OrderBooks
        this.buySideOrderBook = new OrderBook(Side.BUY);
        this.sellSideorderBook = new OrderBook(Side.SELL);
    }

    public void submitOrder(CryptoOrder order) {
        if (order.getCoinType() != this.coinType) {
            throw new RuntimeException(String.format("Invalid order submitted to OrderBoard - Expected: %s, Actual: %s", this.coinType, order.getCoinType()));
        }

        if (order instanceof CancelOrder) {
            removeOrder(order);
        } else {
            addOrder(order);
        }
    }

    private void addOrder(Order order) {
        getOrderBook(order.getSide()).submitOrder(order);
    }

    private void removeOrder(Order order) {
        getOrderBook(order.getSide()).removeOrder(order);
    }

    private OrderBook getOrderBook(Side side) {
        return side == Side.BUY ? buySideOrderBook : sellSideorderBook;
    }

    public String[] getOrderSummary(Side side) {
        return getOrderBook(side).getOrderSummary();
    }

    public String[] getOrderSummary(Side side, int maxDepth) {
        return getOrderBook(side).getOrderSummary(maxDepth);
    }

}
