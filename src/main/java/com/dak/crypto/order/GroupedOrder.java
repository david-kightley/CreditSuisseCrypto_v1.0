package com.dak.crypto.order;

import com.dak.crypto.Side;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GroupedOrder implements Order {

    private final List<Order> orders;

    public GroupedOrder(Order order1, Order order2) {
        orders = new LinkedList<>();
        orders.add(order1);
        orders.add(order2);
    }

    public UUID getOrderId() {
        return null;
    }

    public Side getSide() {
        return orders.get(0).getSide();
    }

    public BigDecimal getQuantity() {
        return orders.stream().map(o -> o.getQuantity()).reduce(new BigDecimal(0), (a,b) -> a.add(b));
    }

    public BigDecimal getPrice() {
        return orders.get(0).getPrice();
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    /**
     *
     * @param order
     * @return true if this group is now empty
     */
    public boolean removeOrder(Order order) {
        orders.remove(order);
        return (orders.size() == 0);
    }
}
