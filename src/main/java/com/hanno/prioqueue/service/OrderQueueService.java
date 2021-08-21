package com.hanno.prioqueue.service;

import com.hanno.prioqueue.entity.ClientOrderState;
import com.hanno.prioqueue.entity.OrderItem;
import com.hanno.prioqueue.exception.InvalidOrderParameterException;

import java.util.List;

public interface OrderQueueService {

    /**
     * Add order into the queue
     *
     * @param order order item to be added
     * @return true if order has been added, otherwise false
     * @throws InvalidOrderParameterException if order data is invalid
     */
    OrderItem addOrder(OrderItem order) throws InvalidOrderParameterException;

    /**
     * Retrieve all the orders from the queue
     *
     * @return list of all the orders in the queue
     */
    List<OrderItem> getAllOrders();

    /**
     * Take the orders to deliver from the queue
     *
     * @return List of orders that picked up from the queue for the delivery
     */
    List<OrderItem> getNextDelivery();

    /**
     * Get the order state from the specific client
     *
     * @param clientId ID of the client to retrieve data
     * @return Client's position and approximate wait time in minutes
     * @throws InvalidOrderParameterException if client ID is invalid
     */
    ClientOrderState getClientOrderState(Long clientId) throws InvalidOrderParameterException;

    /**
     * Remove order from the queue by request ID
     *
     * @param clientId ID of the client to remove order
     * @return true if order has been removed, otherwise false
     * @throws InvalidOrderParameterException if client ID is invalid
     */
    boolean removeOrder(Long clientId) throws InvalidOrderParameterException;

}
