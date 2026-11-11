package com.consolehub.marketplace.application;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.consolehub.marketplace.api.dto.CreateGameRequest;
import com.consolehub.marketplace.api.dto.GameResponse;
import com.consolehub.marketplace.domain.Game;
import com.consolehub.marketplace.infrastructure.GameRepository;

import org.springframework.stereotype.Service;

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

    private GameResponse toResponse(Game game) {
        
        return new GameResponse(
            game.getId(),
            game.getTitle(),
            game.getDescription(),
            game.getPrice(),
            game.getCurrency(),
            game.getSizeMb(),
            game.isPublished()
        );
    }
    
}
