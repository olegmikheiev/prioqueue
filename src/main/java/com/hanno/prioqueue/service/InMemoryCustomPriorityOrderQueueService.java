package com.hanno.prioqueue.service;

import com.hanno.prioqueue.entity.ClientOrderState;
import com.hanno.prioqueue.entity.OrderItem;
import com.hanno.prioqueue.exception.DuplicateClientOrderException;
import com.hanno.prioqueue.exception.InvalidOrderParameterException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("CustomPriorityOrderQueue")
public class InMemoryCustomPriorityOrderQueueService implements OrderQueueService {

    @Value("${queue.client.maxId}")
    private long maxClientId;

    @Value("${joe.cart.capacity}")
    private int cartCapacity;

    @Value("${joe.cart.pickupTimeout}")
    private long pickupTimeoutSeconds;

    private final List<OrderItem> orderQueue = new ArrayList<>();

    @Override
    public OrderItem addOrder(@NonNull OrderItem order) throws InvalidOrderParameterException {
        validateClientId(order.getClientId());
        validateOrderQuantity(order.getQuantity());
        if (orderQueue.stream().anyMatch(o -> order.getClientId().equals(o.getClientId()))) {
            throw new DuplicateClientOrderException(
                    "Order for the client with ID '" + order.getClientId() + "' already exists in the queue");
        }
        order.setOrderAdded(LocalDateTime.now());
        log.info("Adding order: {}", order);
        addOrderBasedOnPriority(order);
        logOrderState();
        return order;
    }

    @Override
    public List<OrderItem> getAllOrders() {
        log.info("Currently queue contains {} orders", orderQueue.size());
        return new ArrayList<>(orderQueue);
    }

    @Override
    public List<OrderItem> getNextDelivery() {
        log.info("Gathering next delivery using cart capacity of {} items", cartCapacity);
        List<OrderItem> cart = new ArrayList<>();
        int itemsInTheCart = 0;
        while (!orderQueue.isEmpty()) {
            OrderItem order = orderQueue.get(0);
            if (itemsInTheCart + order.getQuantity() > cartCapacity) {
                return cart;
            } else {
                itemsInTheCart += order.getQuantity();
                cart.add(orderQueue.remove(0));
            }
        }
        log.info("Next delivery contains {} order(s) with total items quantity {}", cart.size(), itemsInTheCart);
        return cart;
    }

    @Override
    public ClientOrderState getClientOrderState(Long clientId) throws InvalidOrderParameterException {
        validateClientId(clientId);
        Iterator<OrderItem> iterator = orderQueue.iterator();
        int clientPosition = 0;
        int prevCartsNumber = 0;
        int itemsInCurrentCart = 0;
        while (iterator.hasNext()) {
            clientPosition++;
            OrderItem order = iterator.next();
            // Orders cannot be spitted, so calculate full buckets
            if (itemsInCurrentCart + order.getQuantity() > cartCapacity) {
                prevCartsNumber++;
                itemsInCurrentCart = order.getQuantity();
            } else {
                itemsInCurrentCart += order.getQuantity();
            }
            if (clientId.equals(order.getClientId())) {
                return ClientOrderState.of(clientId, clientPosition, prevCartsNumber * pickupTimeoutSeconds);
            }
        }
        return null; // If we're here, then client's order has not been found in the queue
    }

    @Override
    public boolean removeOrder(Long clientId) throws InvalidOrderParameterException {
        validateClientId(clientId);
        return orderQueue.removeIf(o -> clientId.equals(o.getClientId()));
    }

    private void addOrderBasedOnPriority(OrderItem order) {
        orderQueue.add(order);
        for (int i = orderQueue.size() - 1; i > 0; i--) {
            if (orderQueue.get(i).compareTo(orderQueue.get(i - 1)) > 0) {
                return;
            }
            Collections.swap(orderQueue, i, i - 1);
        }
    }

    private void validateClientId(Long clientId) throws InvalidOrderParameterException {
        if (clientId == null || clientId < 1 || clientId > maxClientId) {
            throw new InvalidOrderParameterException(String.format(
                    "Client ID '%d' should be in range [1-%d]", clientId, maxClientId));
        }
    }

    private void validateOrderQuantity(Integer quantity) throws InvalidOrderParameterException {
        // Joe cannot split orders, so order quantity cannot be greater than cart capacity
        if (quantity == null || quantity < 1 || quantity > cartCapacity) {
            throw new InvalidOrderParameterException(String.format(
                    "Order quantity '%d', should be in range [1-%d]", quantity, cartCapacity));
        }
    }

    private void logOrderState() {
        log.debug("Queue state:\n{}",
                orderQueue.stream().map(OrderItem::toString).collect(
                        Collectors.joining("\n", "__________\n", "\n^^^^^^^^^^")));
    }

}
