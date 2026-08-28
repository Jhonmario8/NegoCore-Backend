package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDTO {

    private Long id;
    private Long businessId;
    private String name;
    private Boolean active;
    private LocalDateTime createdAt;


}
