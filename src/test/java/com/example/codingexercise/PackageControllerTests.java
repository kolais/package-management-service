package com.example.codingexercise;

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
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PackageControllerTests {

    private final TestRestTemplate restTemplate;
    private final PackageRepository packageRepository;

    @Autowired
    PackageControllerTests(TestRestTemplate restTemplate, PackageRepository packageRepository) {
        this.restTemplate = restTemplate;
        this.packageRepository = packageRepository;
    }

    private static void assertPackageEquals(ProductPackage expected, ProductPackage actual, boolean shouldCheckId) {
        assertNotNull(actual, "Unexpected package");
        if (shouldCheckId) {
            assertEquals(expected.getId(), actual.getId(), "Unexpected id");
        }
        assertEquals(expected.getName(), actual.getName(), "Unexpected name");
        assertEquals(expected.getDescription(), actual.getDescription(), "Unexpected description");
        assertEquals(expected.getProductIds(), actual.getProductIds(), "Unexpected products");
    }

    @BeforeEach
    void cleanUpRepository() {
        // ensure there are no packages in the repository before each test
        packageRepository.getAll()
                .stream()
                .map(ProductPackage::getId)
                .forEach(packageRepository::delete);
    }

    @Test
    void createPackage() {
        ProductPackage packageToCreate = new ProductPackage(null, "Test Name", "Test Desc", List.of("prod1"));
        ResponseEntity<ProductPackage> created = restTemplate.postForEntity("/api/v1/packages", packageToCreate, ProductPackage.class);
        assertEquals(HttpStatus.OK, created.getStatusCode(), "Unexpected status code");
        ProductPackage createdBody = created.getBody();
        assertPackageEquals(packageToCreate, createdBody, false);

        ProductPackage productPackage = packageRepository.get(createdBody.getId());
        assertPackageEquals(createdBody, productPackage, true);
    }

    @Test
    void getPackage() {
        ProductPackage productPackage = packageRepository.create("Test Name 2", "Test Desc 2", List.of("prod2"));
        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/api/v1/packages/{id}", ProductPackage.class, productPackage.getId());
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        ProductPackage fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertPackageEquals(productPackage, fetchedBody, true);
    }

    @Test
    void listPackages() {
        ProductPackage productPackage1 = packageRepository.create("Test Name 1", "Test Desc 1", List.of("prod1"));
        ProductPackage productPackage2 = packageRepository.create("Test Name 2", "Test Desc 2", List.of("prod2"));

        ResponseEntity<List<ProductPackage>> fetched = restTemplate.exchange("/api/v1/packages", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        List<ProductPackage> fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        Map<String, ProductPackage> fetchedProductsMap = fetchedBody
                .stream()
                .collect(toMap(ProductPackage::getId, Function.identity()));
        assertEquals(2, fetchedProductsMap.size(), "Unexpected number of products");
        assertPackageEquals(productPackage1, fetchedProductsMap.get(productPackage1.getId()), true);
        assertPackageEquals(productPackage2, fetchedProductsMap.get(productPackage2.getId()), true);
    }
}
