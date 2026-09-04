package com.negocore.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientResponseDTO {

    private Long id;
    private Long businessId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;

}
