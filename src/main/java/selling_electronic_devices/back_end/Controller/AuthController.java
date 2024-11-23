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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
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

        LoginResponse loginResponse = new LoginResponse(jwt, customer.getCustomerId(), authenticationRequest.getEmail(), "customer", "1728958738001.jpg");
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Login Successfully.");
        response.put("user", loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (customerRepository.findByEmail(signupRequest.getEmail()) != null){
            return ResponseEntity.status(HttpStatus.CREATED).body("Email already exists.");
        }

        // Tạo mới nếu email chưa tồn tại
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setEmail(signupRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        customer.setUserName("name" + System.currentTimeMillis());
        customer.setFullName("full_name" + System.currentTimeMillis());
        customer.setAddress("Ha Noi");
        customer.setRole("customer");
        customer.setAvatar(signupRequest.getAvatar());
        customer.setPhone(signupRequest.getPhone());
        customer.setIsDelete("false");

        customerRepository.save(customer);

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Signup Successfully.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> handleCustomerLogout(@RequestBody Map<String, Long> request) throws Exception{
        try {
            long customerId = request.get("customerId");


            return ResponseEntity.ok("Customer logout successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to logout!");
        }
    }

}
