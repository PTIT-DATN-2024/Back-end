package selling_electronic_devices.back_end.Controller;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
import java.util.Optional;
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
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("EC", 1);
            response.put("MS", "INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Tìm customer
        Customer customer = customerRepository.findByEmail(authenticationRequest.getEmail());
        Admin admin = adminRepository.findByEmail(authenticationRequest.getEmail());
        Staff staff = staffRepository.findByEmail(authenticationRequest.getEmail());
        Object user = null;
        String id = "", role = "", avatar= "";
        if (admin != null) {
            id = admin.getAdminId();
            role = admin.getRole();
            avatar = admin.getAvatar();
            user = admin;
        } else if (staff != null) {
            id = staff.getStaffId();
            role = staff.getRole();
            avatar = staff.getAvatar();
            user = staff;
        } else if (customer != null) {
            id = customer.getCustomerId();
            role = customer.getRole();
            avatar = customer.getAvatar();
            user = customer;
        }

        // Tạo jwt
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse(jwt, id, authenticationRequest.getEmail(), role, avatar, user);
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Login Successfully.");
        response.put("user", loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/signup")//, consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // yêu cầu chặt định dạng form-data, vị dụ nếu ko gửi avatar với type = multipart file-> Ném lỗi tham số hoặc unsupported mediaType
    public ResponseEntity<?> signup(@ModelAttribute SignupRequest signupRequest) { // linh hoạt ánh xạ hơn khi bỏ consumes...mediatype, nhưng ko đảm bảo chuẩn format

        Map<String, Object> response = new HashMap<>();

        try {
            switch (signupRequest.getRole()) {
                case "ADMIN" -> {
                    if (adminRepository.findByEmail(signupRequest.getEmail()) != null) {
                        response.put("EC", 1);
                        response.put("MS", "Email already exists.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    // Tạo mới nếu email chưa tồn tại
                    Admin admin = new Admin();
                    admin.setAdminId(UUID.randomUUID().toString());
                    admin.setEmail(signupRequest.getEmail());
                    admin.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                    admin.setUsername(signupRequest.getUsername());//setUsername("name" + System.currentTimeMillis());
                    admin.setFullName(signupRequest.getFullName());
                    admin.setAddress(signupRequest.getAddress());
                    admin.setRole("ADMIN");
                    admin.setPhone(signupRequest.getPhone());
                    admin.setIsDelete("False");

                    admin.setAvatar(saveAvatar(signupRequest.getAvatar(), "ADMIN"));

                    adminRepository.save(admin);

                    response.put("EC", 0);
                    response.put("MS", "Signup Successfully.");
                }
                case "STAFF" -> {
                    if (staffRepository.findByEmail(signupRequest.getEmail()) != null) {
                        response.put("EC", 1);
                        response.put("MS", "Email already exists.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    // Tạo mới nếu email chưa tồn tại
                    Staff staff = new Staff();
                    staff.setStaffId(UUID.randomUUID().toString());
                    staff.setEmail(signupRequest.getEmail());
                    staff.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                    staff.setUsername(signupRequest.getUsername());
                    staff.setFullName(signupRequest.getFullName());
                    staff.setAddress(signupRequest.getAddress());
                    staff.setRole("STAFF");
                    staff.setPhone(signupRequest.getPhone());
                    staff.setIsDelete("False");

                    staff.setAvatar(saveAvatar(signupRequest.getAvatar(), "STAFF"));

                    staffRepository.save(staff);

                    response.put("EC", 0);
                    response.put("MS", "Signup Successfully.");
                }
                case "CUSTOMER" -> {
                    if (customerRepository.findByEmail(signupRequest.getEmail()) != null) {
                        response.put("EC", 1);
                        response.put("MS", "Email already exists.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    // Tạo mới nếu email chưa tồn tại
                    Customer customer = new Customer();
                    customer.setCustomerId(UUID.randomUUID().toString());
                    //customer.setCustomerId("cust007");
                    customer.setEmail(signupRequest.getEmail());
                    customer.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                    customer.setUsername(signupRequest.getUsername());
                    customer.setFullName(signupRequest.getFullName());
                    customer.setAddress(signupRequest.getAddress());
                    customer.setRole("CUSTOMER");
                    customer.setPhone(signupRequest.getPhone());
                    customer.setIsDelete("False");

                    // Lưu ảnh
                    if (signupRequest.getAvatar() != null && !signupRequest.getAvatar().isEmpty()) {
                        customer.setAvatar(saveAvatar(signupRequest.getAvatar(), "CUSTOMER"));
                    } else {
                        customer.setAvatar("");
                    }

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
        } catch (DataIntegrityViolationException e) { // với test_database ko có constraint unique trong Postgres => thêm unique=true vào "email" Class Customer
            response.put("EC", 1);
            response.put("MS", "Data integrity violation.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("EC", 2);
            response.put("MS", "An error occurred during registration, try again later");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
    public ResponseEntity<?> handleCustomerLogout(@RequestParam(value = "customerId") String customerId) { //Ko có required=false: bắt exception từ param đầu vào -> nếu có lỗi (invalid param) là bỏ qua bên trong + return status mà ko có body => Muốn có thêm body thì phải cấu hình Trong GlobalExceptionHandle
        try {
            if (customerId == null || customerId.isEmpty() || !customerId.matches("[a-zA-Z0-9]+")) {  // bắt exception từ trong (TH ko bị invalid thì work bthuong):  cả khi invalid param ==> thêm "required = false" để cho phép continue vào trong (như này ko cần define lại Exception trong GlobalExceptionHandler)
                throw new IllegalArgumentException("Invalid customerId format.");
            }

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            return optionalCustomer.map(customer ->
                ResponseEntity.ok(Map.of("EC", 0, "MS", "Logout successfully."))
            ).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("EC", 1, "MS", "Not found id."))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 2, "MS", "Invalid or missing parameter."));
        }
    }

}
