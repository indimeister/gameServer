package com.indimeister.api.gameServer.domain.dto;

import com.indimeister.api.gameServer.domain.enums.TypePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    @NonNull
    private TypePlayer player;
    @NonNull
    private Integer numMatchPlayer;

}
