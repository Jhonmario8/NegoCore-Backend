package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotBlank(message = ApplicationConstants.EMAIL_NOT_BLANK)
    private String email;

    @NotBlank(message = ApplicationConstants.PASSWORD_NOT_BLANK)
    private String password;

}
