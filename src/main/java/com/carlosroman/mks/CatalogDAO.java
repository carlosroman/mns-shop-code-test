package com.carlosroman.mks;

import com.carlosroman.mks.model.Product;
import com.google.common.collect.ImmutableMap;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import com.univocity.parsers.conversions.Conversions;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CatalogDAO {

    private final Map<String, Product> productMap;

    private CatalogDAO(final Map<String, Product> productMap) {
        this.productMap = productMap;
    }

    public Map<String, Product> getProductMap() {
        return productMap;
    }

    public static CatalogDAO create(final String resourceFilePath) throws IOException {
        try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceFilePath)) {
            final CsvParserSettings settings = new CsvParserSettings();
            settings.getFormat().setLineSeparator("\n");

            final ImmutableMap.Builder<String, Product> builder = ImmutableMap.builder();

            final ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
                @Override
                public void rowProcessed(Object[] row, ParsingContext context) {

                    final BigDecimal price = ((BigDecimal) row[2]).setScale(2, RoundingMode.DOWN);
                    final Product product = new Product.Builder()
                            .withCode((String) row[1])
                            .withName((String) row[0])
                            .withPrice(price)
                            .build();
                    builder.put(product.getCode(), product);
                }
            };
            rowProcessor.convertIndexes(Conversions.toBigDecimal()).set(2);
            settings.setRowProcessor(rowProcessor);
            settings.setHeaderExtractionEnabled(true);
            final CsvParser parser = new CsvParser(settings);

            parser.parse(inputStream, UTF_8);
            return new CatalogDAO(builder.build());
        }
    }

    public int size() {
        return this.productMap.size();
    }

    public Optional<Product> get(final String code) {
        return Optional.ofNullable(this.productMap.get(code));
    }

}
