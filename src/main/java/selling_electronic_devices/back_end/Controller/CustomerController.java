package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.CustomerDto;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Service.CustomerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get all users success!");
        response.put("customers", customerService.getAllCustomers(offset, limit));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable String customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        Map<String, Object> response = new HashMap<>();

        return optionalCustomer.map(customer -> {
            response.put("EC", 0);
            response.put("MS", "Get customer success!");
            response.put("customer", customer);
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("EC", 1);
            response.put("MS", "Not found customer!");
            response.put("customer", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateUser(@PathVariable String customerId, @RequestBody CustomerDto customerDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isUpdated = customerService.updateCustomer(customerId, customerDto);
            if (isUpdated) {
                response.put("EC", 0);
                response.put("MS", "Update user success!");
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
            boolean isDeleted = customerService.deleteCustomer(customerId);
            if (isDeleted) {
                response.put("EC", 0);
                response.put("MS", "Deleted customer successfully.");
                return ResponseEntity.ok(response);
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
