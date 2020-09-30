package com.dak.crypto.order;

import com.dak.crypto.Cryptocurrency;
import com.dak.crypto.Side;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CryptoMarketOrder extends MarketOrder implements CryptoOrder {

    private final String userId;
    private final Cryptocurrency coinType;

    public CryptoMarketOrder(UUID orderId, String userId, Cryptocurrency coinType,
                             Side side, BigDecimal quantity, BigDecimal price) {
        super(orderId, side, quantity, price);
        this.coinType = coinType;
        this.userId = userId;

        assert(coinType != null && userId != null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, coinType);
    }

    public String getUserId() {
        return userId;
    }

    public Cryptocurrency getCoinType() {
        return coinType;
    }
}
