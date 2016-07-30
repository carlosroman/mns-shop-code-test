package com.carlosroman.mks;

import com.carlosroman.mks.promotions.BuyOneGetOneFree;
import com.carlosroman.mks.promotions.Promotion;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PromotionsService {
    private static final String ID = "id";
    private static final String PRODUCT_CODES = "product_codes";
    private final List<Promotion> promotions;

    private PromotionsService(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public static PromotionsService create(final String resourceFilePath) throws IOException {
        try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceFilePath)) {
            final Configuration configuration = Configuration.defaultConfiguration();
            final List<Map<String, Object>> res = JsonPath.using(configuration)
                    .parse(inputStream)
                    .read("$.promotions");
            final ArrayList<Promotion> promos = new ArrayList<>();
            res.forEach(promo -> {
                if (BuyOneGetOneFree.ID.equals(promo.get(ID))) {
                    final JSONArray productCodes = (JSONArray) promo.get(PRODUCT_CODES);
                    final List<String> pc = productCodes.stream().map(a -> (String) a).collect(Collectors.toList());
                    promos.add(new BuyOneGetOneFree(pc));
                }
            });
            return new PromotionsService(Collections.unmodifiableList(promos));
        }
    }

    public List<Promotion> getPromotions() {
        return this.promotions;
    }
}
