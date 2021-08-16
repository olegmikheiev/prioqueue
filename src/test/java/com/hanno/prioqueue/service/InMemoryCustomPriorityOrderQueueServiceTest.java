package com.hanno.prioqueue.service;

import com.hanno.prioqueue.entity.OrderItem;
import com.hanno.prioqueue.exception.DuplicateClientOrderException;
import com.hanno.prioqueue.exception.InvalidOrderParametersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringJUnit4ClassRunner.class)
public class InMemoryCustomPriorityOrderQueueServiceTest {

    private static final long MAX_CLIENT_ID = 20000L;
    private static final int CART_CAPACITY = 25;
    private static final long PICKUP_TIMEOUT = 300L;

    private OrderQueueService orderQueueService;

    @BeforeEach
    public void setUp() {
        orderQueueService = new InMemoryCustomPriorityOrderQueueService();
        ReflectionTestUtils.setField(orderQueueService, "maxClientId", MAX_CLIENT_ID);
        ReflectionTestUtils.setField(orderQueueService, "cartCapacity", CART_CAPACITY);
        ReflectionTestUtils.setField(orderQueueService, "pickupTimeoutSeconds", PICKUP_TIMEOUT);
    }

    @Test
    public void verifyOrdersPrioritizationWithinTheQueue() throws InvalidOrderParametersException {
        OrderItem o1 = new OrderItem(101L, 1);
        OrderItem o2 = new OrderItem(102L, 2);
        OrderItem o3 = new OrderItem(103L, 3);
        OrderItem o4 = new OrderItem(1000L, 4);
        OrderItem o11 = new OrderItem(1001L, 11);
        OrderItem o12 = new OrderItem(1002L, 12);
        OrderItem o13 = new OrderItem(1003L, 13);

        orderQueueService.addOrder(o1);
        orderQueueService.addOrder(o11);
        orderQueueService.addOrder(o2);
        orderQueueService.addOrder(o12);
        orderQueueService.addOrder(o13);
        orderQueueService.addOrder(o3);
        orderQueueService.addOrder(o4);

        assertThat(orderQueueService.getAllOrders())
                .as("Orders are not in expected order")
                .containsExactly(o1, o2, o3, o4, o11, o12, o13);
    }

    @Test
    public void addOrderNullObject() {
        assertThrows(NullPointerException.class,
                () -> orderQueueService.addOrder(null));
    }

    @Test
    public void addOrderClientIdIsNull() {
        OrderItem order = new OrderItem(null, 1);
        assertThrows(InvalidOrderParametersException.class,
                () -> orderQueueService.addOrder(order));
    }

    @Test
    public void addOrderClientIdIsNegative() {
        OrderItem order = new OrderItem(-1L, 1);
        assertThrows(InvalidOrderParametersException.class,
                () -> orderQueueService.addOrder(order));
    }

    @Test
    public void addOrderWithDuplicatedClientId() throws InvalidOrderParametersException {
        OrderItem o1 = new OrderItem(11L, 1);
        OrderItem o2 = new OrderItem(11L, 2);

        orderQueueService.addOrder(o1);
        assertThrows(DuplicateClientOrderException.class,
                () -> orderQueueService.addOrder(o2));

        assertThat(orderQueueService.getAllOrders().size()).isEqualTo(1);
        assertThat(orderQueueService.getAllOrders().get(0).getClientId()).isEqualTo(o1.getClientId());
        assertThat(orderQueueService.getAllOrders().get(0).getQuantity()).isEqualTo(o1.getQuantity());
    }

    @Test
    public void addOrderClientIdIsMax() throws InvalidOrderParametersException {
        OrderItem order = new OrderItem(MAX_CLIENT_ID, 1);

        orderQueueService.addOrder(order);

        assertThat(orderQueueService.getAllOrders().size()).isEqualTo(1);
        assertThat(orderQueueService.getAllOrders().get(0).getClientId()).isEqualTo(MAX_CLIENT_ID);
    }


