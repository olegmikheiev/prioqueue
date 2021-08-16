package com.hanno.prioqueue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Comparable<OrderItem> {

    private static final int PREMIUM_CLIENTS_RANGE = 1000;

    private Long clientId;
    private Integer quantity;
    private LocalDateTime orderAdded;

    public OrderItem(Long clientId, Integer quantity) {
        this.clientId = clientId;
        this.quantity = quantity;
    }

    @Override
    public int compareTo(OrderItem o) {
        if (isPremiumCustomer(this) && !isPremiumCustomer(o)) {
            return -1;
        }
        if (!isPremiumCustomer(this) && isPremiumCustomer(o)) {
            return 1;
        }
        return this.orderAdded.isBefore(o.orderAdded) ? -1 : 1;
    }

    private boolean isPremiumCustomer(OrderItem order) {
        return order.getClientId() <= PREMIUM_CLIENTS_RANGE;
    }
}
