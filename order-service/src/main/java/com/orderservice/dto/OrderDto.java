package com.orderservice.dto;

import com.orderservice.model.OrderLineItems;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String productCode;
    private Integer quantity;
}
