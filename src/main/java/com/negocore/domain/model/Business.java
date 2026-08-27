package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Business {

    private Long id;
    private String name;
    private Long ownerId;
    private String currency;
    private Boolean active;
    private LocalDateTime createdAt;

}
