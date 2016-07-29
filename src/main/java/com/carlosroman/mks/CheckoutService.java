package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import com.carlosroman.mks.promotions.Promotion;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CheckoutService {

    private final CatalogDAO catalogDAO;
    private final List<Promotion> promotions;

    public CheckoutService(final CatalogDAO catalogDAO,
                           final List<Promotion> promotions) {
        this.catalogDAO = catalogDAO;
        this.promotions = promotions;
    }

    public BigDecimal getTotalFor(final ShoppingBasket shoppingBasket) {

        final Multiset<String> basketMultiset = shoppingBasket.getBasket();

        final Stream<BigDecimal> promoStream = promotions.stream().map(promotion -> {
            final Multiset<Product> promoItems = ConcurrentHashMultiset.create();
            promotion.productCodes().stream().parallel().forEach(code -> {
                if (basketMultiset.contains(code)) {
                    this.catalogDAO.get(code).ifPresent(product -> {
                        final int count = basketMultiset.count(code);
                        promoItems.add(product, count);
                        basketMultiset.remove(code, count);
                    });
                }
            });
            if (!promoItems.isEmpty()) {
                final BigDecimal total = promotion.sumTotal(promoItems);
                if (!promoItems.isEmpty()) {
                    promoItems.forEach(product -> {
                        System.out.println("S:"+product);
                        basketMultiset.add(product.getCode(), promoItems.count(product));
                    });
                }
                return total;
            }
            return new BigDecimal(0);
        });

        final Optional<BigDecimal> promoTotal = promoStream.reduce(BigDecimal::add);

        final Stream<BigDecimal> sumStream = basketMultiset.entrySet().stream().map(item -> {
            final Optional<Product> product = this.catalogDAO.get(item.getElement());
            if (product.isPresent()) {
                final int productCount = item.getCount();
                return product.get().getPrice().multiply(BigDecimal.valueOf(productCount));
            }
            return new BigDecimal(0);
        });

        final Optional<BigDecimal> reduce = sumStream.reduce(BigDecimal::add);

        if (reduce.isPresent() && promoTotal.isPresent()) {
            return reduce.get().add(promoTotal.get()).setScale(2, RoundingMode.CEILING);
        }

        return new BigDecimal(-1);
    }


}
