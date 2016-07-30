package com.carlosroman.mks.promotions;

import com.carlosroman.mks.model.Product;
import com.google.common.collect.Multiset;

import java.math.BigDecimal;
import java.util.List;

public interface Promotion {

    /**
     *
     * @return the ID of the promotion
     */
    String getId();

    /**
     * The product codes that the promotion works on.
     * @return List of product codes that trigger promotion
     */
    List<String> productCodes();

    /**
     * Process a Multiset of products and their counts. The function will remove from
     * the Multiset any items (reduce the count) if they have been processed by the promotion.
     *
     * @param productsAndCounts
     * @return total of product Multiset
     */
    BigDecimal sumTotal(Multiset<Product> productsAndCounts);
}
