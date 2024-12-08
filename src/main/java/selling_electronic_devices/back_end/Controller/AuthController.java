package selling_electronic_devices.back_end.Controller;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

import java.io.File;
import java.io.IOException;
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

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup(
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("address") String address,
            @RequestPart("phone") String phone,
            @RequestPart("role") String role,
            @RequestPart("avatar") MultipartFile avatar) {

        Map<String, Object> response = new HashMap<>();
        SignupRequest signupRequest = new SignupRequest(email, password, address, phone, role);

        try {
            switch (signupRequest.getRole()) {
                case "ADMIN" -> {
                    if (adminRepository.findByEmail(signupRequest.getEmail()) != null) {
                        response.put("EC", 1);
                        response.put("MS", "Email already exists.");
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
                    admin.setPhone(signupRequest.getPhone());
                    admin.setIsDelete("False");

                    admin.setAvatar(saveAvatar(avatar, "ADMIN"));

                    adminRepository.save(admin);

                    response.put("EC", 0);
                    response.put("MS", "Signup Successfully.");
                }
                case "STAFF" -> {
                    if (staffRepository.findByEmail(signupRequest.getEmail()) != null) {
                        response.put("EC", 1);
                        response.put("MS", "Email already exists.");
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
                    staff.setPhone(signupRequest.getPhone());
                    staff.setIsDelete("False");

                    staff.setAvatar(saveAvatar(avatar, "STAFF"));

                    staffRepository.save(staff);

                    response.put("EC", 0);
                    response.put("MS", "Signup Successfully.");
                }
                case "CUSTOMER" -> {
                    if (customerRepository.findByEmail(signupRequest.getEmail()) != null) {
                        response.put("EC", 1);
                        response.put("MS", "Email already exists.");
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
                    customer.setPhone(signupRequest.getPhone());
                    customer.setIsDelete("False");

                    // Lưu ảnh
                    customer.setAvatar(saveAvatar(avatar, "CUSTOMER"));

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
        } catch (DataIntegrityViolationException e) {
            response.put("EC", 1);
            response.put("MS", "Email already exists.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        return ResponseEntity.ok(response);
    }

    public static String saveAvatar(MultipartFile avatar, String role) {
        String avtPath;
        if (role.equals("ADMIN")) {
            avtPath = "D:/electronic_devices/uploads/users/admins/" + avatar.getOriginalFilename();
        } else if (role.equals("STAFF")) {
            avtPath = "D:/electronic_devices/uploads/users/staffs/" + avatar.getOriginalFilename();
        } else {
            avtPath = "D:/electronic_devices/uploads/users/customers/" + avatar.getOriginalFilename();
        }

        File avtFile = new File(avtPath);

        String urlAvtDb = null;
        try {
            avatar.transferTo(avtFile);

            if (role.equals("ADMIN")) {
                urlAvtDb = "http://localhost:8080/uploads/users/admins/" + avatar.getOriginalFilename();
            } else if (role.equals("STAFF")) {
                urlAvtDb = "http://localhost:8080/uploads/users/staffs/" + avatar.getOriginalFilename();
            } else {
                urlAvtDb = "http://localhost:8080/uploads/users/customers/" + avatar.getOriginalFilename();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlAvtDb;
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
