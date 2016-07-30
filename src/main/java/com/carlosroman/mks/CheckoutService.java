package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Stream;

public class CheckoutService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
    private final CatalogDAO catalogDAO;
    private final PromotionsService promotionsService;

    public CheckoutService(final CatalogDAO catalogDAO,
                           final PromotionsService promotionsService) {
        this.catalogDAO = catalogDAO;
        this.promotionsService = promotionsService;
    }

    public BigDecimal getTotalFor(final ShoppingBasket shoppingBasket) {

        final Multiset<String> basketMultiset = shoppingBasket.getBasket();

        final Stream<BigDecimal> promoStream = promotionsService.getPromotions().stream().map(promotion -> {
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
                        System.out.println("S:" + product);
                        basketMultiset.add(product.getCode(), promoItems.count(product));
                    });
                }
                return total;
            }
            return ZERO;
        });

        final Optional<BigDecimal> promoTotal = promoStream.reduce(BigDecimal::add);

        final Stream<BigDecimal> sumStream = basketMultiset.entrySet().stream().map(item -> {
            final Optional<Product> product = this.catalogDAO.get(item.getElement());
            if (product.isPresent()) {
                final int productCount = item.getCount();
                return product.get().getPrice().multiply(BigDecimal.valueOf(productCount));
            }
            return ZERO;
        });

        final Optional<BigDecimal> reduce = sumStream.reduce(BigDecimal::add);

        if (reduce.isPresent() && promoTotal.isPresent()) {
            return reduce.get().add(promoTotal.get()).setScale(2, RoundingMode.DOWN);
        }

        return new BigDecimal(-1);
    }

    public BigDecimal getShippingCost(BigDecimal basketTotal) {

        return ZERO;
    }
}
