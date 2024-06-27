package com.example.codingexercise.controller;

import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/packages")
public class PackageController {

    private final PackageRepository packageRepository;

    public PackageController(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ProductPackage create(@RequestBody ProductPackage newProductPackage) {
        return packageRepository.create(newProductPackage.getName(), newProductPackage.getDescription(), newProductPackage.getProductIds());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ProductPackage get(@PathVariable String id) {
        return packageRepository.get(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<ProductPackage> getAll() {
        return packageRepository.getAll();
    }
}
