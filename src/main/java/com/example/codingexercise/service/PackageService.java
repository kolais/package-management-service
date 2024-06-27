package com.example.codingexercise.service;

import com.example.codingexercise.controller.dto.PackageRequest;
import com.example.codingexercise.model.Product;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
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
            // TODO: validate and fetch products against product service
            var product = new Product(productToAdd.id(), null, 0, productToAdd.quantity());
            products.add(product);
            usdTotalPrice += product.usdItemPrice() * product.quantity();
        }
        return new ProductPackage(null, packageRequest.name(), packageRequest.description(), products, usdTotalPrice);
    }
}
