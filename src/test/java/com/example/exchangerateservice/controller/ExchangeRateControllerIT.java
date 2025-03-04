package com.example.exchangerateservice.controller;

import com.example.exchangerateservice.controller.ExchangeRateController;
import com.example.exchangerateservice.dto.ExchangeRateAmountResponse;
import com.example.exchangerateservice.dto.ExchangeRateResponse;
import com.example.exchangerateservice.service.ExchangeRateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**Integration tests for {@link ExchangeRateController}*/

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ExchangeRateControllerIT {
    private final MockMvc mvc;

    @MockitoBean
    private ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateControllerIT(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("GET /api/exchange-rate valid request")
    void testReferenceExchangeRate() throws Exception {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse("HUF", "EUR", 20.25);
        when(exchangeRateService.getExchangeRate("HUF", "EUR")).thenReturn(Optional.of(mockResponse));

        mvc.perform(get("/api/exchange-rate")
                        .param("base", "HUF")
                        .param("target", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("HUF"))
                .andExpect(jsonPath("$.target").value("EUR"))
                .andExpect(jsonPath("$.rate").value(20.25));
    }

    @Test
    @DisplayName("GET /api/exchange-rate invalid request: missing target currency")
    void testReferenceExchangeRateMissingTarget() throws Exception {
        mvc.perform(get("/api/exchange-rate")
                .param("base", "HUF"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/exchange-rate/supported-currencies get list of supported currencies and how many times they were accessed")
    void testAllExchangeRates() throws Exception {
        Map<String, Integer> mockResponse = Map.of("HUF", 5, "EUR", 3);
        when(exchangeRateService.getAccessCounterMap()).thenReturn(mockResponse);

        mvc.perform(get("/api/exchange-rate/supported-currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.HUF").value(5))
                .andExpect(jsonPath("$.EUR").value(3));
    }

    @Test
    @DisplayName("GET /api/exchange-rate/convert valid conversion")
    void testConvertForAmount() throws Exception {
        ExchangeRateAmountResponse mockResponse = new ExchangeRateAmountResponse("HUF", "EUR", 100.0, 2025.0);
        when(exchangeRateService.getConvertedAmount("HUF", "EUR", 100))
                .thenReturn(Optional.of(mockResponse));

        mvc.perform(get("/api/exchange-rate/convert")
                        .param("base", "HUF")
                        .param("target", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("HUF"))
                .andExpect(jsonPath("$.target").value("EUR"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.convertedAmount").value(2025.0));
    }
}
