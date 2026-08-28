package com.negocore.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessListResponseDTO {

    private Long id;
    private String name;
    private String currency;
    private Boolean active;

}
