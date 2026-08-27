package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @NotBlank(message = ApplicationConstants.NAME_NOT_BLANK)
    private String name;
    @NotBlank(message = ApplicationConstants.EMAIL_NOT_BLANK)
    @Email(message = ApplicationConstants.EMAIL_NOT_VALID)
    private String email;
    @NotBlank(message = ApplicationConstants.PHONE_NUMBER_NOT_BLANK)
    private String phoneNumber;
    @NotBlank(message = ApplicationConstants.PASSWORD_NOT_BLANK)
    private String password;


}
