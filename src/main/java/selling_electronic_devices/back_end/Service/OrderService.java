package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.CartDetailDto;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Entity.*;
import selling_electronic_devices.back_end.Repository.*;

import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DetailOrderedProductRepository detailOrderedProductRepository;

    @Autowired
    private ProductRepository productRepository;

    public void createOrder(OrderDto orderDto) {
        Optional<Customer> optionalCustomer = customerRepository.findById(orderDto.getCustomerId());
        Optional<Staff> optionalStaff = staffRepository.findById(orderDto.getStaffId());
        if (optionalCustomer.isPresent() && optionalStaff.isPresent()) {

            Order order = new Order();
            order.setOrderId(UUID.randomUUID().toString());
            order.setCustomer(optionalCustomer.get());
            order.setStaff(optionalStaff.get());
            order.setShipAddress("address");
            order.setTotal(orderDto.getTotal());
            order.setPaymentType("cash");
            order.setStatus("CXN");

            orderRepository.save(order);

            // lưu detailOrderedProduct từ các sản phầm customer chon để Order từ giỏ hàng-Cart (chi tiết dược lưu trong CartDetails)
            List<CartDetailDto> items = orderDto.getCartDetails();
            for(CartDetailDto item : items) {
                DetailOrderedProduct detailOrderedProduct = new DetailOrderedProduct();
                detailOrderedProduct.setDetailOrderProductId(UUID.randomUUID().toString());
                detailOrderedProduct.setOrder(order);
                Optional<Product> optionalProduct = productRepository.findById(item.getProductId());
//                if (optionalProduct.isPresent()) {
//                    detailOrderedProduct.setProduct(optionalProduct.get());
//                } else {
//                    detailOrderedProduct.setProduct(null);
//                }
                detailOrderedProduct.setProduct(optionalProduct.orElseGet(null));
                detailOrderedProduct.setQuantity(item.getQuantity());
                detailOrderedProduct.setTotalPrice(item.getTotalPrice());

                detailOrderedProductRepository.save(detailOrderedProduct);
            }
        } else {
            throw new RuntimeException();
        }
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
        Optional<Customer> optionalCustomer = customerRepository.findById(orderDto.getCustomerId());
        Optional<Staff> optionalStaff = staffRepository.findById(orderDto.getStaffId());
        if (optionalOrder.isPresent() && optionalCustomer.isPresent() && optionalStaff.isPresent()) {
            Order order = optionalOrder.get();
            order.setOrderId(UUID.randomUUID().toString());
            order.setCustomer(optionalCustomer.get());
            order.setStaff(optionalStaff.get());
            order.setShipAddress("address");
            order.setTotal(orderDto.getTotal());
            order.setPaymentType("cash");
            order.setStatus("CXN");

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
