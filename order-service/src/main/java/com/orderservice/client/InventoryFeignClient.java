package com.orderservice.client;

import com.orderservice.dto.StockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {
    @GetMapping(path = "api/v1/inventory/available/{product_code}/{quantity}")
    Boolean isStockAvailable(@PathVariable("product_code") String productCode, @PathVariable("quantity") Integer quantity);
}
