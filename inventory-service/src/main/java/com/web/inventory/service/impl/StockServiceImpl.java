package com.web.inventory.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.inventory.client.ProductServiceClient;
import com.web.inventory.dto.StockDTO;
import com.web.inventory.exception.InternalServerErrorExceptionHandler;
import com.web.inventory.exception.NotFoundException;
import com.web.inventory.exception.RecordNotUpdateException;
import com.web.inventory.model.Stock;
import com.web.inventory.repository.StockRepository;
import com.web.inventory.service.StockService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    private final StockRepository stockRepository;
    private final ProductServiceClient productServiceClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "handleStockFallback")
    @Override
    public boolean createStock(StockDTO stockDTO) {
        // check product available
        System.out.println("Product service calling...");
        boolean checkProductExists = productServiceClient.checkProduct(stockDTO.getProductCode());
        if (checkProductExists) {
            // check if product stock already available
            Stock existedStock = stockRepository.findStockByProductCode(stockDTO.getProductCode());
            if (existedStock == null) {
                Stock newStock = new Stock();
                newStock.setProductCode(stockDTO.getProductCode());
                newStock.setCreatedDate(new Date());
                newStock.setQuantity(stockDTO.getQuantity());
                //save new stock
                stockRepository.save(newStock);
                // check saved successfully
                Stock savedStock = getStock(stockDTO.getProductCode());
                if (savedStock.getProductCode().equals(stockDTO.getProductCode())) {
                    return true;
                }
                throw new InternalServerErrorExceptionHandler("Can't create product stock.");
            }
            throw new InternalServerErrorExceptionHandler("Stock already added. Please update the stock.");
        }
        throw new InternalServerErrorExceptionHandler("Can't fetch product.");
    }

    @Override
    public List<Stock> getAllStock() {
        List<Stock> getAllStock = stockRepository.findAll();
        if (getAllStock.size() > 0) {
            return getAllStock;
        }
        throw new NotFoundException("Stock list not found.");
    }

    @Override
    public Stock getStock(String productCode) {
        Stock stock = stockRepository.findStockByProductCode(productCode);
        if (stock != null) {
            return stock;
        }
        throw new NotFoundException("Could not found stock.");
    }

    @Override
    public boolean isStockAvailable(String productCode, Integer quantity) {
        Stock stock = getStock(productCode);
        if (stock.getQuantity() > 0 && quantity< stock.getQuantity()) {
            return true;
        }
        return false;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "handleStockFallback")
    @Override
    public boolean updateStock(StockDTO stockDTO) {
        boolean checkProductExists = productServiceClient.checkProduct(stockDTO.getProductCode());
        if (checkProductExists) {
            Stock stock = getStock(stockDTO.getProductCode());
            if (stock != null) {
                stock.setQuantity(stockDTO.getQuantity());
                stock.setUpdatedDate(new Date());
                stockRepository.save(stock);
                Stock updatedStock = getStock(stockDTO.getProductCode());
                if (updatedStock.getProductCode().equals(stockDTO.getProductCode())) {
                    return true;
                }
                throw new InternalServerErrorExceptionHandler("Can't update stock");
            }
            throw new InternalServerErrorExceptionHandler("Internal server error");
        }
        throw new InternalServerErrorExceptionHandler("Can't fetch product.");
    }

    @KafkaListener(
            topics = "order",
            groupId = "groupId"
    )
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "handleUpdateStockAfterPurchaseFallback")
    @Override
    public boolean updateStockAfterPurchase(ConsumerRecord<String, Object> order) throws JsonProcessingException {
        String orders = (String) order.value();
        JsonNode jsonNode = mapper.readTree(orders);
        String productCode = jsonNode.get("productCode").asText();
        int quantity = jsonNode.get("quantity").asInt();
        boolean checkProductExists = productServiceClient.checkProduct(productCode);
        if (checkProductExists) {
            Stock stock = getStock(productCode);
            if (stock != null) {
                boolean isStockAvailable = isStockAvailable(productCode, quantity);
                if (isStockAvailable) {
                    stock.setProductCode(productCode);
                    int updatedQuantity = stockQuantityCalculation(stock, quantity);
                    stock.setQuantity(updatedQuantity);
                    stock.setUpdatedDate(new Date());
                    stockRepository.save(stock);
                    Stock updatedStock = getStock(productCode);
                    if (updatedStock.getProductCode().equals(productCode)) {
                        kafkaTemplate.send("inventory", "successfully-purchased");
                        return true;
                    }
                }
                throw new InternalServerErrorExceptionHandler("Can't update stock");
            }
            throw new InternalServerErrorExceptionHandler("Internal server error");
        }
        throw new InternalServerErrorExceptionHandler("Can't fetch product.");
    }

    private int stockQuantityCalculation(Stock stock, int quantity) {
        int currentStock = stock.getQuantity();
        if (currentStock > quantity) {
            int updatedStock = currentStock - quantity;
            return updatedStock;
        }
        throw new RecordNotUpdateException("Product stock cannot updated");
    }

    @Override
    public boolean deleteStock(String productCode) {
        Stock stock = getStock(productCode);
        if (stock != null) {
            stockRepository.delete(stock);
            Stock deletedStock = getStock(productCode);
            if (deletedStock == null) {
                return true;
            }
            throw new InternalServerErrorExceptionHandler("Can't delete stock");
        }
        throw new InternalServerErrorExceptionHandler("Internal server error");
    }

    public boolean handleStockFallback(StockDTO stockDTO, RuntimeException e) {
        throw new InternalServerErrorExceptionHandler("Could not fetch product info.");
    }

    public boolean handleUpdateStockAfterPurchaseFallback(ConsumerRecord<String, Object> order, RuntimeException e) {
        throw new InternalServerErrorExceptionHandler("Could not fetch product info.");
    }
}
