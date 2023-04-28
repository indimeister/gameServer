package com.indimeister.api.gameServer.controller;

import com.indimeister.api.gameServer.domain.enums.TypePlayer;
import com.indimeister.api.gameServer.service.GameService;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import com.indimeister.api.gameServer.domain.NimGame;
import com.indimeister.api.gameServer.domain.dto.RequestDto;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GameControllerTest {
    @Mock
    private GameService service;

    @InjectMocks
    private GameController controller;

    @Test
    public void testGetAll() {
        List<NimGame> games = Arrays.asList(
                new NimGame(13, 3),
                new NimGame(7, 2)
        );
        when(service.findAll()).thenReturn(games);

        ResponseEntity<List<NimGame>> response = controller.getAll();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(games, response.getBody());
        verify(service).findAll();
    }

    @Test
    public void testGet() {
        Long gameId = 1L;
        NimGame game = new NimGame(13, 3);
        when(service.findById(gameId)).thenReturn(game);

        ResponseEntity<NimGame> response = controller.get(gameId);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(game, response.getBody());
        verify(service).findById(gameId);
    }

    @Test
    public void testPlay() {
        NimGame game = new NimGame(13, 3);
        when(service.save(any(NimGame.class))).thenReturn(game);

        ResponseEntity<NimGame> response = controller.play();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(game, response.getBody());
        verify(service).save(any(NimGame.class));
    }

    @Test
    public void testPlayTurn() {
        Long gameId = 1L;
        RequestDto dto = new RequestDto(TypePlayer.PLAYER,2);
        NimGame game = new NimGame(13, 3);
        when(service.playTurn(gameId, dto)).thenReturn(game);

        ResponseEntity<NimGame> response = controller.play(gameId, dto);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(game, response.getBody());
        verify(service).playTurn(gameId, dto);
    }

    @Test
    public void testDelete() {
        Long gameId = 1L;

        ResponseEntity<?> response = controller.delete(gameId);

        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).delete(gameId);
    }

}
