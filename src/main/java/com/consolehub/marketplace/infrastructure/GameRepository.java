package com.consolehub.marketplace.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consolehub.marketplace.domain.Game;

public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findByPublishedTrue();
}
