package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
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
public class ClientUpdateDTO {

    @Size(min = 2, max = 100, message = ApplicationConstants.CLIENT_NAME_SIZE)
    private String name;

    @Pattern(regexp = ApplicationConstants.PHONE_REGEX, message = ApplicationConstants.CLIENT_PHONE_INVALID)
    private String phone;

    @Pattern(regexp = ApplicationConstants.EMAIL_REGEX, message = ApplicationConstants.CLIENT_EMAIL_INVALID)
    private String email;

    private String address;

}
