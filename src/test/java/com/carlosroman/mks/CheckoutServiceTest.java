package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import com.carlosroman.mks.promotions.Promotion;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class CheckoutServiceTest {

    private final CatalogDAO catalogDAO = mock(CatalogDAO.class);
    private final Promotion promotionOne = mock(Promotion.class);
    private final Promotion promotionTwo = mock(Promotion.class);
    private final List<Promotion> promotions = Arrays.asList(promotionOne, promotionTwo);
    private final ShoppingBasket shoppingBasket = mock(ShoppingBasket.class);

    private Product productOne = new Product.Builder().withCode("one").withName("Jeans One").withPrice(new BigDecimal(10)).build();
    private Product productTwo = new Product.Builder().withCode("two").withName("Jeans Two").withPrice(new BigDecimal(15)).build();

    private final CheckoutService undertest = new CheckoutService(catalogDAO, Collections.unmodifiableList(promotions));


    @Test
    public void shouldAddCorrectlyBasketTotalWithNoPromotions() throws Exception {
        given(shoppingBasket.getAllProductCodes()).willReturn(Arrays.asList("one", "two"));
        given(shoppingBasket.getProductCount(productOne)).willReturn(2);
        given(shoppingBasket.getProductCount(productTwo)).willReturn(1);

        given(catalogDAO.get("one")).willReturn(Optional.of(productOne));
        given(catalogDAO.get("two")).willReturn(Optional.of(productTwo));

        final BigDecimal total = undertest.getTotalFor(shoppingBasket);
        assertThat(total).isEqualTo(new BigDecimal(35).setScale(2, RoundingMode.CEILING));
    }
}