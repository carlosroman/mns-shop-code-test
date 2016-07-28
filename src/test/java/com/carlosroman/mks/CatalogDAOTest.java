package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CatalogDAOTest {
    private static final String CATALOG_LOCATION = "test-catalog.csv";

    @Test
    public void shouldLoadCatalogCorrectly() throws Exception {
        final CatalogDAO catalogDAO = getCatalogDAO();
        assertThat(catalogDAO).isNotNull();
        assertThat(catalogDAO.size()).isEqualTo(2);
    }

    private CatalogDAO getCatalogDAO() throws IOException {
        return CatalogDAO.create(CATALOG_LOCATION);
    }

    @Test
    public void shouldReturnCorrectProduct() throws Exception {
        final BigDecimal price = new BigDecimal(15.00).setScale(2, RoundingMode.CEILING);
        final Product expected = new Product.Builder().withCode("J02").withName("Blue Jeans").withPrice(price).build();
        final Optional<Product> product = getCatalogDAO().get(expected.getCode());
        assertThat(product).isPresent();
        assertThat(product.get()).isEqualTo(expected);
    }


    @Test
    public void shouldReturnEmptyIfProductNotFound() throws Exception {
        final Optional<Product> product = getCatalogDAO().get("bad code");
        assertThat(product).isNotPresent();
    }
}