package com.example.exchangerateservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InnerCube(
        @JacksonXmlElementWrapper(useWrapping = false) 
        @JacksonXmlProperty(localName = "Cube")
        List<ExchangeRateResponse> rates 
) {}
