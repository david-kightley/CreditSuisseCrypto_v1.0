package com.dak.crypto;

import com.dak.crypto.order.CryptoMarketCancelOrder;
import com.dak.crypto.order.CryptoMarketOrder;
import com.dak.crypto.order.CryptoOrder;
import com.dak.crypto.order.Order;
import com.dak.crypto.util.OrderCreator;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class OrderBoardTest {

    private static final Cryptocurrency C_CCY = Cryptocurrency.ETHERIUM;

    @Test
    public void testSubmitToCorrectSide() {
        OrderBoard ob = new OrderBoard(C_CCY);

        CryptoOrder buyOrder = createOrder(Side.BUY, "1.23", "2.4");
        CryptoOrder sellOrder = createOrder(Side.SELL, "1.23", "2.4");
        CryptoOrder cancelBuyOrder = createCancelOrder(buyOrder);
        CryptoOrder cancelSellOrder = createCancelOrder(sellOrder);

        String[] summary = ob.getOrderSummary(Side.BUY);
        assertNotNull(summary);
        assertEquals(0, summary.length);

        summary = ob.getOrderSummary(Side.SELL);
        assertNotNull(summary);
        assertEquals(0, summary.length);

        ob.submitOrder(buyOrder);
        assertEquals(1, ob.getOrderSummary(Side.BUY).length);
        assertEquals(0, ob.getOrderSummary(Side.SELL).length);

        ob.submitOrder(sellOrder);
        assertEquals(1, ob.getOrderSummary(Side.BUY).length);
        assertEquals(1, ob.getOrderSummary(Side.SELL).length);

        ob.submitOrder(cancelSellOrder);
        assertEquals(1, ob.getOrderSummary(Side.BUY).length);
        assertEquals(0, ob.getOrderSummary(Side.SELL).length);

        ob.submitOrder(cancelBuyOrder);
        assertEquals(0, ob.getOrderSummary(Side.BUY).length);
        assertEquals(0, ob.getOrderSummary(Side.SELL).length);

        ob.submitOrder(sellOrder);
        assertEquals(0, ob.getOrderSummary(Side.BUY).length);
        assertEquals(1, ob.getOrderSummary(Side.SELL).length);
    }

    @Test(expected = RuntimeException.class)
    public void submitOrderToWrongOrderBoard() {
        OrderBoard ob = new OrderBoard(Cryptocurrency.TETHER);
        CryptoMarketOrder buyOrder = createOrder(Side.BUY, "1.23", "2.4");
        assertNotSame(Cryptocurrency.TETHER, buyOrder.getCoinType());

        ob.submitOrder(buyOrder);
    }

    private CryptoMarketOrder createOrder(Side side, String price, String quantity) {
        return OrderCreator.createCryptoMarketOrder(C_CCY, "userId", side, new BigDecimal(quantity), new BigDecimal(price));
    }

    private CryptoMarketCancelOrder createCancelOrder(Order origOrder) {
        return OrderCreator.createCryptoMarketCancelOrder(origOrder.getOrderId(), C_CCY, origOrder.getSide(), origOrder.getPrice());
    }


}
