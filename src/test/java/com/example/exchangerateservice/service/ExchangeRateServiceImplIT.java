package com.example.exchangerateservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.exchangerateservice.dto.ExchangeRateAmountResponse;
import com.example.exchangerateservice.dto.ExchangeRateResponse;
import com.example.exchangerateservice.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ExchangeRateServiceImplIT {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setup() {
        exchangeRateService.updateExchangeRates();
    }

    @Test
    @DisplayName("Service should fetch exchange rates and populate the map")
    void testUpdateExchangeRates() {
        Map<String, Integer> accessCounterMap = exchangeRateService.getAccessCounterMap();
        assertThat(accessCounterMap).isNotEmpty();
    }

    @Test
    @DisplayName("Service should return a valid exchange rate from real data")
    void testGetExchangeRate() {
        Optional<ExchangeRateResponse> response = exchangeRateService.getExchangeRate("EUR", "USD");

        assertThat(response).isPresent();
        assertThat(response.get().rate()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Service should correctly convert amounts")
    void testGetConvertedAmount() {
        Optional<ExchangeRateAmountResponse> response = exchangeRateService.getConvertedAmount("EUR", "USD", 100);

        assertThat(response).isPresent();
        assertThat(response.get().convertedAmount()).isGreaterThan(0);
    }
}

