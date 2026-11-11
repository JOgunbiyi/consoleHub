package com.consolehub.marketplace.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GameResponse (
    UUID id,
    String title,
    String description,
    BigDecimal price,
    String currency,
    Integer sizeMb,
    boolean published
) {}