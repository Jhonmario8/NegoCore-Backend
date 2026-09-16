package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuoteResponseDTO {

    private String businessName;
    private String address;
    private String phone;
    private String email;
    private String sellerName;
    private String clientName;
    private LocalDate quoteDate;
    private LocalDate expirationDate;
    private List<QuoteItemResponseDTO> items;
    private BigDecimal total;
}
