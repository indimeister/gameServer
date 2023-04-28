package com.indimeister.api.gameServer.domain;

import com.indimeister.api.gameServer.domain.enums.TypePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

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
    private boolean playerTurn;
    private boolean over;
    private TypePlayer winner;

    public NimGame(int heapSize, int maxMatches) {
       this.heapSize = heapSize;
       this.maxMatches = maxMatches;
       this.over = false;
       this.playerTurn = true;
    }
}
