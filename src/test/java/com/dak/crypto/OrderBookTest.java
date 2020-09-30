package com.dak.crypto;

import com.dak.crypto.order.CryptoMarketCancelOrder;
import com.dak.crypto.order.CryptoMarketOrder;
import com.dak.crypto.order.Order;
import com.dak.crypto.util.OrderCreator;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class OrderBookTest {

    private static final String USER_ID = "userId";
    private static final Cryptocurrency C_CCY = Cryptocurrency.LITECOIN;

    @Test
    public void testSubmitOrder() {
        OrderBook ob = new OrderBook(Side.BUY);
        assertEquals(0, ob.getSize());

        Order order = createBuyOrder("1.23",  "3.45");
        ob.submitOrder(order);
        assertEquals(1, ob.getSize());

        order = createBuyOrder("1.24",  "5.231");
        ob.submitOrder(order);
        assertEquals(2, ob.getSize());
    }

    @Test
    public void testOrderGrouping() {
        final String price = "1.2345";

        OrderBook ob = new OrderBook(Side.BUY);
        assertEquals(0, ob.getSize());

        Order order1 = createBuyOrder(price,  "3.45");
        ob.submitOrder(order1);
        assertEquals(1, ob.getSize());

        Order order2 = createBuyOrder(price,  "1.58");
        assertNotSame(order1.getOrderId(), order2.getOrderId());
        assertNotSame(order1.getQuantity(), order2.getQuantity());
        assertEquals(order1.getPrice(), order2.getPrice());

        ob.submitOrder(order2);
        assertEquals(1, ob.getSize());

        // Append a delta to the price
        String priceDx = price + "1";
        Order order3 = createBuyOrder(priceDx,  "9.34");
        assertNotSame(order3.getPrice(), order1.getPrice());

        ob.submitOrder(order3);
        assertEquals(2, ob.getSize());

        ob.submitOrder(createBuyOrder(price,  "1.8"));
        assertEquals(2, ob.getSize());
    }


    @Test(expected = RuntimeException.class)
    public void testSubmissionOfIncorrectOrderToBook() {
        OrderBook ob = new OrderBook(Side.SELL);
        assertEquals(0, ob.getSize());

        Order order1 = createBuyOrder("1.35", "3.45");
        ob.submitOrder(order1);
    }

    @Test
    public void testOrderRemoval() {
        OrderBook ob = new OrderBook(Side.SELL);
        Order order1 = createSellOrder("1.35", "3.45");
        ob.submitOrder(order1);
        Order order2 = createSellOrder("1.36", "4.5");
        ob.submitOrder(order2);

        assertEquals(2, ob.getSize());

        String[] summary = ob.getOrderSummary();
        assertEquals(2, summary.length);
        assertEquals(formatPrice("1.35"), getPriceFromSummary(summary[0]));
        assertEquals(formatPrice("1.36"), getPriceFromSummary(summary[1]));

        Order cancel1 = createCancelOrder(order1);
        Order cancel2 = createCancelOrder(order2);

        ob.removeOrder(cancel1);
        assertEquals(1, ob.getSize());

        summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals(formatPrice("1.36"), getPriceFromSummary(summary[0]));

        // Check that removal of order does not throw an exception
        ob.removeOrder(cancel1);
        assertEquals(1, ob.getSize());

        ob.removeOrder(cancel2);
        assertEquals(0, ob.getSize());

        summary = ob.getOrderSummary();
        assertEquals(0, summary.length);

    }

    @Test
    public void testOrderRemovalWhenGrouped() {
        OrderBook ob = new OrderBook(Side.SELL);
        Order order1 = createSellOrder("1.35", "3.45");
        ob.submitOrder(order1);
        Order order2 = createSellOrder("1.35", "4.5");
        ob.submitOrder(order2);
        Order order3 = createSellOrder("1.35", "2.2");
        ob.submitOrder(order3);

        assertEquals(1, ob.getSize());

        String[] summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals(formatPrice("1.35"), getPriceFromSummary(summary[0]));
        assertEquals("10.15", getQuantityFromSummary(summary[0]));

        Order cancel1 = createCancelOrder(order1);
        Order cancel2 = createCancelOrder(order2);
        Order cancel3 = createCancelOrder(order3);

        ob.removeOrder(cancel1);
        assertEquals(1, ob.getSize());

        summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals(formatPrice("1.35"), getPriceFromSummary(summary[0]));
        assertEquals("6.7", getQuantityFromSummary(summary[0]));

        ob.removeOrder(cancel2);
        assertEquals(1, ob.getSize());
        summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals(formatPrice("1.35"), getPriceFromSummary(summary[0]));
        assertEquals("2.2", getQuantityFromSummary(summary[0]));

        ob.removeOrder(cancel3);
        assertEquals(0, ob.getSize());
        summary = ob.getOrderSummary();
        assertEquals(0, summary.length);
    }


    @Test
    public void testSummaryOutputBuySide() {
        final String[] pxQty1 = {"1.23","4.56"};
        final String[] pxQty2 = {"1.22","4.56"};
        final String[] pxQty3 = {"1.24","4.56"};

        OrderBook ob = new OrderBook(Side.BUY);
        assertEquals(0, ob.getSize());

        ob.submitOrder(createBuyOrder(pxQty1[0], pxQty1[1]));
        assertEquals(1, ob.getSize());
        String[] summary = ob.getOrderSummary();
        assertNotNull(summary);
        assertEquals(1, summary.length);

        ob.submitOrder(createBuyOrder(pxQty2[0], pxQty2[1]));
        assertEquals(2, ob.getSize());
        summary = ob.getOrderSummary();
        assertEquals(2, summary.length);

        ob.submitOrder(createBuyOrder(pxQty3[0], pxQty3[1]));
        assertEquals(3, ob.getSize());
        summary = ob.getOrderSummary();
        assertEquals(3, summary.length);

        // Check ordering - highest first
        assertEquals(formatPrice(pxQty3[0]), getPriceFromSummary(summary[0]));
        assertEquals(formatPrice(pxQty1[0]), getPriceFromSummary(summary[1]));
        assertEquals(formatPrice(pxQty2[0]), getPriceFromSummary(summary[2]));
    }

    @Test
    public void testSummaryOutputSellSide() {
        final String[] pxQty1 = {"1.23","4.56"};
        final String[] pxQty2 = {"1.22","4.56"};
        final String[] pxQty3 = {"1.24","4.56"};

        OrderBook ob = new OrderBook(Side.SELL);
        assertEquals(0, ob.getSize());

        ob.submitOrder(createSellOrder(pxQty1[0], pxQty1[1]));
        assertEquals(1, ob.getSize());
        String[] summary = ob.getOrderSummary();
        assertNotNull(summary);
        assertEquals(1, summary.length);

        ob.submitOrder(createSellOrder(pxQty2[0], pxQty2[1]));
        assertEquals(2, ob.getSize());
        summary = ob.getOrderSummary();
        assertEquals(2, summary.length);

        ob.submitOrder(createSellOrder(pxQty3[0], pxQty3[1]));
        assertEquals(3, ob.getSize());
        summary = ob.getOrderSummary();
        assertEquals(3, summary.length);

        // Check ordering - lowest first
        assertEquals(formatPrice(pxQty2[0]), getPriceFromSummary(summary[0]));
        assertEquals(formatPrice(pxQty1[0]), getPriceFromSummary(summary[1]));
        assertEquals(formatPrice(pxQty3[0]), getPriceFromSummary(summary[2]));
    }

    @Test
    public void testNettingOfQuantitiesInSummary() {
        OrderBook ob = new OrderBook(Side.BUY);
        assertEquals(0, ob.getSize());
        assertEquals(0, ob.getOrderSummary().length);

        final String price = "5.34";

        String qty = "2.5";
        ob.submitOrder(createBuyOrder(price, qty));
        assertEquals(1, ob.getSize());
        String[] summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals(qty, getQuantityFromSummary(summary[0]));

        ob.submitOrder(createBuyOrder(price, qty));
        summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals("5.0", getQuantityFromSummary(summary[0]));

        ob.submitOrder(createBuyOrder(price, "2.3"));
        summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals("7.3", getQuantityFromSummary(summary[0]));

        ob.submitOrder(createBuyOrder(price, "0.7"));
        summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals("8.0", getQuantityFromSummary(summary[0]));

        ob.submitOrder(createBuyOrder(price, "7.24"));
        summary = ob.getOrderSummary();
        assertEquals(1, summary.length);
        assertEquals("15.24", getQuantityFromSummary(summary[0]));
    }

    @Test
    public void testLimitSummaryOutputLength() {
        OrderBook ob = new OrderBook(Side.SELL);
        assertEquals(0, ob.getSize());
        assertEquals(0, ob.getOrderSummary(3).length);

        ob.submitOrder(createSellOrder("4.31", "5.67"));
        assertEquals(1, ob.getSize());
        assertEquals(1, ob.getOrderSummary(3).length);

        ob.submitOrder(createSellOrder("4.32", "5.67"));
        assertEquals(2, ob.getSize());
        assertEquals(2, ob.getOrderSummary(3).length);

        ob.submitOrder(createSellOrder("4.33", "5.67"));
        assertEquals(3, ob.getSize());
        assertEquals(3, ob.getOrderSummary(3).length);

        ob.submitOrder(createSellOrder("4.34", "5.67"));
        assertEquals(4, ob.getSize());
        assertEquals(3, ob.getOrderSummary(3).length);

        ob.submitOrder(createSellOrder("4.35", "5.67"));
        assertEquals(5, ob.getSize());
        String[] summary = ob.getOrderSummary(3);
        assertEquals(3, summary.length);

        assertEquals("£4.31", getPriceFromSummary(summary[0]));
        assertEquals("£4.32", getPriceFromSummary(summary[1]));
        assertEquals("£4.33", getPriceFromSummary(summary[2]));

        ob.submitOrder(createSellOrder("4.29", "5.67"));
        assertEquals(6, ob.getSize());
        summary = ob.getOrderSummary(3);
        assertEquals(3, summary.length);

        assertEquals("£4.29", getPriceFromSummary(summary[0]));
        assertEquals("£4.31", getPriceFromSummary(summary[1]));
        assertEquals("£4.32", getPriceFromSummary(summary[2]));

        ob.submitOrder(createSellOrder("4.303", "5.67"));
        assertEquals(7, ob.getSize());
        summary = ob.getOrderSummary(3);
        assertEquals(3, summary.length);

        assertEquals("£4.29", getPriceFromSummary(summary[0]));
        assertEquals("£4.303", getPriceFromSummary(summary[1]));
        assertEquals("£4.31", getPriceFromSummary(summary[2]));

        ob.submitOrder(createSellOrder("4.36", "5.67"));
        assertEquals(8, ob.getSize());
        summary = ob.getOrderSummary(3);
        assertEquals(3, summary.length);

        assertEquals("£4.29", getPriceFromSummary(summary[0]));
        assertEquals("£4.303", getPriceFromSummary(summary[1]));
        assertEquals("£4.31", getPriceFromSummary(summary[2]));

        ob.submitOrder(createSellOrder("4.26", "5.67"));
        ob.submitOrder(createSellOrder("4.27", "5.67"));
        ob.submitOrder(createSellOrder("4.28", "5.67"));
        assertEquals(11, ob.getSize());

        summary = ob.getOrderSummary(3);
        assertEquals(3, summary.length);

        assertEquals("£4.26", getPriceFromSummary(summary[0]));
        assertEquals("£4.27", getPriceFromSummary(summary[1]));
        assertEquals("£4.28", getPriceFromSummary(summary[2]));

        summary = ob.getOrderSummary(5);
        assertEquals(5, summary.length);

        assertEquals("£4.26", getPriceFromSummary(summary[0]));
        assertEquals("£4.27", getPriceFromSummary(summary[1]));
        assertEquals("£4.28", getPriceFromSummary(summary[2]));
        assertEquals("£4.29", getPriceFromSummary(summary[3]));
        assertEquals("£4.303", getPriceFromSummary(summary[4]));

        summary = ob.getOrderSummary(8);
        assertEquals(8, summary.length);

        assertEquals("£4.31", getPriceFromSummary(summary[5]));
        assertEquals("£4.32", getPriceFromSummary(summary[6]));
        assertEquals("£4.33", getPriceFromSummary(summary[7]));

        // Default = 10
        summary = ob.getOrderSummary();
        assertEquals(10, summary.length);

        assertEquals("£4.26", getPriceFromSummary(summary[0]));
        assertEquals("£4.27", getPriceFromSummary(summary[1]));
        assertEquals("£4.28", getPriceFromSummary(summary[2]));
        assertEquals("£4.29", getPriceFromSummary(summary[3]));
        assertEquals("£4.303", getPriceFromSummary(summary[4]));
        assertEquals("£4.31", getPriceFromSummary(summary[5]));
        assertEquals("£4.32", getPriceFromSummary(summary[6]));
        assertEquals("£4.33", getPriceFromSummary(summary[7]));
        assertEquals("£4.34", getPriceFromSummary(summary[8]));
        assertEquals("£4.35", getPriceFromSummary(summary[9]));
    }


    private CryptoMarketOrder createBuyOrder(String price, String quantity) {
        return OrderCreator.createCryptoMarketOrder(C_CCY, USER_ID, Side.BUY, new BigDecimal(quantity), new BigDecimal(price));
    }

    private CryptoMarketOrder createSellOrder(String price, String quantity) {
        return OrderCreator.createCryptoMarketOrder(C_CCY, USER_ID, Side.SELL, new BigDecimal(quantity), new BigDecimal(price));
    }

    private CryptoMarketCancelOrder createCancelOrder(Order origOrder) {
        return OrderCreator.createCryptoMarketCancelOrder(origOrder.getOrderId(), C_CCY, origOrder.getSide(), origOrder.getPrice());
    }


    private String getPriceFromSummary(String summary) {
        return summary.split(" ")[2];
    }

    private String getQuantityFromSummary(String summary) {
        return summary.split(" ")[0];
    }

    private String formatPrice(String price) {
        return "£" + price;
    }

}
