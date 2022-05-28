package com.web.inventory.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockDTO {
    @NotNull(message = "Product Code can't be null")
    @Size(min = 2, max = 30, message = "Product Code can't less than 2")
    private String productCode;
    @Min(value = 1, message = "Add at least one product")
    private int quantity;
}
