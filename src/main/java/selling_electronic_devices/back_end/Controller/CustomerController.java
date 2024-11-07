package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.CustomerDto;
import selling_electronic_devices.back_end.Service.CustomerService;


@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(customerService.getAllCustomers(offset, limit));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateUser(@PathVariable String customerId, @RequestBody CustomerDto customerDto) {
        try {
            boolean isUpdated = customerService.updateCustomer(customerId, customerDto);
            if (isUpdated) {
                return ResponseEntity.ok("Updated Successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteUser(@PathVariable String customerId) {
        try {
            boolean isDeleted = customerService.deleteUser(customerId);
            if (isDeleted) {
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}
