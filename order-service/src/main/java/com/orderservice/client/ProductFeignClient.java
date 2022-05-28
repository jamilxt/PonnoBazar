package com.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping(path = "api/v1/products/exist/{product_code}")
    Boolean checkProduct(@PathVariable(name = "product_code") String productCode);
}
