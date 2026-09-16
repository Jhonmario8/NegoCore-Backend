package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequestDTO {

    @Size(max = 100, message = ApplicationConstants.CLIENT_NAME_SIZE_QUOTE)
    private String clientName;

    @Positive(message = ApplicationConstants.VALIDITY_DAYS_MIN)
    private Integer validityDays;

    @NotNull(message = ApplicationConstants.QUOTE_ITEMS_REQUIRED)
    @NotEmpty(message = ApplicationConstants.QUOTE_ITEMS_REQUIRED)
    private List<@Valid QuoteItemRequestDTO> items;
}
