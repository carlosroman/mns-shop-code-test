package com.carlosroman.mks.promotions;

import com.carlosroman.mks.model.Product;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BuyOneGetOneFree implements Promotion {

    public static final String ID = "BOGOF";
    private static final BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE);
    private final List<String> productCodes;

    public BuyOneGetOneFree(final List<String> productCodes) {
        this.productCodes = ImmutableList.copyOf(productCodes);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<String> productCodes() {
        return this.productCodes;
    }

    @Override
    public BigDecimal sumTotal(final Multiset<Product> productsAndCounts) {
        final Queue<List<Product>> pp = new LinkedList<>();
        pp.add(new ArrayList<>());
        productsAndCounts.forEach(product -> {
            if (pp.peek().size() < 2) {
                pp.peek().add(product);
            } else {
                final List<Product> products = new ArrayList<>();
                products.add(product);
                pp.add(products);
            }
        });
        final List<Product> productsProcessed = new ArrayList<>();
        final List<BigDecimal> sums = new ArrayList<>();
        sums.add(BigDecimal.ZERO);
        while (!pp.isEmpty()) {
            final List<Product> remove = pp.remove();
            if (remove.size() == 2) {
                productsProcessed.addAll(remove);
                sums.add(remove.get(0).getPrice().add(remove.get(1).getPrice().divide(TWO, BigDecimal.ROUND_UP)));
            }
        }

        productsAndCounts.removeAll(productsProcessed);
        return sums.stream().reduce(BigDecimal::add).get().setScale(2, RoundingMode.CEILING);
    }
}
