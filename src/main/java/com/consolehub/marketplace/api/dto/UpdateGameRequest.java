package com.consolehub.marketplace.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateGameRequest (
    
    @NotBlank 
    @Size(max = 200)
    String title,

    String description,

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    BigDecimal price,
    
    @Positive 
    @NotNull 
    Integer sizeMb,

    @NotNull 
    Long version

){}
