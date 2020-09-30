package com.dak.crypto.order;

import com.dak.crypto.Cryptocurrency;

public interface CryptoOrder extends Order {

    String getUserId();

    Cryptocurrency getCoinType();

}
