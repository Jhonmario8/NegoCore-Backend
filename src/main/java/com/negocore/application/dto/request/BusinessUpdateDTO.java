package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessUpdateDTO {

    private String name;

    @Pattern(
            regexp = ApplicationConstants.CURRENCY_REGEX,
            message = ApplicationConstants.CURRENCY_INVALID
    )
    private String currency;

    private String address;

    private String phone;

    @Pattern(
            regexp = ApplicationConstants.EMAIL_REGEX,
            message = ApplicationConstants.EMAIL_NOT_VALID
    )
    private String email;

}
