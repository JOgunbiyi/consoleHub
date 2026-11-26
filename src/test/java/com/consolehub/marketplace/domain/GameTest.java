package com.consolehub.marketplace.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class GameTest {
    private Game createGame(){
        return new Game("Test Race Game",
         "Description test", 
         new BigDecimal("20.99"), 
         100); 
    }

    /** 
     * New game
     * - Unpublished
     * 
     * Update scenarios
     * - Normal update
     * - Illegal Argument Exception
     * 
     * Publish scenarios
     * - Normal publish
     * - Illegal State exception
     * 
     * Apply Discount
     * - Unpublished game (Illegal state)
     * - Successful discount
     * - Illegal argument exception
    */

    @Test 
    public void newGameShouldStartUnpublished(){
        Game gameForTesting = createGame();
        assertFalse(gameForTesting.isPublished());
    }

    @Test 
    public void updateGameSuccessfulUpdate(){
        Game gameForTesting = createGame();

        String newTitle = "Updated Game title";
        String newDescription = "Updated description";
        BigDecimal newPrice = new BigDecimal("29.99");
        Integer newSizeMb = 50;
        gameForTesting.updateDetails(newTitle, 
        newDescription, 
        newPrice, 
        newSizeMb);

        assertTrue(gameForTesting.getTitle().equals(newTitle));
        assertTrue(gameForTesting.getDescription().equals(newDescription));
        assertTrue(gameForTesting.getPrice().equals(newPrice));
        assertTrue(gameForTesting.getSizeMb().equals(newSizeMb));
    }

    @Test 
    public void updateGameFailInvalidTitle(){
        Game gameForTesting = createGame();

        assertThrows(IllegalArgumentException.class,
            () -> gameForTesting.updateDetails(null, 
                gameForTesting.getDescription(),
                gameForTesting.getPrice(),
                gameForTesting.getSizeMb())
        );

        assertThrows(IllegalArgumentException.class,
            () -> gameForTesting.updateDetails("", 
                gameForTesting.getDescription(),
                gameForTesting.getPrice(),
                gameForTesting.getSizeMb())
        );
    }

    @Test 
    public void updateGameFailInvalidPrice(){
        Game gameForTesting = createGame();

        assertThrows(IllegalArgumentException.class,
            () -> gameForTesting.updateDetails(gameForTesting.getTitle(), 
                gameForTesting.getDescription(),
                new BigDecimal("-1.00"),
                gameForTesting.getSizeMb())
        );

        assertThrows(IllegalArgumentException.class,
            () -> gameForTesting.updateDetails(gameForTesting.getTitle(), 
                gameForTesting.getDescription(),
                new BigDecimal("10.232"),
                gameForTesting.getSizeMb())
        );
    }

    @Test 
    public void publishGameSuccessfully(){
        Game gameForTesting = createGame();

        gameForTesting.publish();

        assertTrue(gameForTesting.isPublished());
    }

    @Test 
    public void cannotPublishGameTwice(){
        Game gameForTesting = createGame();

        gameForTesting.publish();

        assertThrows(IllegalStateException.class,
            () -> gameForTesting.publish()
        );
    }

    @Test
    void shouldApplyValidDiscount() {
        Game gameForTesting = createGame();

        gameForTesting.publish();

        gameForTesting.applyDiscount(new BigDecimal("19.99"));

        assertEquals(
            0,
            new BigDecimal("19.99")
                .compareTo(gameForTesting.getPrice())
        );
    }

    @Test 
    public void discountRejectedForUnpublishedGame(){
        Game gameForTesting = createGame();

        assertThrows(IllegalStateException.class,
            () ->
            gameForTesting.applyDiscount(new BigDecimal("19.99"))
        );
    }

    @Test 
    public void discountRejectedWhenPriceIncreased(){
        Game gameForTesting = createGame();

        gameForTesting.publish();
        assertThrows(IllegalArgumentException.class,
            () ->
            gameForTesting.applyDiscount(new BigDecimal("30.99"))
        );
    }

    
}