    @Test
    public void addOrderClientIdIsGreaterThanMax() {
        OrderItem order = new OrderItem(MAX_CLIENT_ID + 1L, 1);
        assertThrows(InvalidOrderParametersException.class,
                () -> orderQueueService.addOrder(order));
    }

    @Test
    public void addOrderQuantityIsNull() {
        OrderItem order = new OrderItem(1L, null);
        assertThrows(InvalidOrderParametersException.class,
                () -> orderQueueService.addOrder(order));
    }

    @Test
    public void addOrderQuantityIsNegative() {
        OrderItem order = new OrderItem(1L, -1);
        assertThrows(InvalidOrderParametersException.class,
                () -> orderQueueService.addOrder(order));
    }

    @Test
    public void addOrderQuantityIsMax() throws InvalidOrderParametersException {
        OrderItem order = new OrderItem(1L, CART_CAPACITY);

        orderQueueService.addOrder(order);

        assertThat(orderQueueService.getAllOrders().size()).isEqualTo(1);
        assertThat(orderQueueService.getAllOrders().get(0).getQuantity()).isEqualTo(CART_CAPACITY);
    }

    @Test
    public void addOrderQuantityIsGreaterThanMax() {
        OrderItem order = new OrderItem(1L, CART_CAPACITY + 1);
        assertThrows(InvalidOrderParametersException.class,
                () -> orderQueueService.addOrder(order));
    }

    @Test
    public void getNextDeliveryEmptyQueue() {
        List<OrderItem> delivery = orderQueueService.getNextDelivery();

        assertThat(delivery).isNotNull();
        assertThat(delivery).isEmpty();
    }

    @Test
    public void getNextDelivery1Order() throws InvalidOrderParametersException {
        OrderItem o1 = new OrderItem(11L, 20);
        orderQueueService.addOrder(o1);

        List<OrderItem> delivery = orderQueueService.getNextDelivery();

        assertThat(delivery).isNotNull().isNotEmpty();
        assertThat(delivery.size()).isEqualTo(1);
        assertThat(delivery.get(0).getClientId()).isEqualTo(o1.getClientId());
        assertThat(delivery.get(0).getQuantity()).isEqualTo(o1.getQuantity());
    }

    @Test
    public void getNextDelivery2Orders() throws InvalidOrderParametersException {
        OrderItem o1 = new OrderItem(11L, 20);
        OrderItem o2 = new OrderItem(12L, 5);

        orderQueueService.addOrder(o1);
        orderQueueService.addOrder(o2);

        List<OrderItem> delivery = orderQueueService.getNextDelivery();

        assertThat(delivery).isNotNull().isNotEmpty();
        assertThat(delivery.size()).isEqualTo(2);
        assertThat(delivery.stream().mapToInt(OrderItem::getQuantity).sum())
                .isEqualTo(o1.getQuantity() + o2.getQuantity());

        assertThat(orderQueueService.getNextDelivery()).isEmpty();
    }

    @Test
    public void getNextDeliveryPartialDelivery() throws InvalidOrderParametersException {
        OrderItem o1 = new OrderItem(11L, 20);
        OrderItem o2 = new OrderItem(12L, 6);
        OrderItem o3 = new OrderItem(13L, 4);

        orderQueueService.addOrder(o1);
        orderQueueService.addOrder(o2);
        orderQueueService.addOrder(o3);

        List<OrderItem> delivery1 = orderQueueService.getNextDelivery();

        assertThat(delivery1).isNotNull().isNotEmpty();
        assertThat(delivery1.size()).isEqualTo(1);
        assertThat(delivery1.get(0).getQuantity()).isEqualTo(o1.getQuantity());

        List<OrderItem> delivery2 = orderQueueService.getNextDelivery();

        assertThat(delivery2).isNotNull().isNotEmpty();
        assertThat(delivery2.size()).isEqualTo(2);
        assertThat(delivery2.stream().mapToInt(OrderItem::getQuantity).sum())
                .isEqualTo(o2.getQuantity() + o3.getQuantity());

        assertThat(orderQueueService.getNextDelivery()).isEmpty();
    }

}