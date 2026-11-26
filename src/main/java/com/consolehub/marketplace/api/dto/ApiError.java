package com.consolehub.marketplace.api.dto;

import java.time.Instant;

/**
 * time stamp
 * status
 * error
 * message
 * ApiError
 */
public record ApiError (
    Instant timestamp,
    int status,
    String error,
    String message
){}
