package com.dak.crypto.util;

import com.dak.crypto.Cryptocurrency;
import com.dak.crypto.Side;
import com.dak.crypto.order.CryptoMarketCancelOrder;
import com.dak.crypto.order.CryptoMarketOrder;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderCreator {

    public static CryptoMarketOrder createCryptoMarketOrder(Cryptocurrency crypto,
                                                            String userId,
                                                            Side side,
                                                            BigDecimal quantity,
                                                            BigDecimal price) {
        return new CryptoMarketOrder(UUID.randomUUID(), userId, crypto, side, quantity, price);
    }

    public static CryptoMarketCancelOrder createCryptoMarketCancelOrder(UUID originalOrderId,
                                                                        Cryptocurrency crypto,
                                                                        Side side,
                                                                        BigDecimal price) {
        return new CryptoMarketCancelOrder(UUID.randomUUID(), originalOrderId, crypto, side, price);
    }



}
