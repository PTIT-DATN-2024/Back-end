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
import selling_electronic_devices.back_end.Entity.Admin;
import selling_electronic_devices.back_end.Entity.Cart;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Staff;
import selling_electronic_devices.back_end.Jwt.JwtUtil;
import selling_electronic_devices.back_end.Repository.AdminRepository;
import selling_electronic_devices.back_end.Repository.CartRepository;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.StaffRepository;

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
    private AdminRepository adminRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CartRepository cartRepository;

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
            throw new Exception("INVALID_CREDENTIALS ", e);
        }

        // Tìm customer
        Customer customer = customerRepository.findByEmail(authenticationRequest.getEmail());
        Admin admin = adminRepository.findByEmail(authenticationRequest.getEmail());
        Staff staff = staffRepository.findByEmail(authenticationRequest.getEmail());
        String id = "", role = "";
        if (admin != null) {
            id = admin.getAdminId();
            role = admin.getRole();
        } else if (staff != null) {
            id = staff.getStaffId();
            role = staff.getRole();
        } else if (customer != null) {
            id = customer.getCustomerId();
            role = customer.getRole();
        }

        // Tạo jwt
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse(jwt, id, authenticationRequest.getEmail(), role, "1728958738001.jpg");
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Login Successfully.");
        response.put("user", loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        Map<String, Object> response = new HashMap<>();

        switch (signupRequest.getRole()) {
            case "ADMIN" -> {
                if (adminRepository.findByEmail(signupRequest.getEmail()) != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("Email already exists.");
                }

                // Tạo mới nếu email chưa tồn tại
                Admin admin = new Admin();
                admin.setAdminId(UUID.randomUUID().toString());
                admin.setEmail(signupRequest.getEmail());
                admin.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                admin.setUsername("name" + System.currentTimeMillis());
                admin.setFullName("adm" + System.currentTimeMillis());
//            admin.setAddress("Ha Noi");
                admin.setRole("ADMIN");
                admin.setAvatar(signupRequest.getAvatar());
                admin.setPhone(signupRequest.getPhone());
                admin.setIsDelete("false");

                adminRepository.save(admin);

                response.put("EC", 0);
                response.put("MS", "Signup Successfully.");
            }
            case "STAFF" -> {
                if (staffRepository.findByEmail(signupRequest.getEmail()) != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("Email already exists.");
                }

                // Tạo mới nếu email chưa tồn tại
                Staff staff = new Staff();
                staff.setStaffId(UUID.randomUUID().toString());
                staff.setEmail(signupRequest.getEmail());
                staff.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                staff.setUsername("name" + System.currentTimeMillis());
                staff.setFullName("staff" + System.currentTimeMillis());
//            staff.setAddress("Ha Noi");
                staff.setRole("STAFF");
                staff.setAvatar(signupRequest.getAvatar());
                staff.setPhone(signupRequest.getPhone());
                staff.setIsDelete("false");

                staffRepository.save(staff);

                response.put("EC", 0);
                response.put("MS", "Signup Successfully.");
            }
            case "CUSTOMER" -> {
                if (customerRepository.findByEmail(signupRequest.getEmail()) != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("Email already exists.");
                }

                // Tạo mới nếu email chưa tồn tại
                Customer customer = new Customer();
                customer.setCustomerId(UUID.randomUUID().toString());
                customer.setEmail(signupRequest.getEmail());
                customer.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                customer.setUserName("CUS" + System.currentTimeMillis());
                customer.setFullName("customer" + System.currentTimeMillis());
                customer.setAddress("Ha Noi");
                customer.setRole("CUSTOMER");
                customer.setAvatar(signupRequest.getAvatar());
                customer.setPhone(signupRequest.getPhone());
                customer.setIsDelete("false");

                customerRepository.save(customer);

                // tạo giỏ hàng
                Cart cart = new Cart();
                cart.setCartId(UUID.randomUUID().toString());
                cart.setCustomer(customer);
                cartRepository.save(cart);

                response.put("EC", 0);
                response.put("MS", "Signup Successfully.");
            }
        }

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
