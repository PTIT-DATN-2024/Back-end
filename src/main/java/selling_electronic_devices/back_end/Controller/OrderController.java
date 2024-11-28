package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.OrderRepository;
import selling_electronic_devices.back_end.Service.OrderService;

import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.createOrder(orderDto);
            response.put("EC", 0);
            response.put("MS", "Created order successfully.");
            return ResponseEntity.ok(response);
        } catch (DataAccessException e) {
            response.put("EC", 1);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("EC", 2);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        return ResponseEntity.ok(orderService.getAllOrders(offset, limit));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable String orderId){
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get an Order success!");
        response.put("product", orderRepository.findById(orderId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrderByCustomerId(@PathVariable String customerId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        return optionalCustomer.map(customer -> {
            response.put("EC", 0);
            response.put("MS", "Get order by customerId successfully.");
            response.put("orders", orderRepository.findByCustomer(customer));
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("EC", 0);
            response.put("MS", "The Customer has no orders.");
            response.put("orders", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    //*@PutMapping("/{orderId}")
    /**
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestBody OrderDto orderDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.updateOrder(orderId, orderDto);
            response.put("EC", 0);
            response.put("MS", "Updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "Error while updating!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }*/

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.updateOrder(orderId, status);
            response.put("EC", 0);
            response.put("MS", "Updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "Error while updating!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(orderService.deleteOrder(orderId));
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("EC", 1);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
