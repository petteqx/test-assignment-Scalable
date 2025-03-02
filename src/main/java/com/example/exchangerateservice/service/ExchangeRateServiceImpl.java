package com.example.exchangerateservice.service;

import com.example.exchangerateservice.dto.ExchangeRateAmountResponse;
import com.example.exchangerateservice.dto.ExchangeRateResponse;
import com.example.exchangerateservice.dto.ExchangeRatesDTO;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private static final String ECB_API_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    private final XmlMapper xmlMapper;
    private Map<String, Double> exchangeRateMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> accessCounterMap = new ConcurrentHashMap<>();

    public ExchangeRateServiceImpl() {
        xmlMapper = new XmlMapper();
        updateExchangeRates();
    }

    @Scheduled(fixedRate = 3600000)
    public void updateExchangeRates() {
        try {
            ExchangeRatesDTO exchangeRatesDTO = xmlMapper.readValue(new File("C:\\Users\\Dragonborn\\Desktop\\exchangerateservice\\exchangerateservice\\src\\main\\resources\\eurofxref-daily.xml"), ExchangeRatesDTO.class);

            exchangeRateMap = exchangeRatesDTO.outerCube().innerCube().rates().stream()
                    .collect(Collectors.toMap(ExchangeRateResponse::target, ExchangeRateResponse::rate));

            exchangeRateMap.put("EUR", 1.0);

            for (String key : exchangeRateMap.keySet()) {
                accessCounterMap.merge(key, 0, Integer::sum);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch exchange rates", e);
        }

    }

    @Override
    public Optional<ExchangeRateResponse> getExchangeRate(String baseCurrency, String targetCurrency) {
        baseCurrency = Optional.ofNullable(baseCurrency)
                .map(String::toUpperCase)
                .orElse("");

        targetCurrency = Optional.ofNullable(targetCurrency)
                .map(String::toUpperCase)
                .orElse("");

        Double baseRate = exchangeRateMap.get(baseCurrency);
        Double targetRate = exchangeRateMap.get(targetCurrency);

        if (baseRate != null && targetRate != null) {
            accessCounterMap.merge(baseCurrency, 1, Integer::sum);
            accessCounterMap.merge(targetCurrency, 1, Integer::sum);
            return Optional.of(new ExchangeRateResponse(baseCurrency, targetCurrency, targetRate / baseRate));
        }
        return Optional.empty();
    }

    @Override
    public Map<String, Integer> getAccessCounterMap() {
        return accessCounterMap;
    }

    @Override
    public Optional<ExchangeRateAmountResponse> getConvertedAmount(String baseCurrency, String targetCurrency, double amount) {
        baseCurrency = Optional.ofNullable(baseCurrency)
                .map(String::toUpperCase)
                .orElse("");

        targetCurrency = Optional.ofNullable(targetCurrency)
                .map(String::toUpperCase)
                .orElse("");

        updateExchangeRates();

        Double baseRate = exchangeRateMap.get(baseCurrency);
        Double targetRate = exchangeRateMap.get(targetCurrency);

        if (baseRate != null && targetRate != null) {
            Double conversionRate = targetRate / baseRate;
            Double convertedAmount = conversionRate * amount;

            accessCounterMap.merge(baseCurrency, 1, Integer::sum);
            accessCounterMap.merge(targetCurrency, 1, Integer::sum);

            return Optional.of(new ExchangeRateAmountResponse(baseCurrency, targetCurrency, amount, convertedAmount));
        } else {
            return Optional.empty();
        }
    }
}
