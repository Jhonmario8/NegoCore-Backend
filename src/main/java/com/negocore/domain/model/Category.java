package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private Long id;
    private Long businessId;
    private String name;
    private Boolean active;
    private LocalDateTime createdAt;

}
