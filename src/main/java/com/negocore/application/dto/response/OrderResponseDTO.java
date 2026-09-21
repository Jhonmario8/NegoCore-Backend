package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderItem;
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
public class OrderResponseDTO {

    private Order order;
    private List<OrderItem> items;
}
