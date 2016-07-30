package com.carlosroman.mks.promotions;

import com.carlosroman.mks.model.Product;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class BuyOneGetOneFreeTest {

    private static final BigDecimal PRICE = new BigDecimal(32.95);
    private static final Product PROD_ONE = new Product.Builder().withCode("J01").withName("Blue Jeans").withPrice(PRICE).build();
    private static final Product PROD_TWO = new Product.Builder().withCode("J02").withName("Green Jeans").withPrice(new BigDecimal(32.95)).build();
    private final BuyOneGetOneFree undertest = new BuyOneGetOneFree(ImmutableList.of(PROD_ONE.getCode(), PROD_TWO.getCode()));

    @Test
    public void shouldReturnTotalOfZeroIfPromotionNotTriggered() throws Exception {
        final HashMultiset<Product> productsAndCounts = HashMultiset.create(2);
        productsAndCounts.add(PROD_ONE);
        final BigDecimal total = undertest.sumTotal(productsAndCounts);
        assertThat(total).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.CEILING));
        assertThat(productsAndCounts).hasSize(1).contains(PROD_ONE);
    }

    @Test
    public void shouldRemoveItemsForMultiSetIfTheyTriggeredPromotion() throws Exception {
        final HashMultiset<Product> productsAndCounts = HashMultiset.create(2);
        productsAndCounts.add(PROD_ONE);
        productsAndCounts.add(PROD_TWO);
        productsAndCounts.add(PROD_ONE);
        undertest.sumTotal(productsAndCounts);
        assertThat(productsAndCounts).hasSize(1);
        assertThat(productsAndCounts.count(PROD_TWO)).isEqualTo(1);
        assertThat(productsAndCounts.contains(PROD_ONE)).isFalse();

    }

    @Test
    public void shouldReturnCorrectSumTotalIfTriggeredPromotion() throws Exception {
        final HashMultiset<Product> productsAndCounts = HashMultiset.create(2);
        productsAndCounts.add(PROD_ONE);
        productsAndCounts.add(PROD_TWO);
        productsAndCounts.add(PROD_ONE);
        final BigDecimal total = undertest.sumTotal(productsAndCounts);
        assertThat(total).isEqualTo(new BigDecimal(49.42).setScale(2, RoundingMode.CEILING));
    }
}