package com.example.codingexercise.controller;

import com.example.codingexercise.controller.dto.PackageRequest;
import com.example.codingexercise.controller.dto.PackageResponse;
import com.example.codingexercise.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    public PackageResponse create(@Valid @RequestBody PackageRequest packageRequest,
                                  @RequestParam(required = false, defaultValue = "USD") String currency) {
        return packageService.create(packageRequest, currency);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public PackageResponse get(@PathVariable String id,
                               @RequestParam(required = false, defaultValue = "USD") String currency) {
        return packageService.get(id, currency);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public PackageResponse update(@PathVariable String id,
                                  @Valid @RequestBody PackageRequest packageRequest,
                                  @RequestParam(required = false, defaultValue = "USD") String currency) {
        return packageService.update(id, packageRequest, currency);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void delete(@PathVariable String id) {
        packageService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<PackageResponse> getAll(@RequestParam(required = false, defaultValue = "USD") String currency) {
        return packageService.getAll(currency);
    }
}
