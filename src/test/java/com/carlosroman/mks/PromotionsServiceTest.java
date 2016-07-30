package com.carlosroman.mks;

import com.carlosroman.mks.promotions.Promotion;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PromotionsServiceTest {

    @Test
    public void shouldLoadUpConfigFile() throws Exception {
        final PromotionsService promotionsService = PromotionsService.create("test-promotions.json");
        assertThat(promotionsService).isNotNull();
    }

    @Test
    public void shouldReturnCorrectPromotions() throws Exception {
        final PromotionsService promotionsService = PromotionsService.create("test-promotions.json");
        final List<Promotion> promotions = promotionsService.getPromotions();
        assertThat(promotions)
                .isNotEmpty()
                .hasSize(2);
        assertThat(promotions.get(0).productCodes()).containsOnly("J01");
        assertThat(promotions.get(1).productCodes()).containsOnly("AAA", "BBB");
    }

}