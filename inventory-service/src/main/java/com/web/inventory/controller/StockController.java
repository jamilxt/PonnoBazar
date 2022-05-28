package com.web.inventory.controller;

import com.web.inventory.dto.StockDTO;
import com.web.inventory.model.Stock;
import com.web.inventory.service.impl.StockServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/inventory")
public class StockController {

    private final StockServiceImpl stockService;

    public StockController(StockServiceImpl stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createStock(@Valid @RequestBody StockDTO stockDTO)
    {
        stockService.createStock(stockDTO);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Stock>> findAllStock() {
        return ResponseEntity.ok(stockService.getAllStock());
    }

    @PutMapping
    public ResponseEntity<Boolean> updateStock(@RequestBody StockDTO stockDTO) {
        return ResponseEntity.ok(stockService.updateStock(stockDTO));
    }

    @GetMapping(path = "/{product_code}")
    public ResponseEntity<Stock> getStock(@PathVariable("product_code") String productCode) {
        return ResponseEntity.ok(stockService.getStock(productCode));
    }

    @GetMapping(path = "/available/{product_code}/{quantity}")
    public ResponseEntity<Boolean> isStockAvailable(@PathVariable("product_code") String productCode,@PathVariable("quantity") Integer quantity) {
        boolean isStockAvailable = stockService.isStockAvailable(productCode, quantity);
        return ResponseEntity.ok(isStockAvailable);
    }

    @DeleteMapping(path = "/{product_code}")
    public ResponseEntity<Boolean> deleteStock(@PathVariable("product_code") String productCode) {
        return ResponseEntity.ok(stockService.deleteStock(productCode));
    }
}
