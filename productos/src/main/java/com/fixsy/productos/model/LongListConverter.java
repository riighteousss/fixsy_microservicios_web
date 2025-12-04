package com.fixsy.productos.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Convierte listas de Long a una representacion simple separada por comas.
 */
@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        String[] split = dbData.split(",");
        List<Long> list = new ArrayList<>();
        for (String value : split) {
            if (value != null && !value.isBlank()) {
                try {
                    list.add(Long.parseLong(value.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return list;
    }
}
