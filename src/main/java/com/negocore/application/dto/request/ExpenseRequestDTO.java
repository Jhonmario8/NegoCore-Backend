package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseRequestDTO {


    @NotBlank(message = ApplicationConstants.DESCRIPTION_NOT_BLANK)
    @Size(max = 200, message = ApplicationConstants.DESCRIPTION_SIZE)
    private String description;
    private String category;
    @NotNull(message = ApplicationConstants.AMOUNT_NOT_NULL)
    @Positive(message = ApplicationConstants.AMOUNT_POSITIVE)
    private Double amount;

}
