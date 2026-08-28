package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
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
public class CategoryRequestDTO {



    @NotBlank(message = ApplicationConstants.CATEGORY_NAME_NOT_BLANK)
    @Size(min = 2, max = 60, message = ApplicationConstants.CATEGORY_NAME_SIZE)
    private String name;


}
