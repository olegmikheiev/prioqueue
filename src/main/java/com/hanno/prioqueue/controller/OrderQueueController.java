package com.hanno.prioqueue.controller;

import com.hanno.prioqueue.dto.OrderDto;
import com.hanno.prioqueue.entity.ClientOrderState;
import com.hanno.prioqueue.entity.OrderItem;
import com.hanno.prioqueue.exception.InvalidOrderParametersException;
import com.hanno.prioqueue.service.OrderQueueService;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("storehouse")
public class OrderQueueController {

    private final OrderQueueService orderQueueService;
    private final Mapper mapper;

    @Autowired
    public OrderQueueController(@Qualifier("CustomPriorityOrderQueue") OrderQueueService orderQueueService, Mapper mapper) {
        this.orderQueueService = orderQueueService;
        this.mapper = mapper;
    }

    @GetMapping("orders")
    List<OrderDto> getAllOrders() {
        return convertToDto(orderQueueService.getAllOrders());
    }

    @PutMapping("order")
    @ResponseStatus()
    ResponseEntity<OrderItem> addOrder(@RequestBody OrderDto order) throws InvalidOrderParametersException {
        OrderItem addedOrder = orderQueueService.addOrder(mapper.map(order, OrderItem.class));
        return new ResponseEntity<>(addedOrder, HttpStatus.OK);
    }

    @GetMapping("client/{clientId}/status")
    ResponseEntity<ClientOrderState> checkClientState(@PathVariable Long clientId) throws InvalidOrderParametersException {
        ClientOrderState orderState = orderQueueService.getClientOrderState(clientId);
        return new ResponseEntity<>(orderState, orderState == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("next-delivery")
    List<OrderDto> getNextDelivery() {
        return convertToDto(orderQueueService.getNextDelivery());
    }

    @DeleteMapping("order")
    void cancelOrder(@RequestParam Long clientId) throws InvalidOrderParametersException {
        orderQueueService.removeOrder(clientId);
    }

    private List<OrderDto> convertToDto(List<OrderItem> orders) {
        return orders.stream()
                .map(o -> mapper.map(o, OrderDto.class))
                .collect(Collectors.toList());
    }

}
