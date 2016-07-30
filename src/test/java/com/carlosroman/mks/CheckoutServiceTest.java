package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import com.carlosroman.mks.promotions.Promotion;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class CheckoutServiceTest {

    private final CatalogDAO catalogDAO = mock(CatalogDAO.class);
    private final PromotionsService promotionsService = mock(PromotionsService.class);
    private final Promotion promotionThree = mock(Promotion.class);
    private final List<Promotion> promotions = Collections.unmodifiableList(Arrays.asList(promotionThree));
    private final ShoppingBasket shoppingBasket = mock(ShoppingBasket.class);

    private Product productOne = new Product.Builder().withCode("one").withName("Jeans One").withPrice(new BigDecimal(10)).build();
    private Product productTwo = new Product.Builder().withCode("two").withName("Jeans Two").withPrice(new BigDecimal(15)).build();
    private Product productPromo = new Product.Builder().withCode("promo").withName("Jeans Two").withPrice(new BigDecimal(20)).build();

    private final CheckoutService undertest = new CheckoutService(catalogDAO, promotionsService);


    @Test
    public void shouldAddCorrectlyBasketTotalWithNoPromotions() throws Exception {
        final HashMultiset<String> value = HashMultiset.create();
        value.add("one", 2);
        value.add("two", 1);
        given(promotionsService.getPromotions()).willReturn(promotions);
        given(shoppingBasket.getBasket()).willReturn(value);
        given(promotionThree.productCodes()).willReturn(Arrays.asList("promo"));

        given(catalogDAO.get("one")).willReturn(Optional.of(productOne));
        given(catalogDAO.get("two")).willReturn(Optional.of(productTwo));

        final BigDecimal total = undertest.getTotalFor(shoppingBasket);
        assertThat(total).isEqualTo(new BigDecimal(35).setScale(2, RoundingMode.CEILING));
    }

    @Test
    public void shouldApplyPromotionToTotal() throws Exception {
        given(promotionsService.getPromotions()).willReturn(promotions);
        given(shoppingBasket.getAllProductCodes()).willReturn(Arrays.asList("promo", "two"));
        final HashMultiset<String> value = HashMultiset.create();
        value.add("promo", 2);
        value.add("two", 1);
        given(shoppingBasket.getBasket()).willReturn(value);

        given(promotionThree.productCodes()).willReturn(Arrays.asList("promo"));

        given(catalogDAO.get("promo")).willReturn(Optional.of(productPromo));
        given(catalogDAO.get("two")).willReturn(Optional.of(productTwo));

        given(promotionThree.sumTotal(any(Multiset.class))).willAnswer(new Answer<BigDecimal>() {
            @Override
            public BigDecimal answer(InvocationOnMock invocation) throws Throwable {
                Multiset argumentAt = invocation.getArgumentAt(0, Multiset.class);
                argumentAt.clear();
                return new BigDecimal(15);
            }
        });

        final BigDecimal total = undertest.getTotalFor(shoppingBasket);

        assertThat(total).isEqualTo(new BigDecimal(30).setScale(2, RoundingMode.CEILING));
    }
}