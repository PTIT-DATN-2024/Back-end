package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Entity.Order;
import selling_electronic_devices.back_end.Repository.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(orderDto.getUserId());
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setStatus(orderDto.getStatus());

        orderRepository.save(order);
    }


    public List<Order> getAllOrders(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("orderId")));
        return orderRepository.findAll(pageRequest).getContent();
    }

    public void updateOrder(String orderId, OrderDto orderDto) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setUserId(orderDto.getUserId());
            order.setTotalAmount(orderDto.getTotalAmount());
            order.setStatus(orderDto.getStatus());

            orderRepository.save(order);
        }
    }

    public void deleteOrder(String orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        optionalOrder.ifPresentOrElse(
                order -> orderRepository.delete(order),
                () -> {
                    throw new RuntimeException("Order with ID " + orderId + " not found.");
                }
        );
    }
}
