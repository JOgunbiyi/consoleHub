package com.consolehub.marketplace.application;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.consolehub.marketplace.api.dto.CreateGameRequest;
import com.consolehub.marketplace.api.dto.GameResponse;
import com.consolehub.marketplace.api.dto.UpdateGameRequest;
import com.consolehub.marketplace.api.dto.ApplyDiscountRequest;


import com.consolehub.marketplace.domain.Game;
import com.consolehub.marketplace.infrastructure.GameRepository;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository){
        this.gameRepository = gameRepository;
    }

    @Transactional
    public GameResponse createGame(CreateGameRequest request){
        Game game = new Game(
            request.title(),
            request.description(),
            request.price(),
            request.sizeMb()
        );

        Game savedGame = gameRepository.save(game);

        return toResponse(savedGame);
    }

    /**
     * Update the game.
     * Check the version first
     * Then update
     * @param id
     * @param request
     * @return toResponse the game
     */
    @Transactional
    public GameResponse updateGame(UUID id, UpdateGameRequest request){
        Game game = findGame(id);

        checkVersion(game, request.version());

        game.updateDetails(
            request.title(), 
            request.description(), 
            request.price(), 
            request.sizeMb());   

        gameRepository.flush();

        return toResponse(game);
    }

    /**
     * Publish the game
     * @param id
     * @param version
     * @return
     */
    @Transactional
    public GameResponse publishGame(UUID id, Long version){
        Game game = findGame(id);

        checkVersion(game, version);

        game.publish();

        gameRepository.flush();

        return toResponse(game);
    }

    /**
     * Apply discount to game and change the price
     * @param id
     * @param request
     * @return
     */
    @Transactional 
    public GameResponse applyDiscount(UUID id, ApplyDiscountRequest request){
        Game game = findGame(id);

        checkVersion(game, request.version());

        game.applyDiscount(request.newPrice());

        gameRepository.flush();

        return toResponse(game);
    }

    @Transactional(readOnly = true)
    public List<GameResponse> getPublishedGames(){
        
        return gameRepository.findByPublishedTrue()
        .stream()
        .map(this :: toResponse)
        .toList();
    }

    @Transactional(readOnly = true)
    public GameResponse getPublishedGame(UUID id){

        Game game = gameRepository
        .findByIdAndPublishedTrue(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Game not found"
        ));

        return toResponse(game);
    }

    /**
     * Used to find a game by the ID irrespective of it being published or not
     * @param id
     * @return
     */
    public Game findGame(UUID id){
        return gameRepository.findById(id).
        orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Game not found"
        ));
    }

    /**
     * Utilised to check a version to see if the current version is the expected version
     * @param game
     * @param expectedVersion
     */
    private void checkVersion(Game game, Long expectedVersion){
        if(!game.getVersion().equals(expectedVersion)){
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Game was modified by another request "
            );
        }
    }

    /**
     * To Response will be the DTO
     * @param game
     * @return
     */
    private GameResponse toResponse(Game game) {
        
        return new GameResponse(
            game.getId(),
            game.getTitle(),
            game.getDescription(),
            game.getPrice(),
            game.getCurrency(),
            game.getSizeMb(),
            game.isPublished(),
            game.getVersion()
        );
    }

    public GameResponse getAdminGame(UUID id) {
        return toResponse(findGame(id));
    }
    
}
