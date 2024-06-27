package com.example.codingexercise;

import com.example.codingexercise.controller.dto.PackageRequest;
import com.example.codingexercise.controller.dto.ProductRequest;
import com.example.codingexercise.model.Product;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PackageControllerTests {

    private static final String PACKAGE_API_BASE_URI = "/api/v1/packages";

    private final TestRestTemplate restTemplate;
    private final PackageRepository packageRepository;

    @Autowired
    PackageControllerTests(TestRestTemplate restTemplate, PackageRepository packageRepository) {
        this.restTemplate = restTemplate;
        this.packageRepository = packageRepository;
    }

    private static void assertPackageEquals(ProductPackage expected, ProductPackage actual) {
        assertEquals(expected.id(), actual.id(), "Unexpected ID");
        assertEquals(expected.name(), actual.name(), "Unexpected name");
        assertEquals(expected.description(), actual.description(), "Unexpected description");
        assertIterableEquals(expected.products(), actual.products(), "Unexpected products");
    }

    private static void assertPackageEqualsRequest(PackageRequest expected, ProductPackage actual) {
        assertEquals(expected.name(), actual.name(), "Unexpected name");
        assertEquals(expected.description(), actual.description(), "Unexpected description");
        assertIterableEquals(expected.products(),
                actual.products().stream().map(p -> new ProductRequest(p.id(), p.quantity())).toList(),
                "Unexpected products");

    }

    @BeforeEach
    void cleanUpRepository() {
        // ensure there are no packages in the repository before each test
        packageRepository.getAll()
                .stream()
                .map(ProductPackage::id)
                .forEach(packageRepository::delete);
    }

    @Test
    void createPackage() {
        var packageToCreate = new PackageRequest("Test Name", "Test Desc", List.of(new ProductRequest("TestProdID01", 1)));
        var created = restTemplate.postForEntity(PACKAGE_API_BASE_URI, packageToCreate, ProductPackage.class);
        assertEquals(HttpStatus.OK, created.getStatusCode(), "Unexpected status code");

        var createdBody = created.getBody();
        assertNotNull(createdBody, "Unexpected package");
        assertPackageEqualsRequest(packageToCreate, createdBody);

        var productPackage = packageRepository.get(createdBody.id());
        assertPackageEquals(createdBody, productPackage);
    }

    @Test
    void getPackage() {
        var productPackage = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1)),
                        10));

        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity(PACKAGE_API_BASE_URI + "/{id}", ProductPackage.class, productPackage.id());
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");

        var fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertPackageEquals(productPackage, fetchedBody);
    }

    @Test
    void updatePackage() {
        var productPackage = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1)),
                        10));

        var updatedPackage = new PackageRequest("Test Name 2", "Test Desc 2", List.of(new ProductRequest("TestProdID02", 2)));
        var requestEntity = RequestEntity.put(PACKAGE_API_BASE_URI + "/{id}", productPackage.id()).body(updatedPackage);
        var updated = restTemplate.exchange(requestEntity, ProductPackage.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode(), "Unexpected status code");

        var updatedBody = updated.getBody();
        assertNotNull(updatedBody);
        assertEquals(productPackage.id(), updatedBody.id());
        assertPackageEqualsRequest(updatedPackage, updatedBody);

        var updatedPackageInRepository = packageRepository.get(productPackage.id());
        assertNotNull(updatedPackageInRepository, "Unexpected package in repository");
        assertPackageEquals(updatedBody, updatedPackageInRepository);
    }

    @Test
    void deletePackage() {
        var productPackage1 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1)),
                        10));

        var productPackage2 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 2",
                        "Test Desc 2",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1)),
                        10));

        var requestEntity = RequestEntity.delete(PACKAGE_API_BASE_URI + "/{id}", productPackage2.id()).build();
        var responseEntity = restTemplate.exchange(requestEntity, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Unexpected status code");

        var packagesInRepository = packageRepository.getAll();
        assertEquals(1, packagesInRepository.size());
        assertPackageEquals(productPackage1, packagesInRepository.iterator().next());
    }

    @Test
    void listPackages() {
        var productPackage1 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1)),
                        10));

        var productPackage2 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 2",
                        "Test Desc 2",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1)),
                        10));

        ResponseEntity<List<ProductPackage>> fetched = restTemplate.exchange(PACKAGE_API_BASE_URI,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        var fetchedBody = fetched.getBody();

        assertNotNull(fetchedBody, "Unexpected body");
        var fetchedProductsMap = fetchedBody
                .stream()
                .collect(toMap(ProductPackage::id, Function.identity()));
        assertEquals(2, fetchedProductsMap.size(), "Unexpected number of products");
        assertPackageEquals(productPackage1, fetchedProductsMap.get(productPackage1.id()));
        assertPackageEquals(productPackage2, fetchedProductsMap.get(productPackage2.id()));
    }
}
