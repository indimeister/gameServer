package com.indimeister.api.gameServer.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.indimeister.api.gameServer.domain.enums.TypePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Random;

@Entity
@Table(name = "nim_game")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NimGame{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int heapSize;
    private int maxMatches;
    private int numberMatch;
    private boolean playerTurn;
    private boolean over;
    private TypePlayer winner;
    @JsonIgnore
    int[] numbersGuess;

    public NimGame(int heapSize, int maxMatches) {
       this.heapSize = heapSize;
       this.maxMatches = maxMatches;
       this.over = false;
       this.playerTurn = true;
       this.numbersGuess = randomNumbers(heapSize);
       this.numberMatch = 0;
       this.winner = TypePlayer.NONE;
    }

    private int[] randomNumbers(int heapSize){
        numbersGuess = new int[heapSize];
        Random rand = new Random();
        // Generate array of 13 random numbers between 1 and 99
        for (int i = 0; i < numbersGuess.length; i++) {
            numbersGuess[i] = rand.nextInt(99) + 1;
        }
        return numbersGuess;
    }
}
