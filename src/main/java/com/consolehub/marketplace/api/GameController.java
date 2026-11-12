package com.consolehub.marketplace.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.consolehub.marketplace.api.dto.ApplyDiscountRequest;
import com.consolehub.marketplace.api.dto.CreateGameRequest;
import com.consolehub.marketplace.api.dto.GameResponse;
import com.consolehub.marketplace.api.dto.UpdateGameRequest;
import com.consolehub.marketplace.application.GameService;
import com.consolehub.marketplace.domain.Game;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/v1")
public class GameController {
    
    private final GameService gameService;

    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @PostMapping("/admin/games")
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse createGame(
        @Valid @RequestBody CreateGameRequest request) {

        return gameService.createGame(request);
    }

    @GetMapping("/games")
    public List<GameResponse> getPublishedGames() {
        return gameService.getPublishedGames();
    }

    @GetMapping("/games/{id}")
    public GameResponse getPublishedGame(@PathVariable UUID id){
        return gameService.getPublishedGame(id);
    }

    @GetMapping("admin/games/{id}")
    public GameResponse getAdminGame(@PathVariable UUID id) {
        return gameService.getAdminGame(id);
    }

    @PutMapping("admin/games/{id}")
    public GameResponse updateGame(@PathVariable UUID id, 
        @RequestBody UpdateGameRequest request){
            return gameService.updateGame(id, request);
        }
    

    @PostMapping("admin/games/{id}/publish")
    public GameResponse publishGame(@PathVariable UUID id,
        @RequestParam Long version
    ) {
        return gameService.publishGame(id, version);
    }

    @PostMapping("/admin/games/{id}/discount")
    public GameResponse discountGame(
        @PathVariable UUID id, @Valid @RequestBody ApplyDiscountRequest request) {

        return gameService.applyDiscount(id, request);
    }
}
