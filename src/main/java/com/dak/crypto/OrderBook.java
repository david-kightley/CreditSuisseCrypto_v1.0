package com.dak.crypto;

import com.dak.crypto.Side;
import com.dak.crypto.order.GroupedOrder;
import com.dak.crypto.order.Order;
import com.dak.crypto.util.OutputFormatter;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook {
    class OrderComparator implements Comparator<BigDecimal> {

        private final boolean ascending;

        OrderComparator(Side side) {
            ascending = (side == Side.SELL);
        }

        public int compare(BigDecimal o1, BigDecimal o2) {
            return (ascending ? 1 : -1) * o1.compareTo(o2);
        }
    }

    private static final int DEFAULT_MAX_DEPTH = 10;

    private final Side bookSide;
    private final Map<BigDecimal, Order> internalOrderBook;

    public OrderBook(Side side) {
        this.bookSide = side;
        this.internalOrderBook = new TreeMap<BigDecimal, Order>(new OrderComparator(side));
    }

    public void submitOrder(Order order) {
        if (order.getSide() != bookSide) {
            throw new RuntimeException("Order submitted to wrong book side");
        }

        BigDecimal price = order.getPrice();
        synchronized (internalOrderBook) {
            Order existingOrder = internalOrderBook.get(price);
            if (existingOrder == null) {
                internalOrderBook.put(price, order);
            } else {
                if (existingOrder instanceof GroupedOrder) {
                    ((GroupedOrder) existingOrder).addOrder(order);
                } else {
                    internalOrderBook.put(price, new GroupedOrder(existingOrder, order));
                }
            }
        }
    }

    public void removeOrder(Order order) {
        BigDecimal price = order.getPrice();
        synchronized (internalOrderBook) {
            Order originalOrder = internalOrderBook.get(price);
            if (originalOrder != null) {
                if (originalOrder instanceof GroupedOrder) {
                    GroupedOrder orderGroup = (GroupedOrder) originalOrder;
                    // Is this the only e try in the group
                    if (orderGroup.removeOrder(order)) {
                        internalOrderBook.remove(price);
                    }
                } else if (originalOrder.equals(order)) {
                    internalOrderBook.remove(price);
                }
            }

        }
    }

    public String[] getOrderSummary() {
        return getOrderSummary(DEFAULT_MAX_DEPTH);
    }

    public String[] getOrderSummary(int length) {
        synchronized (internalOrderBook) {
            int outputLength = Math.min(length, internalOrderBook.size());
            return internalOrderBook.values().stream().map(o -> OutputFormatter.formatOutput(o)).limit(outputLength).toArray(String[]::new);
        }
    }

    public int getSize() {
        return internalOrderBook.size();
    }

}
