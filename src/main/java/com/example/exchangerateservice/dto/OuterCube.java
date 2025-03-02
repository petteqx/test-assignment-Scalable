package com.example.exchangerateservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OuterCube(
        @JacksonXmlProperty(localName = "Cube")
        InnerCube innerCube
) {}
