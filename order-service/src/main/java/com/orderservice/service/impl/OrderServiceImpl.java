package com.orderservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.client.InventoryFeignClient;
import com.orderservice.client.ProductFeignClient;
import com.orderservice.dto.OrderDto;
import com.orderservice.dto.TxResponse;
import com.orderservice.exception.InternalServerErrorExceptionHandler;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductFeignClient productFeignClient,
                            InventoryFeignClient inventoryFeignClient,
                            KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper mapper) {
        this.orderRepository = orderRepository;
        this.productFeignClient = productFeignClient;
        this.inventoryFeignClient = inventoryFeignClient;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "handlePlaceOrderFallback")
    @Override
    public TxResponse placeOrder(OrderDto orderDto) throws JsonProcessingException {
//        boolean isProductAvailable = orderDto.getOrderLineItems().stream().allMatch(x -> productFeignClient.checkProduct( x.getProductCode()));
        boolean isProductAvailable = productFeignClient.checkProduct(orderDto.getProductCode());
//        boolean isStockAvailable = orderDto.getOrderLineItems().stream().allMatch(x -> inventoryFeignClient.isStockAvailable(x.getProductCode(), x.getQuantity()));
        boolean isStockAvailable = inventoryFeignClient.isStockAvailable(orderDto.getProductCode(), orderDto.getQuantity());
        if (isProductAvailable && isStockAvailable) {
            logger.info("-----------------Producing Order Message ------------------------");
            String request = mapper.writeValueAsString(orderDto);
            System.out.println("String dto: " + request);
            kafkaTemplate.send("order", "product", orderDto);
            return new TxResponse("200", "Order Successfully Placed");
        }
        return null;
    }

    @KafkaListener(
            topics = "inventory",
            groupId = "groupId"
    )
    @Override
    public String orderStatusUpdate(String message) {
        logger.info("-------------------------Receiving inventory message -------------------");
        System.out.println("Message received: " + message);
        String received = "successfully-purchased";
        if (message.equals(received)) {
            return "success";
        }
        return "Something went wrong! Could not make order!";
    }

    public TxResponse handlePlaceOrderFallback(OrderDto order, InternalServerErrorExceptionHandler e) {
        throw new InternalServerErrorExceptionHandler("Could not fetch product info.");
    }
}
