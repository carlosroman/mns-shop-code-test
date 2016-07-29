package com.carlosroman.mks.promotions;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class BuyOneGetOneFree implements Promotion {

    private static final String ID = "BOGOF";
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
}
