package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import com.carlosroman.mks.promotions.Promotion;

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

        final Stream<BigDecimal> sumStream = shoppingBasket.getAllProductCodes().stream().map(code -> {
            final Optional<Product> product = this.catalogDAO.get(code);
            if (product.isPresent()) {
                final int productCount = shoppingBasket.getProductCount(product.get());
                return product.get().getPrice().multiply(BigDecimal.valueOf(productCount));
            }
            return new BigDecimal(0);
        });
        final Optional<BigDecimal> reduce = sumStream.reduce(BigDecimal::add);

        if(reduce.isPresent()){
            return reduce.get().setScale(2, RoundingMode.CEILING);
        }

        return new BigDecimal(-1);
    }
}