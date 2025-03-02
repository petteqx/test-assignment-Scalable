package com.example.exchangerateservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record ExchangeRateResponse(
        @JsonInclude(JsonInclude.Include.NON_NULL)  String base,
        @JacksonXmlProperty(isAttribute = true, localName = "currency") String target,
        @JacksonXmlProperty(isAttribute = true, localName = "rate") double rate
) {}
