package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Entity.Order;
import selling_electronic_devices.back_end.Repository.OrderRepository;

import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerId(orderDto.getCustomerId());
        order.setStaffId(orderDto.getStaffId());
        order.setShipAddress(orderDto.getShipAddress());
        order.setShipFee(orderDto.getShipFee());
        order.setPaymentType(orderDto.getPaymentType());
        order.setStatus(orderDto.getStatus());

        orderRepository.save(order);
    }


    public Map<String, Object> getAllOrders(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("orderId")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get All Orders Successfully.");
        response.put("orders", orderRepository.findAll(pageRequest).getContent());
        return response;
    }

    public void updateOrder(String orderId, OrderDto orderDto) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setCustomerId(orderDto.getCustomerId());
            order.setStaffId(orderDto.getStaffId());
            order.setShipAddress(orderDto.getShipAddress());
            order.setShipFee(orderDto.getShipFee());
            order.setPaymentType(orderDto.getPaymentType());
            order.setStatus(orderDto.getStatus());

            orderRepository.save(order);
        }
    }

    public Map<String, Object> deleteOrder(String orderId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        optionalOrder.ifPresent(order -> orderRepository.delete(order));
        response.put("EC", 0);
        response.put("MS", "Deleted order successfully.");
        return response;
    }
}
