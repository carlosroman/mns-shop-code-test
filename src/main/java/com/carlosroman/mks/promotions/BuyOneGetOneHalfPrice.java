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
import java.util.Stack;

public class BuyOneGetOneHalfPrice implements Promotion {

    public static final String ID = "BOGOHP";
    private static final BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE).setScale(2, RoundingMode.DOWN);
    private final List<String> productCodes;

    public BuyOneGetOneHalfPrice(final List<String> productCodes) {
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
        final Stack<List<Product>> pp = new Stack<>();
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
            final List<Product> remove = pp.pop();
            if (remove.size() == 2) {
                productsProcessed.addAll(remove);
                final BigDecimal divide = remove.get(1).getPrice().divide(TWO, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN);
                sums.add(remove.get(0).getPrice().add(divide));
            }
        }

        productsProcessed.forEach(product -> productsAndCounts.remove(product, 1));
        return sums.stream().reduce(BigDecimal::add).get().setScale(2, RoundingMode.DOWN);
    }
}
