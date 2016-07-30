package com.carlosroman.mks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        try {
            final CatalogDAO catalogDAO = CatalogDAO.create("catalog.csv");
            final PromotionsService promotionsService = PromotionsService.create("promotions.json");
            final CheckoutService checkoutService = new CheckoutService(catalogDAO, promotionsService);
            final ShippingCalculator shippingCalculator = new ShippingCalculator();
            final Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the shop:");
            final ShoppingBasket shoppingBasket = ShoppingBasket.create();
            while (true) {
                System.out.println("L - list items.");
                System.out.println("A - add item");
                System.out.println("B - show basket");
                System.out.println("T - show basket total");
                System.out.println("S - get current shipping cost");
                System.out.println("F - get final cost (inc shipping)");
                System.out.println("C - clear basket");
                switch (scanner.next()) {
                    case "L":
                        catalogDAO.getProductMap().entrySet().stream().forEach(entry -> {
                            System.out.println(String.format("%s, %s - Code : %s", entry.getValue().getName(), entry.getValue().getPrice(), entry.getValue().getCode()));
                        });
                        break;
                    case "A":
                        System.out.println("Enter code");
                        catalogDAO.get(scanner.next()).ifPresent(shoppingBasket::addProduct);
                    case "B":
                        System.out.println("Basket contents are:");
                        shoppingBasket.getAllProductCodes().forEach(code -> {
                            catalogDAO.get(code).ifPresent(product ->
                                    System.out.println(String.format("%s x %s", product.getName(), shoppingBasket.getProductCount(product))));
                        });
                        break;
                    case "T":
                        System.out.println(String.format("Basket total is £%s", checkoutService.getTotalFor(shoppingBasket)));
                        break;
                    case "S":
                        System.out.println(String.format("Estimated shipping is £%s", shippingCalculator.getCostFor(checkoutService.getTotalFor(shoppingBasket))));
                        break;
                    case "F":
                        final BigDecimal totalForBasket = checkoutService.getTotalFor(shoppingBasket);
                        final BigDecimal shippingCost = shippingCalculator.getCostFor(totalForBasket);
                        System.out.println(String.format("Final total is £%s", totalForBasket.add(shippingCost)));
                        break;
                    case "C":
                        shoppingBasket.clear();
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
