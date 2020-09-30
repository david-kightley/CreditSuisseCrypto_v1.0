package com.dak.crypto;

import com.dak.crypto.order.CryptoMarketCancelOrder;
import com.dak.crypto.order.CryptoMarketOrder;
import com.dak.crypto.order.CryptoOrder;
import com.dak.crypto.util.OrderCreator;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CryptoMarketTest {

    class Pair<C,S> {
        C p1;
        S p2;

        Pair(C o1, S o2) {
            p1 = o1;
            p2 = o2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(p1, pair.p1) &&
                    Objects.equals(p2, pair.p2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(p1, p2);
        }
    }

    @Test
    public void testSubmissionToCorrectOrderBoard() {
        CryptoMarket market = new CryptoMarket();

        Set<Pair<Cryptocurrency,Side>> ordersPresent = new HashSet<>();
        int orderCount = validateMarket(market, ordersPresent);
        assertEquals(0, orderCount);

        CryptoOrder etherBuyOrder = createOrder(Cryptocurrency.ETHERIUM, Side.BUY, "1.23", "2.4");
        CryptoOrder bitSellOrder = createOrder(Cryptocurrency.BITCOIN, Side.SELL, "1.23", "2.4");
        CryptoOrder tethBuyOrder = createOrder(Cryptocurrency.TETHER, Side.BUY, "1.23", "2.4");
        CryptoOrder xrpSellOrder = createOrder(Cryptocurrency.XRP, Side.SELL, "1.23", "2.4");

        market.submitOrder(etherBuyOrder);
        ordersPresent.add(new Pair<>(etherBuyOrder.getCoinType(), etherBuyOrder.getSide()));
        assertEquals(1, validateMarket(market, ordersPresent));

        market.submitOrder(bitSellOrder);
        ordersPresent.add(new Pair<>(bitSellOrder.getCoinType(), bitSellOrder.getSide()));
        assertEquals(2, validateMarket(market, ordersPresent));

        market.submitOrder(tethBuyOrder);
        ordersPresent.add(new Pair<>(tethBuyOrder.getCoinType(), tethBuyOrder.getSide()));
        assertEquals(3, validateMarket(market, ordersPresent));

        market.submitOrder(xrpSellOrder);
        ordersPresent.add(new Pair<>(xrpSellOrder.getCoinType(), xrpSellOrder.getSide()));
        assertEquals(4, validateMarket(market, ordersPresent));

        CryptoOrder etherBuyCancelOrder = createCancelOrder(etherBuyOrder);
        CryptoOrder bitSellCancelOrder = createCancelOrder(bitSellOrder);
        CryptoOrder tethBuyCancelOrder = createCancelOrder(tethBuyOrder);
        CryptoOrder xrpSellCancelOrder = createCancelOrder(xrpSellOrder);

        market.submitOrder(tethBuyCancelOrder);
        ordersPresent.remove(new Pair<>(tethBuyOrder.getCoinType(), tethBuyOrder.getSide()));
        assertEquals(3, validateMarket(market, ordersPresent));

        market.submitOrder(etherBuyCancelOrder);
        ordersPresent.remove(new Pair<>(etherBuyOrder.getCoinType(), etherBuyOrder.getSide()));
        assertEquals(2, validateMarket(market, ordersPresent));

        market.submitOrder(bitSellCancelOrder);
        ordersPresent.remove(new Pair<>(bitSellOrder.getCoinType(), bitSellOrder.getSide()));
        assertEquals(1, validateMarket(market, ordersPresent));

        market.submitOrder(xrpSellCancelOrder);
        ordersPresent.remove(new Pair<>(xrpSellOrder.getCoinType(), xrpSellOrder.getSide()));
        assertEquals(0, validateMarket(market, ordersPresent));
    }

    private int validateMarket(CryptoMarket market, Set<Pair<Cryptocurrency,Side>> ordersPresent) {
        String[] summary;
        int expected = 0;
        int totalOrders = 0;
        for(Cryptocurrency cCcy : Cryptocurrency.values()) {
            for(Side side : Side.values()) {
                Pair<Cryptocurrency,Side> pair = new Pair<>(cCcy, side);
                expected = ordersPresent.contains(pair) ? 1 : 0;

                summary = market.getOrderSummary(cCcy, side);
                assertNotNull(summary);
                assertEquals(expected, summary.length);
                totalOrders += expected;
            }
        }
        return totalOrders;
    }

    private CryptoMarketOrder createOrder(Cryptocurrency cCcy, Side side, String price, String quantity) {
        return OrderCreator.createCryptoMarketOrder(cCcy, "userId", side, new BigDecimal(quantity), new BigDecimal(price));
    }

    private CryptoMarketCancelOrder createCancelOrder(CryptoOrder origOrder) {
        return OrderCreator.createCryptoMarketCancelOrder(origOrder.getOrderId(), origOrder.getCoinType(), origOrder.getSide(), origOrder.getPrice());
    }
}
