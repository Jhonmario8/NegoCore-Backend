package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.Email;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderUpdateDTO {

    @Size(min = 2, max = 100, message = ApplicationConstants.PROVIDER_NAME_SIZE)
    private String name;

    @Pattern(
            regexp = "^[0-9+()\\- ]+$",
            message = ApplicationConstants.PROVIDER_PHONE_INVALID
    )
    private String phone;

    @Email(message = ApplicationConstants.PROVIDER_EMAIL_INVALID)
    private String email;

    private String address;
}
