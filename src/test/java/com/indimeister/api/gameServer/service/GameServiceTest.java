package com.indimeister.api.gameServer.service;

import static org.mockito.Mockito.*;
import com.indimeister.api.gameServer.domain.entity.NimGame;
import com.indimeister.api.gameServer.domain.dto.RequestDto;
import com.indimeister.api.gameServer.domain.enums.TypePlayer;
import com.indimeister.api.gameServer.exception.GameException;
import com.indimeister.api.gameServer.repository.GameRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private GameService gameService;
    private Long gameId;
    private int heapSize;
    private int maxMatch;
    private NimGame game;
    private RequestDto requestDto;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // given
        gameId = 1L;
        heapSize = 13;
        maxMatch =3;

        game = new NimGame(gameId, 10, 3, 1, true, false, null, new int[5]);
        requestDto = new RequestDto(TypePlayer.PLAYER,20,2);
    }

    @Test
    public void testFindAll() {
        // Setup
        List<NimGame> games = Arrays.asList(new NimGame(), new NimGame());
        when(gameRepository.findAll()).thenReturn(games);

        // Execute
        List<NimGame> result = gameService.findAll();

        // Verify
        Assertions.assertEquals(2, result.size());
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        // when
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Execute
        NimGame result = gameService.findById(gameId);

        // Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(gameId, result.getId());
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test(expected = GameException.class)
    public void testFindByIdNotFound() {
        // when
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // Execute
        gameService.findById(gameId);

        // Verify
        // Exception is thrown
    }

    @Test
    public void testSave() {
        // when
        when(gameRepository.save(game)).thenReturn(game);

        // Execute
        NimGame result = gameService.save(game);

        // Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(game, result);
        verify(gameRepository, times(1)).save(game);
    }

    @Test(expected = GameException.class)
    public void testPlayTurnNotFound() {
        // when
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // Execute
        gameService.playTurn(gameId, requestDto);

        // Verify
        // Exception is thrown
    }

    @Test(expected = GameException.class)
    public void testPlayTurnAlreadyOver() {
        // when
        game.setOver(true);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Execute
        gameService.playTurn(gameId, requestDto);

        // Verify
        // Exception is thrown
    }

    @Ignore //TODO: return here
    public void testPlayTurn() {
        // when
        when(gameService.playTurn(gameId, requestDto)).thenReturn(game);

        // Execute
        NimGame result = gameService.playTurn(gameId, requestDto);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getMaxMatches(), 1);
        Assertions.assertEquals(result.isPlayerTurn(), false);

        // verify
        verify(gameService, times(1)).playTurn(gameId, requestDto);
    }

    @Test(expected = GameException.class)
    public void testPlayTurnGameNotFound() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        // Act
        gameService.playTurn(gameId, requestDto);
    }

    @Test(expected = GameException.class)
    public void testPlayTurnGameAlreadyOver() {
        game.setOver(true);
        requestDto.setPlayer(TypePlayer.PLAYER);
        requestDto.setNumMatchPlayer(2);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        gameService.playTurn(gameId, requestDto);
    }

    @Test(expected = GameException.class)
    public void testPlayTurnInvalidNumMatchesPlayer() {
        requestDto.setPlayer(TypePlayer.PLAYER);
        requestDto.setNumMatchPlayer(4);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        // Act
        gameService.playTurn(gameId, requestDto);
    }

    @Test
    public void testDeleteExistingGame() {
        Optional<NimGame> optionalGame = Optional.of(game);
        when(gameRepository.findById(1L)).thenReturn(optionalGame);

        gameService.delete(1L);

        verify(gameRepository, times(1)).delete(game);
    }

    @Test(expected = GameException.class)
    public void testDeleteNonExistingGame() {
        Optional<NimGame> optionalGame = Optional.empty();
        when(gameRepository.findById(2L)).thenReturn(optionalGame);

        gameService.delete(2L);
    }

    @Test
    public void testTurnPlayGuess() {
        game.setNumberMatch(3);
        game.setNumbersGuess(new int[5]);
        game.setWinner(TypePlayer.NONE);

        RequestDto dto = new RequestDto();
        dto.setNumberGuess(5);

        when(gameRepository.findById(anyLong())).thenReturn(java.util.Optional.of(game));
        when(gameRepository.save(any(NimGame.class))).thenReturn(game);

        NimGame updatedGame = gameService.turnPlayGuess(1L, dto);

        Assertions.assertTrue(updatedGame.isOver());
        Assertions.assertFalse(updatedGame.isPlayerTurn());
        Assertions.assertEquals(4, updatedGame.getNumberMatch());
        Assertions.assertEquals(TypePlayer.COMPUTER, updatedGame.getWinner());
    }

    @Test
    public void testPlayNim() {
        game.setNumbersGuess(new int[] { 1, 2, 3, 4, 5 });
        game.setMaxMatches(3);
        game.setPlayerTurn(false);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameRepository.save(any(NimGame.class))).thenReturn(game);

        requestDto.setNumMatchPlayer(2);

        NimGame updatedGame = gameService.playNim(1L, requestDto);

        Assertions.assertFalse(updatedGame.isOver());
        Assertions.assertNull(updatedGame.getWinner());
        Assertions.assertFalse(Arrays.equals(new int[] { 3, 4, 5 }, updatedGame.getNumbersGuess()));

        verify(gameRepository, times(1)).save(game);
    }
}

