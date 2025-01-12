//package selling_electronic_devices.back_end.Controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import selling_electronic_devices.back_end.Dto.AuthenticationRequest;
//import selling_electronic_devices.back_end.Entity.Customer;
//import selling_electronic_devices.back_end.Repository.CartRepository;
//import selling_electronic_devices.back_end.Repository.CustomerRepository;
//
//import java.util.UUID;
//
////import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-test.properties")
//class AuthControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    //@MockBean // Mock repository, không cần kết nối cơ sở dữ liệu thực
//    //Mockito mô phỏng hành vi của repository:  when(productRepository.findById("prod007")).thenReturn(Optional.of(mockProduct));
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    //@BeforeAll // dùng khi thiết lập chung có thể share giữa các @Test: setup sẽ chạy 1 lần duy nhất để thiết lập cho tất cả @Test: dung khi thiết lập nặng.
//    @BeforeEach // dùng khi khởi tạo hoặc reset trạng thái cho từng Test, đảm bảo các @Test chạy độc lập.
//    void setup() {
//        // Xóa sạch cơ sở dữ liệu trước mỗi test
//        cartRepository.deleteAll(); // Xóa cart trước vì Cart --- reference ---> Customer
//        customerRepository.deleteAll();
//
//        // Thêm dữ liệu mẫu
//        Customer customer = new Customer();
//        customer.setCustomerId("cust007");
//        customer.setEmail("test@example.com");
//        customer.setPassword(new BCryptPasswordEncoder().encode("1234"));
//        customer.setRole("CUSTOMER");
//        customer.setUsername("CUS" + System.currentTimeMillis());
//        customer.setFullName("customer" + System.currentTimeMillis());
//        customer.setAddress("Ha Noi");
//        customer.setPhone("0989123456");
//        customer.setIsDelete("False");
//
//        customerRepository.save(customer);
//    }
//
//
//    @Test
//    void testLogin_Success() throws Exception {
//        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "1234");
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.EC").value(0))
//                .andExpect(jsonPath("$.MS").value("Login Successfully."))
//                .andExpect(jsonPath("$.user.email").value("test@example.com"));
//    }
//
//    @Test
//    void testLogin_InvalidCredentials() throws Exception {
//        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "wrongpassword");
//
//        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.EC").value(1))
//                .andExpect(jsonPath("$.MS").value("INVALID_CREDENTIALS"));
//    }
//
//    @Test
//    void testSignup_Success() throws Exception {
//        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());
//        MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders.multipart("/auth/signup")
//                .file(avatar)
//                .param("email", "newuser@example.com")
//                .param("password", "newpassword")
//                .param("phone", "123456789")
//                .param("role", "CUSTOMER");
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.EC").value(0))
//                .andExpect(jsonPath("$.MS").value("Signup Successfully."));
//    }
//
//    @Test
//    void testSignup_Fail() throws Exception {
//        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());
//        MockMultipartHttpServletRequestBuilder requestBuilder = (MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders.multipart("/auth/signup")
//                .file(avatar)
//                .param("email", "test@example.com") // Email đã tồn tại trong setup()
//                .param("password", "1234")
//                .param("phone", "0444545878")
//                .param("role", "CUSTOMER");
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().isBadRequest()) // Expecting a Bad Request due to existing email
//                .andExpect(jsonPath("$.EC").value(1))
//                .andExpect(jsonPath("$.MS").value("Email already exists."));
//    }
//
//    @Test
//    void testLogout_Success() throws Exception {
//        mockMvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON)
//                        .param("customerId", "cust007")) // @RequestParam
//                        //.content("{\"customerId\":\"cust007\"}")) // th @RequestBody
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.EC").value(0))
//                .andExpect(jsonPath("$.MS").value("Logout successfully."));
//    }
//
//    @Test
//    void testLogout_NotFound() throws Exception { // Post: invalid parameter => Exception default return = MissingServletRequestParameterException [400], muốn xử lý => define lại
//        mockMvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON).param("customerId", "cust009"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.EC").value(1))
//                .andExpect(jsonPath("$.MS").value("Not found id."));
//    }
//
//    @Test
//    void testLogout_InvalidParameter() throws Exception {
//        mockMvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON).param("customderId", ""))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.EC").value(2))
//                .andExpect(jsonPath("$.MS").value("Invalid or missing parameter."));
//    }
//
//}
//
