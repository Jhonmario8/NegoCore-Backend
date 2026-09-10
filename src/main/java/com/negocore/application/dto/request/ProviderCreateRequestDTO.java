package com.negocore.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCreateRequestDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Pattern(
            regexp = "^[0-9+()\\- ]+$",
            message = "Invalid phone format"
    )
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
}
