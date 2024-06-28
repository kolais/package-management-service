package com.example.codingexercise.service;

import com.example.codingexercise.controller.dto.PackageRequest;
import com.example.codingexercise.controller.dto.PackageResponse;
import com.example.codingexercise.controller.dto.ProductResponse;
import com.example.codingexercise.exception.UnknownProductException;
import com.example.codingexercise.gateway.CurrencyRateServiceGateway;
import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.model.Product;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class PackageService {

    private static final String BASE_CURRENCY = "USD";

    private final PackageRepository packageRepository;
    private final ProductServiceGateway productServiceGateway;
    private final CurrencyRateServiceGateway currencyRateServiceGateway;

    public PackageService(PackageRepository packageRepository,
                          ProductServiceGateway productServiceGateway,
                          CurrencyRateServiceGateway currencyRateServiceGateway) {
        this.packageRepository = packageRepository;
        this.productServiceGateway = productServiceGateway;
        this.currencyRateServiceGateway = currencyRateServiceGateway;
    }

    public PackageResponse create(PackageRequest packageRequest, String currency) {
        var productPackage = validateAndCreateProductPackage(packageRequest);
        return renderPackageResponseWithCurrencyConversion(packageRepository.create(productPackage), currency);
    }

    public PackageResponse get(String id, String currency) {
        return renderPackageResponseWithCurrencyConversion(packageRepository.get(id), currency);
    }

    public PackageResponse update(String id, PackageRequest packageRequest, String currency) {
        var productPackage = validateAndCreateProductPackage(packageRequest);
        return renderPackageResponseWithCurrencyConversion(packageRepository.update(id, productPackage), currency);
    }

    public void delete(String id) {
        packageRepository.delete(id);
    }

    public Collection<PackageResponse> getAll(String currency) {
        return packageRepository.getAll().stream().map(p -> renderPackageResponseWithCurrencyConversion(p, currency)).toList();
    }

    private ProductPackage validateAndCreateProductPackage(PackageRequest packageRequest) {
        var products = new ArrayList<Product>();
        int usdTotalPrice = 0;
        for (var productToAdd : packageRequest.products()) {
            var productItem = productServiceGateway.getProduct(productToAdd.id());
            if (null == productItem) {
                throw new UnknownProductException(productToAdd.id());
            }
            var product = new Product(productItem.id(), productItem.name(), productItem.usdPrice(), productToAdd.quantity(), productItem.usdPrice() * productToAdd.quantity());
            products.add(product);
            usdTotalPrice += product.usdTotalPrice();
        }
        return new ProductPackage(null, packageRequest.name(), packageRequest.description(), products, usdTotalPrice);
    }

    /*
     * This method renders product package response from product package applying currency conversion to individual items,
     * rounding them to two decimal places and the reapplying quantity and total calculations. This would work only for
     * currencies where the minimal unit is 1/100 of the base currency unit, which is not universal.
     */
    private PackageResponse renderPackageResponseWithCurrencyConversion(ProductPackage productPackage, String currency) {
        var rate = currencyRateServiceGateway.getRate(BASE_CURRENCY, currency);
        var products = new ArrayList<ProductResponse>();
        var totalPrice = BigDecimal.ZERO;
        for (var product : productPackage.products()) {
            var price = BigDecimal.valueOf(product.usdItemPrice())
                    .multiply(rate)
                    .setScale(2, RoundingMode.HALF_UP);
            price = price.multiply(BigDecimal.valueOf(product.quantity()));
            products.add(new ProductResponse(product.id(), product.name(), product.quantity(), price, currency));
            totalPrice = totalPrice.add(price);
        }
        return new PackageResponse(productPackage.id(), productPackage.name(), productPackage.description(), products, totalPrice, currency);
    }
}
