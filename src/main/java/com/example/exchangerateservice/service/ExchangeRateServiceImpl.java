package com.example.exchangerateservice.service;

import com.example.exchangerateservice.dto.ExchangeRateAmountResponse;
import com.example.exchangerateservice.dto.ExchangeRateResponse;
import com.example.exchangerateservice.dto.ExchangeRatesDTO;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.InputStream;
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
//            RestTemplate restTemplate = new RestTemplate();
//            ExchangeRatesDTO exchangeRatesDTO = xmlMapper.readValue(restTemplate.getForObject(ECB_API_URL, String.class), ExchangeRatesDTO.class);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("eurofxref-daily.xml");

            ExchangeRatesDTO exchangeRatesDTO = xmlMapper.readValue(inputStream, ExchangeRatesDTO.class);

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
        updateExchangeRates();

        baseCurrency = Optional.ofNullable(baseCurrency)
                .map(String::toUpperCase)
                .orElse("");

        targetCurrency = Optional.ofNullable(targetCurrency)
                .map(String::toUpperCase)
                .orElse("");

        Double conversionRate = getFinalRate(baseCurrency, targetCurrency);

        if (conversionRate != null) {
            updateAccessCounterMap(baseCurrency, targetCurrency);
            return Optional.of(new ExchangeRateResponse(baseCurrency, targetCurrency, conversionRate));
        }
        return Optional.empty();
    }

    @Override
    public Map<String, Integer> getAccessCounterMap() {
        return accessCounterMap;
    }

    @Override
    public Optional<ExchangeRateAmountResponse> getConvertedAmount(String baseCurrency, String targetCurrency, double amount) {
        updateExchangeRates();

        baseCurrency = Optional.ofNullable(baseCurrency)
                .map(String::toUpperCase)
                .orElse("");

        targetCurrency = Optional.ofNullable(targetCurrency)
                .map(String::toUpperCase)
                .orElse("");

        Double conversionRate = getFinalRate(baseCurrency, targetCurrency);

        if (conversionRate != null) {
            Double convertedAmount = conversionRate * amount;

            updateAccessCounterMap(baseCurrency, targetCurrency);

            return Optional.of(new ExchangeRateAmountResponse(baseCurrency, targetCurrency, amount, convertedAmount));
        }
        return Optional.empty();

    }

    private Double getFinalRate(String baseCurrency, String targetCurrency) {
        Double baseRate = exchangeRateMap.get(baseCurrency);
        Double targetRate = exchangeRateMap.get(targetCurrency);

        if (baseRate != null && targetRate != null) {
            Double conversionRate = targetRate / baseRate;
            return conversionRate;
        } else {
            return null;
        }
    }

    private void updateAccessCounterMap(String baseCurrency, String targetCurrency) {
        accessCounterMap.merge(baseCurrency, 1, Integer::sum);
        accessCounterMap.merge(targetCurrency, 1, Integer::sum);
    }
}
