package com.hanno.prioqueue.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientOrderState {

    private Long clientId;
    private Integer position;
    private Long waitTimeInSeconds;

    public static ClientOrderState of(Long clientId, Integer position, Long waitTimeInSeconds) {
        return new ClientOrderState(clientId, position, waitTimeInSeconds);
    }

}
