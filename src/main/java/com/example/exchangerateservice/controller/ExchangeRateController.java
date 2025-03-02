package com.example.exchangerateservice.controller;

import com.example.exchangerateservice.dto.ExchangeRateAmountResponse;
import com.example.exchangerateservice.dto.ExchangeRateResponse;
import com.example.exchangerateservice.service.ExchangeRateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {
    private final ExchangeRateServiceImpl exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateServiceImpl exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public ResponseEntity<ExchangeRateResponse> referenceExchangeRate(@RequestParam(name = "base", defaultValue = "EUR") String baseCurrency,
                                                                      @RequestParam(name = "target") String targetCurrency) {

        Optional<ExchangeRateResponse> response = exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(response.get());
        }
    }

    @GetMapping("/supported-currencies")
    public ResponseEntity<Map<String, Integer>> allExchangeRates() {
        return ResponseEntity.ok(exchangeRateService.getAccessCounterMap());
    }

    @GetMapping("/convert")
    public ResponseEntity<ExchangeRateAmountResponse> convertForAmount(@RequestParam(name = "base") String baseCurrency,
                                                                       @RequestParam(name = "target") String targetCurrency,
                                                                       @RequestParam(name = "amount") double amount) {
        Optional<ExchangeRateAmountResponse> response = exchangeRateService.getConvertedAmount(baseCurrency, targetCurrency, amount);
        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(response.get());
        }
    }
}
