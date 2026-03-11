package com.azyu.mahallavote.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class LocalizedNameConverter implements AttributeConverter<LocalizedName, String> {

    private static final Logger LOG = LoggerFactory.getLogger(LocalizedNameConverter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(LocalizedName attribute) {
        if (attribute == null) return "{}";
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            LOG.warn("Failed to serialize LocalizedName: {}", e.getMessage());
            return "{}";
        }
    }

    @Override
    public LocalizedName convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new LocalizedName();
        try {
            return MAPPER.readValue(dbData, LocalizedName.class);
        } catch (Exception e) {
            LOG.warn("Failed to deserialize LocalizedName: {}", e.getMessage());
            return new LocalizedName();
        }
    }
}
