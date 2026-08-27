package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessCreateDTO {


    @NotBlank(message = ApplicationConstants.NAME_NOT_BLANK)
    private String name;
    @Pattern(
            regexp = ApplicationConstants.CURRENCY_REGEX,
            message = ApplicationConstants.CURRENCY_INVALID
    )
    private String currency;




}
