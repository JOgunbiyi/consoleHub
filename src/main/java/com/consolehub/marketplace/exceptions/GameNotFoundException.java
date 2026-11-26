package com.consolehub.marketplace.exceptions;

public class GameNotFoundException extends RuntimeException {
    /**
     * When a game cannot be found
     * @param s
     */
    public GameNotFoundException(String s){
        super(s);
    }
}
