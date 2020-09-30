package com.dak.crypto.order;

import com.dak.crypto.Side;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class MarketOrder implements Order {

    private final UUID orderId;
    private final Side side;
    private final BigDecimal quantity;
    private final BigDecimal price;

    public MarketOrder(UUID orderId, Side side, BigDecimal quantity, BigDecimal price) {
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;

        assert(orderId != null && side != null && quantity != null && price != null);
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if(! (o instanceof Order)) return false;
        if (o instanceof CancelOrder) {
            return orderId.equals(((CancelOrder)o).getOriginalOrderId());
        }
        return orderId.equals(((Order)o).getOrderId());
    }

}
