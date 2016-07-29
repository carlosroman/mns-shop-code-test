package com.carlosroman.mks.promotions;

import com.carlosroman.mks.model.Product;
import com.google.common.collect.Multiset;

import java.math.BigDecimal;
import java.util.List;

public interface Promotion {

    String getId();
    List<String> productCodes();
    BigDecimal sumTotal(Multiset<Product> productsAndCounts);
}
