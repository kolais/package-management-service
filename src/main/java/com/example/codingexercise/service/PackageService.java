package com.example.codingexercise.service;

import com.example.codingexercise.controller.dto.PackageRequest;
import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.model.Product;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final ProductServiceGateway productServiceGateway;

    public PackageService(PackageRepository packageRepository, ProductServiceGateway productServiceGateway) {
        this.packageRepository = packageRepository;
        this.productServiceGateway = productServiceGateway;
    }

    public ProductPackage create(PackageRequest packageRequest) {
        var productPackage = validateAndCreateProductPackage(packageRequest);
        return packageRepository.create(productPackage);
    }

    public ProductPackage get(String id) {
        return packageRepository.get(id);
    }

    public ProductPackage update(String id, PackageRequest packageRequest) {
        var productPackage = validateAndCreateProductPackage(packageRequest);
        return packageRepository.update(id, productPackage);
    }

    public void delete(String id) {
        packageRepository.delete(id);
    }

    public Collection<ProductPackage> getAll() {
        return packageRepository.getAll();
    }

    private ProductPackage validateAndCreateProductPackage(PackageRequest packageRequest) {
        var products = new ArrayList<Product>();
        int usdTotalPrice = 0;
        for (var productToAdd : packageRequest.products()) {
            var productItem = productServiceGateway.getProduct(productToAdd.id());
            if (null == productItem) {
                throw new IllegalArgumentException(format("Unknown product %s", productToAdd.id()));
            }
            var product = new Product(productItem.id(), productItem.name(), productItem.usdPrice(), productToAdd.quantity());
            products.add(product);
            usdTotalPrice += product.usdItemPrice() * product.quantity();
        }
        return new ProductPackage(null, packageRequest.name(), packageRequest.description(), products, usdTotalPrice);
    }
}
