package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.CustomerDto;
import selling_electronic_devices.back_end.Service.CustomerService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get All Categories Successfully.");
        response.put("customers", customerService.getAllCustomers(offset, limit));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateUser(@PathVariable String customerId, @RequestBody CustomerDto customerDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isUpdated = customerService.updateCustomer(customerId, customerDto);
            if (isUpdated) {
                response.put("EC", 0);
                response.put("MS", "Get all customers successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("EC", 1);
                response.put("MS", "Not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteUser(@PathVariable String customerId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDeleted = customerService.deleteUser(customerId);
            if (isDeleted) {
                response.put("EC", 0);
                response.put("MS", "Deleted customer successfully.");
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                response.put("EC", 1);
                response.put("MS", "Not found to delete!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
