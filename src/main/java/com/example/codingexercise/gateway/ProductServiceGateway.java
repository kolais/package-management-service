package com.example.codingexercise.gateway;

import com.example.codingexercise.gateway.dto.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class ProductServiceGateway {

    private final Logger logger = LogManager.getLogger();

    private final RestTemplate restTemplate;

    @Value("${product-service.api.url}")
    private String apiUrl;

    @Value("${product-service.api.auth.username}")
    private String apiAuthUsername;

    @Value("${product-service.api.auth.password}")
    private String apiAuthPassword;

    public ProductServiceGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable("product")
    public Product getProduct(String id) {
        logger.info("Requested product with ID [{}]", id);
        var requestEntity = RequestEntity.get(apiUrl + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader())
                .build();
        try {
            return restTemplate.exchange(requestEntity, Product.class).getBody();
        } catch (HttpClientErrorException.NotFound x) {
            logger.warn("Product with ID [{}] not found", id);
            return null;
        } catch (HttpClientErrorException x) {
            logger.error("An exception occurred while querying product service", x);
            return null;
        }
    }

    private String getAuthHeader() {
        var auth = apiAuthUsername + ":" + apiAuthPassword;
        var encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        return "Basic " + new String(encodedAuth);
    }
}
