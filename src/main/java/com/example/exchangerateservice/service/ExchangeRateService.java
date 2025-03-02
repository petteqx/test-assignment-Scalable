package com.example.exchangerateservice.service;

import com.example.exchangerateservice.dto.ExchangeRateAmountResponse;
import com.example.exchangerateservice.dto.ExchangeRateResponse;

import java.util.Map;
import java.util.Optional;

public interface ExchangeRateService {
    Optional<ExchangeRateResponse> getExchangeRate(String baseCurrency, String targetCurrency);
    Map<String, Integer> getAccessCounterMap();

    Optional<ExchangeRateAmountResponse> getConvertedAmount(String baseCurrency, String targetCurrency, double amount);
}
