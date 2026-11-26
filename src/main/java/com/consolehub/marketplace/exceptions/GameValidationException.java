package com.consolehub.marketplace.exceptions;

public class GameValidationException extends RuntimeException {
    /**
     * Application level validations
     * Prevent us from having to rely on HTTP exceptions
     * @param s
     */
    public GameValidationException(String s){
        super(s);
    }
}
