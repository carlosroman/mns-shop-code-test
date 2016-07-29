package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import java.util.List;

public class ShoppingBasket {

    private final Multiset<String> items;

    private ShoppingBasket(final Multiset<String> items) {
        this.items = items;
    }

    public static ShoppingBasket create() {
        return new ShoppingBasket(ConcurrentHashMultiset.create());
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public void addProduct(final Product product) {
        this.items.add(product.getCode());
    }

    public int getProductCount(final Product product) {
        return this.items.count(product.getCode());
    }

    public List<String> getAllProductCodes() {
        return ImmutableList.copyOf(this.items.elementSet());
    }

    public Multiset<String> getBasket() {
        final HashMultiset<String> strings = HashMultiset.create(this.items.size());
        this.items.entrySet().forEach(entry -> strings.add(entry.getElement(), entry.getCount()));
        return strings;
    }
}
