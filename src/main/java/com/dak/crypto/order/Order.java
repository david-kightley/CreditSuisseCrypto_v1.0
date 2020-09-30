package com.dak.crypto.order;

import com.dak.crypto.Side;

import java.math.BigDecimal;
import java.util.UUID;

public interface Order {

    UUID getOrderId();

    Side getSide();

    BigDecimal getQuantity();

    BigDecimal getPrice();



}