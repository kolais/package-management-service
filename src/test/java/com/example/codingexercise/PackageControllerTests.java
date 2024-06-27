package com.example.codingexercise;

import com.example.codingexercise.controller.dto.PackageRequest;
import com.example.codingexercise.controller.dto.PackageResponse;
import com.example.codingexercise.controller.dto.ProductRequest;
import com.example.codingexercise.controller.dto.ProductResponse;
import com.example.codingexercise.gateway.CurrencyRateServiceGateway;
import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.model.Product;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PackageControllerTests {

    private static final String PACKAGE_API_BASE_URI = "/api/v1/packages";

    private final TestRestTemplate restTemplate;
    private final PackageRepository packageRepository;

    @MockBean
    ProductServiceGateway productServiceGateway;

    @MockBean
    CurrencyRateServiceGateway currencyRateServiceGateway;

    @Autowired
    PackageControllerTests(TestRestTemplate restTemplate, PackageRepository packageRepository) {
        this.restTemplate = restTemplate;
        this.packageRepository = packageRepository;
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
        when(productServiceGateway.getProduct("TestProdID01")).thenReturn(
                new com.example.codingexercise.gateway.dto.Product("TestProdID01", "Test Product Name 1", 10)
        );
        when(currencyRateServiceGateway.getRate(anyString(), anyString())).thenReturn(BigDecimal.ONE);

        var createRequest = new PackageRequest("Test Name", "Test Desc", List.of(new ProductRequest("TestProdID01", 1)));
        var createdResponse = restTemplate.postForEntity(PACKAGE_API_BASE_URI, createRequest, PackageResponse.class);
        assertEquals(HttpStatus.OK, createdResponse.getStatusCode(), "Unexpected status code");

        var createdBody = createdResponse.getBody();
        assertNotNull(createdBody, "Unexpected package");
        assertPackageResponseMatchesRequest(createRequest, createdBody);

        var productPackage = packageRepository.get(createdBody.id());
        assertPackageModelMatchesResponse(createdBody, productPackage);
    }

    @Test
    void getPackage() {
        when(currencyRateServiceGateway.getRate(anyString(), anyString())).thenReturn(BigDecimal.ONE);

        var productPackage = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1, 10)),
                        10));

        ResponseEntity<PackageResponse> fetched = restTemplate.getForEntity(PACKAGE_API_BASE_URI + "/{id}", PackageResponse.class, productPackage.id());
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");

        var fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertPackageResponseMatchesModel(productPackage, fetchedBody);
    }

    @Test
    void updatePackage() {
        when(productServiceGateway.getProduct("TestProdID02")).thenReturn(
                new com.example.codingexercise.gateway.dto.Product("TestProdID02", "Test Product Name 2", 10)
        );
        when(currencyRateServiceGateway.getRate(anyString(), anyString())).thenReturn(BigDecimal.ONE);

        var productPackage = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1, 10)),
                        10));

        var updatedPackage = new PackageRequest("Test Name 2", "Test Desc 2", List.of(new ProductRequest("TestProdID02", 2)));
        var requestEntity = RequestEntity.put(PACKAGE_API_BASE_URI + "/{id}", productPackage.id()).body(updatedPackage);
        var updated = restTemplate.exchange(requestEntity, PackageResponse.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode(), "Unexpected status code");

        var updatedBody = updated.getBody();
        assertNotNull(updatedBody);
        assertEquals(productPackage.id(), updatedBody.id());
        assertPackageResponseMatchesRequest(updatedPackage, updatedBody);

        var updatedPackageInRepository = packageRepository.get(productPackage.id());
        assertNotNull(updatedPackageInRepository, "Unexpected package in repository");
        assertPackageModelMatchesResponse(updatedBody, updatedPackageInRepository);
    }

    @Test
    void deletePackage() {
        var productPackage1 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1, 10)),
                        10));

        var productPackage2 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 2",
                        "Test Desc 2",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1, 10)),
                        10));

        var requestEntity = RequestEntity.delete(PACKAGE_API_BASE_URI + "/{id}", productPackage2.id()).build();
        var responseEntity = restTemplate.exchange(requestEntity, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Unexpected status code");

        var packagesInRepository = packageRepository.getAll();
        assertEquals(1, packagesInRepository.size());
        assertPackageModelEquals(productPackage1, packagesInRepository.iterator().next());
    }

    @Test
    void listPackages() {
        when(currencyRateServiceGateway.getRate(anyString(), anyString())).thenReturn(BigDecimal.ONE);

        var productPackage1 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 1",
                        "Test Desc 1",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1, 10)),
                        10));

        var productPackage2 = packageRepository.create(
                new ProductPackage(null,
                        "Test Name 2",
                        "Test Desc 2",
                        List.of(new Product("TestProdID01", "Test Product Name 1", 10, 1, 10)),
                        10));

        ResponseEntity<List<PackageResponse>> fetched = restTemplate.exchange(PACKAGE_API_BASE_URI,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        var fetchedBody = fetched.getBody();

        assertNotNull(fetchedBody, "Unexpected body");
        var fetchedProductsMap = fetchedBody
                .stream()
                .collect(toMap(PackageResponse::id, Function.identity()));
        assertEquals(2, fetchedProductsMap.size(), "Unexpected number of products");
        assertPackageResponseMatchesModel(productPackage1, fetchedProductsMap.get(productPackage1.id()));
        assertPackageResponseMatchesModel(productPackage2, fetchedProductsMap.get(productPackage2.id()));
    }

    private void assertPackageModelEquals(ProductPackage expected, ProductPackage actual) {
        assertEquals(expected.id(), actual.id(), "Unexpected package ID");
        assertEquals(expected.name(), actual.name(), "Unexpected package name");
        assertEquals(expected.description(), actual.description(), "Unexpected package description");
        assertIterableEquals(expected.products(), actual.products());
    }

    private void assertPackageModelMatchesResponse(PackageResponse expected, ProductPackage actual) {
        assertEquals(expected.id(), actual.id(), "Unexpected package ID");
        assertEquals(expected.name(), actual.name(), "Unexpected package name");
        assertEquals(expected.description(), actual.description(), "Unexpected package description");
        assertProductModelMatchesResponse(expected.products(), actual.products());
    }

    private void assertPackageResponseMatchesModel(ProductPackage expected, PackageResponse actual) {
        assertEquals(expected.id(), actual.id(), "Unexpected package ID");
        assertEquals(expected.name(), actual.name(), "Unexpected package name");
        assertEquals(expected.description(), actual.description(), "Unexpected package description");
        assertProductResponseMatchesModel(expected.products(), actual.products());
    }

    private void assertProductModelMatchesResponse(List<ProductResponse> expected, List<Product> actual) {
        assertEquals(expected.size(), actual.size(), "Unexpected number of products");
        var expectedIterator = expected.iterator();
        var actualIterator = actual.iterator();
        while (expectedIterator.hasNext() && actualIterator.hasNext()) {
            var expectedResponse = expectedIterator.next();
            var actualProduct = actualIterator.next();
            assertEquals(expectedResponse.id(), actualProduct.id(), "Unexpected product ID");
            assertEquals(expectedResponse.name(), actualProduct.name(), "Unexpected product name");
            assertEquals(expectedResponse.quantity(), actualProduct.quantity(), "Unexpected product quantity");
            assertEquals(expectedResponse.price().intValue(), actualProduct.usdTotalPrice(), "Unexpected product price");
        }
    }

    private void assertProductResponseMatchesModel(List<Product> expected, List<ProductResponse> actual) {
        assertEquals(expected.size(), actual.size(), "Unexpected number of products");
        var expectedIterator = expected.iterator();
        var actualIterator = actual.iterator();
        while (expectedIterator.hasNext() && actualIterator.hasNext()) {
            var expectedResponse = expectedIterator.next();
            var actualProduct = actualIterator.next();
            assertEquals(expectedResponse.id(), actualProduct.id(), "Unexpected product ID");
            assertEquals(expectedResponse.name(), actualProduct.name(), "Unexpected product name");
            assertEquals(expectedResponse.quantity(), actualProduct.quantity(), "Unexpected product quantity");
            assertEquals(expectedResponse.usdTotalPrice(), actualProduct.price().intValue(), "Unexpected product price");
        }
    }

    private void assertPackageResponseMatchesRequest(PackageRequest expected, PackageResponse actual) {
        assertEquals(expected.name(), actual.name(), "Unexpected name");
        assertEquals(expected.description(), actual.description(), "Unexpected description");
        assertProductResponseMatchesRequest(expected.products(), actual.products());
    }

    private void assertProductResponseMatchesRequest(List<ProductRequest> expected, List<ProductResponse> actual) {
        assertEquals(expected.size(), actual.size(), "Unexpected number of products");
        var expectedIterator = expected.iterator();
        var actualIterator = actual.iterator();
        while (expectedIterator.hasNext() && actualIterator.hasNext()) {
            var expectedResponse = expectedIterator.next();
            var actualProduct = actualIterator.next();
            assertEquals(expectedResponse.id(), actualProduct.id(), "Unexpected product ID");
            assertEquals(expectedResponse.quantity(), actualProduct.quantity(), "Unexpected product quantity");
        }
    }
}
