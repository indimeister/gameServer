package com.indimeister.api.gameServer.repository;

import com.indimeister.api.gameServer.domain.entity.NimGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<NimGame, Long> {
    Optional<NimGame> findById(Long id);
}
