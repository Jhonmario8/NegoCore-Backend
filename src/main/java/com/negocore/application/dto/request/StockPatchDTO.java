package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.AssertTrue;
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
public class StockPatchDTO {


    private Integer quantity;
    @NotBlank(message = ApplicationConstants.REASON_NOT_BLANK)
    @Size(max = 200, message = ApplicationConstants.REASON_MAX_SIZE)
    private String reason;

    @AssertTrue(message = ApplicationConstants.QUANTITY_NOT_NULL)
    public boolean isQuantityValid() {
        return quantity != null && quantity != 0;
    }
}
