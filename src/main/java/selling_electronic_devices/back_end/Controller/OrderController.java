package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        try {
            orderService.createOrder(orderDto);
            return ResponseEntity.ok("Created order successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while creating order.");
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
        try {
            orderService.updateOrder(orderId, orderDto);
            return ResponseEntity.ok("Updated order successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while updating order.");
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
