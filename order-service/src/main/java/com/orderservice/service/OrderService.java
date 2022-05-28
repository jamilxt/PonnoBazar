package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.dto.OrderDto;
import com.orderservice.dto.TxResponse;

public interface OrderService {
    TxResponse placeOrder(OrderDto orders) throws JsonProcessingException;
    String orderStatusUpdate(String message);
}
