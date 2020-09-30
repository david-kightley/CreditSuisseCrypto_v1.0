package com.dak.crypto.util;

import com.dak.crypto.Cryptocurrency;
import com.dak.crypto.Side;
import com.dak.crypto.order.CryptoMarketCancelOrder;
import com.dak.crypto.order.CryptoMarketOrder;
import com.dak.crypto.order.CryptoOrder;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

public class OrderCreatorTest {
    private final Cryptocurrency crypto = Cryptocurrency.ETHERIUM;
    private final String userId = "UserName";
    private final Side side = Side.BUY;
    private final BigDecimal quantity = new BigDecimal(1.25);
    private final BigDecimal price = new BigDecimal(98.76);
    private final UUID givenOrderId = UUID.randomUUID();


    @Test
    public void testCryptoMarketOrderCreationWithNullParameters() {
        runFailureTestForMarketOrder(null, userId, side, quantity, price);
        runFailureTestForMarketOrder(crypto, null, side, quantity, price);
        runFailureTestForMarketOrder(crypto, userId, null, quantity, price);
        runFailureTestForMarketOrder(crypto, userId, side, null, price);
        runFailureTestForMarketOrder(crypto, userId, side, quantity, null);
    }

    private void runFailureTestForMarketOrder(Cryptocurrency crypto,
                                              String userId,
                                              Side side,
                                              BigDecimal quantity,
                                              BigDecimal price) {
        try {
            OrderCreator.createCryptoMarketOrder(crypto, userId, side, quantity, price);
            fail("Assertion failed to report null parameter");
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    public void testCryptoMarketOrderCreationWithaValidParameters() {
        CryptoMarketOrder order = OrderCreator.createCryptoMarketOrder(crypto, userId, side, quantity, price);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertNotSame(givenOrderId, order.getOrderId());
        assertEquals(crypto, order.getCoinType());
        assertEquals(userId, order.getUserId());
        assertEquals(side, order.getSide());
        assertEquals(quantity, order.getQuantity());
        assertEquals(price, order.getPrice());
    }

    @Test
    public void testCryptoMarketCancelOrderCreationWithNullParameters() {
        runFailureTestForMarketCancel(null, crypto, side, price);
        runFailureTestForMarketCancel(givenOrderId, null, side, price);
        runFailureTestForMarketCancel(givenOrderId, crypto, null, price);
        runFailureTestForMarketCancel(givenOrderId, crypto, side, null);
    }

    private void runFailureTestForMarketCancel(UUID originalOrderId,
                                               Cryptocurrency crypto,
                                               Side side,
                                               BigDecimal price) {
        try {
            OrderCreator.createCryptoMarketCancelOrder(originalOrderId, crypto, side, price);
            fail("Assertion failed to report null parameter");
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    public void testCryptoMarketCancelOrderCreationWithaValidParameters() {
        CryptoMarketCancelOrder cancelOrder = OrderCreator.createCryptoMarketCancelOrder(givenOrderId, crypto, side, price);
        assertNotNull(cancelOrder);
        assertNotNull(cancelOrder.getOrderId());
        assertNotSame(givenOrderId, cancelOrder.getOrderId());
        assertNotSame(cancelOrder.getOrderId(), cancelOrder.getOriginalOrderId());
        assertEquals(givenOrderId, cancelOrder.getOriginalOrderId());
        assertEquals(crypto, cancelOrder.getCoinType());
        assertEquals(side, cancelOrder.getSide());
        assertEquals(price, cancelOrder.getPrice());
    }

    @Test
    public void testCryptoMarketlOrdersEquality() {
        CryptoMarketOrder order = OrderCreator.createCryptoMarketOrder(crypto, userId, side, quantity, price);
        CryptoMarketCancelOrder cancelOrder = OrderCreator.createCryptoMarketCancelOrder(order.getOrderId(), crypto, side, price);
        TestOrderClass myTestOrder = new TestOrderClass(order.getOrderId());

        assertTrue(order.equals(myTestOrder));
        assertTrue(order.equals(cancelOrder));
        assertTrue(cancelOrder.equals(myTestOrder));
        assertTrue(cancelOrder.equals(order));
    }

  class TestOrderClass implements CryptoOrder {

        private final UUID uuid;
        TestOrderClass(UUID id) {
            this.uuid = id;
        }

      @Override
      public String getUserId() {
          return null;
      }

      @Override
      public Cryptocurrency getCoinType() {
          return null;
      }

      @Override
      public UUID getOrderId() {
          return uuid;
      }

      @Override
      public Side getSide() {
          return null;
      }

      @Override
      public BigDecimal getQuantity() {
          return null;
      }

      @Override
      public BigDecimal getPrice() {
          return null;
      }
  }

}
