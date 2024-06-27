package com.example.codingexercise.controller;

import com.example.codingexercise.controller.dto.PackageRequest;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ProductPackage create(@Valid @RequestBody PackageRequest packageRequest) {
        return packageService.create(packageRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ProductPackage get(@PathVariable String id) {
        return packageService.get(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ProductPackage update(@PathVariable String id, @Valid @RequestBody PackageRequest packageRequest) {
        return packageService.update(id, packageRequest);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void delete(@PathVariable String id) {
        packageService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<ProductPackage> getAll() {
        return packageService.getAll();
    }
}
