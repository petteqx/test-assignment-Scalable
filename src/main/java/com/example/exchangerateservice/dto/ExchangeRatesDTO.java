package com.example.exchangerateservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


@JacksonXmlRootElement(localName = "Envelope")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRatesDTO(
        @JacksonXmlProperty(localName = "Cube")
        OuterCube outerCube
) {}

