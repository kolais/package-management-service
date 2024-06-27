package com.example.codingexercise.gateway;

import com.example.codingexercise.gateway.dto.ConversionRates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

@Component
public class FrankfurterCurrencyRateServiceGateway implements CurrencyRateServiceGateway {

    private final Logger logger = LogManager.getLogger();

    private final RestTemplate restTemplate;
    @Value("${frankfurter.api.url}")
    private String apiUrl;

    FrankfurterCurrencyRateServiceGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "currencyRates", key = "#base + '-' + #currency")
    public BigDecimal getRate(String base, String currency) {
        logger.info("Requested currency rate for [{}/{}]", base, currency);
        if (base.equals(currency)) {
            return BigDecimal.ONE;
        }
        var requestEntity = RequestEntity.get(apiUrl + "/latest?from={from}&to={to}", base, currency).build();
        try {
            var rates = ofNullable(restTemplate.exchange(requestEntity, ConversionRates.class).getBody());
            return rates.map(ConversionRates::rates).map(r -> r.get(currency)).orElse(null);
        } catch (HttpClientErrorException.NotFound x) {
            logger.warn("Currency rate for [{}/{}] not found", base, currency);
            return null;
        } catch (HttpClientErrorException x) {
            logger.error("An exception occurred while querying Frankfurter service", x);
            return null;
        }
    }
}
