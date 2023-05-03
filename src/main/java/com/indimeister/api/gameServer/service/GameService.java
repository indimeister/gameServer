package com.indimeister.api.gameServer.service;

import com.indimeister.api.gameServer.domain.entity.NimGame;
import com.indimeister.api.gameServer.domain.dto.RequestDto;
import com.indimeister.api.gameServer.domain.enums.TypePlayer;
import com.indimeister.api.gameServer.exception.GameException;
import com.indimeister.api.gameServer.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Find All
     * @return list NimGame
     */
    public List<NimGame> findAll() {
        return gameRepository.findAll();
    }

    /**
     * Get 1 play
     * @param id
     * @return NimGame
     */
    public NimGame findById(Long id) {
        Optional<NimGame> gameOpt = gameRepository.findById(id);
        if (!gameOpt.isPresent()) {
            throw new GameException("Game not found with ID: " + id);
        }
        return gameOpt.get();
    }

    /**
     * Save 1º play
     * @param game
     * @return NimGame
     */
    public NimGame save(NimGame game) {
        return gameRepository.save(game);
    }


    /**
     * Player given the number Match
     * @param gameId
     * @param dto
     * @return NimGame update
     */
    public NimGame playTurn(Long gameId, RequestDto dto) {
        Optional<NimGame> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new GameException("Game not found with ID: " + gameId);
        }

        NimGame game = gameOpt.get();
        if (game.isOver()) {
            throw new GameException("Game has already ended.");
        }

        int numMatchesPlayer = dto.getNumMatchPlayer();
        if (numMatchesPlayer < 1 || numMatchesPlayer > 3 || numMatchesPlayer > game.getMaxMatches()) {
            throw new GameException("Invalid number of matches taken by player: " + numMatchesPlayer);
        }

        int matchesLeft = game.getMaxMatches() - numMatchesPlayer;
        game.setMaxMatches(matchesLeft);
        game.setPlayerTurn(false);

        if (matchesLeft == 0) {
            game.setOver(true);
            game.setWinner(game.isPlayerTurn() ? TypePlayer.COMPUTER : TypePlayer.PLAYER);
            return Optional.of(gameRepository.save(game)).get();
        }

        int numMatchesComputer = ThreadLocalRandom.current().nextInt(1, Math.min(matchesLeft, 3) + 1);
        matchesLeft -= numMatchesComputer;
        game.setMaxMatches(matchesLeft);
        game.setPlayerTurn(true);

        if (matchesLeft == 0) {
            game.setOver(true);
            game.setWinner(game.isPlayerTurn() ? TypePlayer.PLAYER : TypePlayer.COMPUTER);
            return Optional.of(gameRepository.save(game)).get();
        }

        return Optional.of(gameRepository.save(game)).get();
    }

    /**
     * Player guess the number Random
     * @param gameId
     * @param dto
     * @return NimGame update
     */
    public NimGame turnPlayGuess(Long gameId, RequestDto dto) {
        Optional<NimGame> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new GameException("Game not found with ID: " + gameId);
        }

        NimGame game = gameOpt.get();
        if (game.isOver()) {
            throw new GameException("Game has already ended.");
        }

        if (game.getNumberMatch() <= 0
                && game.getNumberMatch() >= game.getMaxMatches() ) {
            throw new GameException("Invalid number of matches taken by player: " + game.getNumberMatch());
        }

        int matchesLeft = game.getMaxMatches() - game.getNumberMatch();
        game.setNumberMatch(game.getNumberMatch() + 1);
        game.setPlayerTurn(false);

        if (matchesLeft == 0) {
            game.setOver(true);
            game.setWinner(TypePlayer.COMPUTER);
            return Optional.of(gameRepository.save(game)).get();
        }

        //Guess?
        for (int number : game.getNumbersGuess()) {
            if (dto.getNumberGuess() == number) {
                game.setOver(true);
                game.setWinner(TypePlayer.PLAYER);
                game.setPlayerTurn(true);
                return Optional.of(gameRepository.save(game)).get();
            }
        }

        return Optional.of(gameRepository.save(game)).get();
    }
    public NimGame playNim(Long gameId, RequestDto dto) {
        Optional<NimGame> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new GameException("Game not found with ID: " + gameId);
        }

        NimGame game = gameOpt.get();
        if (game.isOver()) {
            throw new GameException("Game has already ended.");
        }

        if (dto.getNumMatchPlayer() > 0
                && dto.getNumMatchPlayer() > game.getMaxMatches()) {
            throw new GameException("Invalid number of matches taken by player: " + game.getNumberMatch());
        }
        Random random = new Random();
        int numToRemove = 0;
        int numMatchesComputer = ThreadLocalRandom.current().nextInt(1, Math.min(game.getMaxMatches(), 4));

        for (int number = 0; number < 2; number++) {//2 = 1 time computer/1 time player
            if (game.getNumbersGuess().length > 0) {
                if (!game.isPlayerTurn()) {
                    // Computer's turn
                    numToRemove = random.nextInt(numMatchesComputer) +1;
                    removeMatchesFromGame(game, numToRemove, true);

                } else {
                    // player's turn
                    numToRemove = dto.getNumMatchPlayer();
                    removeMatchesFromGame(game, numToRemove, false);
                }
            } else {
                determineWinner(game);
            }
        }
        return Optional.of(gameRepository.save(game)).get();
    }

    /**
     * Remove game
     * @param game
     * @param numToRemove
     * @param playerTurn
     */
    private void removeMatchesFromGame(NimGame game, int numToRemove, boolean playerTurn) {
        if (numToRemove < game.getNumbersGuess().length) {
            int[] newNumbersGuess = new int[game.getNumbersGuess().length - numToRemove];
            System.arraycopy(game.getNumbersGuess(), numToRemove, newNumbersGuess, 0, newNumbersGuess.length);
            game.setNumbersGuess(newNumbersGuess);
            game.setPlayerTurn(playerTurn);//next is
        } else {
            determineWinner(game);
        }
    }

    /**
     * How win?
     *
     * @param game
     * @return
     */
    private NimGame determineWinner(NimGame game){
        // Determine winner
        game.setOver(true);
        game.setWinner(game.isPlayerTurn() ? TypePlayer.PLAYER : TypePlayer.COMPUTER);
        game.setPlayerTurn(!game.isPlayerTurn());
        return Optional.of(gameRepository.save(game)).get();
    }

    /**
     *  Delete Play
     * @param id
     */
    public void delete(Long id) {
        Optional<NimGame> game = gameRepository.findById(id);
        if(game.isPresent()) {
            gameRepository.delete(game.get());
        } else {
            throw new GameException("NimGame not found with id " + id);
        }
    }

}
