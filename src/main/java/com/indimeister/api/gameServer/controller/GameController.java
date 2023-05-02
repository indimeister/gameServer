package com.indimeister.api.gameServer.controller;

import com.indimeister.api.gameServer.domain.entity.NimGame;
import com.indimeister.api.gameServer.domain.dto.RequestDto;
import com.indimeister.api.gameServer.exception.GameException;
import com.indimeister.api.gameServer.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class GameController {
    private final GameService service;

    @Autowired
    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping("/play")
    public ResponseEntity<List<NimGame>> getAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<NimGame> get(@PathVariable("id") Long id) {
            return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping("/play")
    public ResponseEntity<NimGame> play() {
        return ResponseEntity.ok()
                 .body(service.save(new NimGame(13, 3))
                );
    }

    @PatchMapping("/play/{id}")
    public ResponseEntity<NimGame> play(
            @PathVariable("id") Long id,
            @RequestBody RequestDto dto) {
        return ResponseEntity.ok().body(service.playTurn(id, dto));
    }

    @PatchMapping("/play/guess/{id}")
    public ResponseEntity<NimGame> playGuess(
            @PathVariable("id") Long id,
            @RequestBody RequestDto dto) {
        if (dto.getNumberGuess() < 0 || dto.getNumberGuess() > 99) {
            throw new GameException("Value must be between 0 and 99 in body: {numberGuess}" );
        }
        return ResponseEntity.ok().body(service.turnPlayGuess(id, dto));
    }

    @DeleteMapping("/play/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
