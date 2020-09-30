package com.dak.crypto.order;

import com.dak.crypto.Cryptocurrency;
import com.dak.crypto.Side;

import java.math.BigDecimal;
import java.util.UUID;

public class CryptoMarketCancelOrder extends MarketCancelOrder implements CryptoOrder {

    private final Cryptocurrency coinType;

    public CryptoMarketCancelOrder(UUID orderId, UUID originalOrderId, Cryptocurrency coinType, Side side, BigDecimal price) {
        super(orderId, originalOrderId, side, price);
        this.coinType = coinType;

        assert(coinType != null);
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public Cryptocurrency getCoinType() {
        return coinType;
    }

}
