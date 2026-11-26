package com.consolehub.marketplace.exceptions;

public class GameConflictException extends RuntimeException{
    /**
     * Conflicts in games
     * @param s
     */
    public GameConflictException(String s){
        super(s);
    }
}
