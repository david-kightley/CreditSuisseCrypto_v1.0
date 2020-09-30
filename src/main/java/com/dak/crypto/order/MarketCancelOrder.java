package com.dak.crypto.order;

import com.dak.crypto.Side;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class MarketCancelOrder implements Order,CancelOrder {

    private final UUID orderId;
    private final UUID originalOrderId;
    private final Side side;
    private final BigDecimal originalOrderPrice;

    public MarketCancelOrder(UUID orderId, UUID originalOrderId, Side side, BigDecimal price) {
        this.orderId = orderId;
        this.originalOrderId = originalOrderId;
        this.side = side;
        this.originalOrderPrice = price;

        assert(orderId != null && originalOrderId != null && side != null && originalOrderPrice != null);
    }

    @Override
    public UUID getOrderId() {
        return orderId;
    }

    @Override
    public UUID getOriginalOrderId() {
        return originalOrderId;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public BigDecimal getQuantity() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof Order)) return false;
        UUID otherId = ((Order) o).getOrderId();
        if (o instanceof CancelOrder) {
            return orderId.equals(otherId);
        }
        return originalOrderId.equals(otherId);
    }

    @Override
    public BigDecimal getPrice() {
        return originalOrderPrice;
    }

}
