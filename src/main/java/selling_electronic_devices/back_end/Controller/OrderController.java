package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Service.OrderService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

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
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(orderService.getAllOrders(offset, limit));
    }

    @PutMapping("/{orderId}")
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
