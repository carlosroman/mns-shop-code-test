package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingBasketTest {

    private static final Product PRODUCT = new Product.Builder().withCode("J02").withName("Blue Jeans").withPrice(new BigDecimal(12)).build();
    private final ShoppingBasket undertest = ShoppingBasket.create();

    @Test
    public void shouldCreateEmptyBasket() throws Exception {
        assertThat(ShoppingBasket.create().isEmpty()).isTrue();
    }

    @Test
    public void shouldNotBeEmptyAfterAddingItem() throws Exception {
        undertest.addProduct(PRODUCT);
        assertThat(undertest.isEmpty()).isFalse();
    }

    @Test
    public void shouldReturnCorrectItemCountForProduct() throws Exception {
        assertThat(undertest.isEmpty()).isTrue();
        IntStream.rangeClosed(1, 10).forEach(count -> {
            undertest.addProduct(PRODUCT);
            assertThat(undertest.getProductCount(PRODUCT)).isEqualTo(count);
        });
    }

    @Test
    public void shouldGetListOfUniqueItems() throws Exception {
        undertest.addProduct(PRODUCT);
        final String expectedCode = "J02A";
        undertest.addProduct(new Product.Builder().withCode(expectedCode).withName("Blue Jeans").withPrice(new BigDecimal(12)).build());
        List<String> codes = undertest.getAllProductCodes();
        assertThat(codes).hasSize(2).contains(PRODUCT.getCode(), expectedCode);
    }
}