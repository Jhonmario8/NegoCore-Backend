package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResponseDTO {

    private Purchase purchase;
    private List<PurchaseItem> items;
}
