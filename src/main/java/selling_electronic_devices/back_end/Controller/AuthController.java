package selling_electronic_devices.back_end.Controller;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.AuthenticationRequest;
import selling_electronic_devices.back_end.Dto.LoginResponse;
import selling_electronic_devices.back_end.Dto.SignupRequest;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Jwt.JwtUtil;
import selling_electronic_devices.back_end.Repository.CustomerRepository;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (Exception e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        // Tìm customer
        Customer customer = customerRepository.findByEmail(authenticationRequest.getEmail());

        // Tạo jwt
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse(jwt, customer.getCustomerId());
        return ResponseEntity.ok(loginResponse); // trả về object chứa: jwt + customerId.
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest signupRequest) {
        if (customerRepository.findByEmail(signupRequest.getEmail()) != null){
            return "Email already exists!";
        }

        // Tạo mới nếu email chưa tồn tại
        Customer customer = new Customer();
        customer.setEmail(signupRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        customer.setPhone(signupRequest.getPhone());
        customer.setFirstName(signupRequest.getFirstName());
        customer.setLastName(signupRequest.getLastName());
        customer.setAddress(signupRequest.getAddress());
        customer.setIsDelete("0");

        customerRepository.save(customer);

        return "Registered successfully.";
    }

    @PostMapping("/logout")
    public ResponseEntity<?> handleCustomerLogout(@RequestBody Map<String, Long> request) throws Exception{
        try {
            long customerId = request.get("customerId");

            // Xử lý sau: ...

            return ResponseEntity.ok("Customer logout successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to logout!");
        }
    }

}
