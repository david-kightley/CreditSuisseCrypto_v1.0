package com.dak.crypto.util;

import com.dak.crypto.order.Order;

public class OutputFormatter {

    public static String formatOutput(Order order) {
        return order.getQuantity().toPlainString() + " for £" + order.getPrice().toPlainString();
    }
}
