package com.web.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.web.inventory.dto.StockDTO;
import com.web.inventory.model.Stock;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public interface StockService {
    boolean createStock(StockDTO stockDTO);
    List<Stock> getAllStock();
    Stock getStock(String productCode);
    boolean isStockAvailable(String productCode, Integer quantity);
    boolean updateStock(StockDTO stockDTO);
    boolean updateStockAfterPurchase(ConsumerRecord<String, Object>  order) throws JsonProcessingException;
    boolean deleteStock(String productCode);
}
