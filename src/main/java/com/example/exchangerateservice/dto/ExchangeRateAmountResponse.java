package com.example.exchangerateservice.dto;

public record ExchangeRateAmountResponse(
        String base,
        String target,
        Double amount,
        Double convertedAmount
) {}
