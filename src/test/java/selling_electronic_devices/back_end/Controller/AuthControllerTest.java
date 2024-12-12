package selling_electronic_devices.back_end.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockMultipartFile;
import selling_electronic_devices.back_end.Dto.AuthenticationRequest;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Jwt.JwtUtil;
import selling_electronic_devices.back_end.Repository.AdminRepository;
import selling_electronic_devices.back_end.Repository.CartRepository;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.StaffRepository;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock// dùng @Mock, @MockBean khi muốn kiểm tra logic (vd: controller, ), mà ko muốn quan tâm tương tác với csdl
    private CustomerRepository customerRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testCreateAuthenticationToken_Success() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password");
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setCustomerId("123");

        when(customerRepository.findByEmail("test@example.com")).thenReturn(customer);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock-jwt-token");

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EC").value(0))
                .andExpect(jsonPath("$.MS").value("Login Successfully."));
//                .andExpect(jsonPath("$.user.jwt").value("mock-jwt-token"));
    }

    @Test
    public void testCreateAuthenticationToken_InvalidCredentials() throws Exception {
        // Mock hành vi của authenticationManager để ném BadCredentialsException
        Mockito.doThrow(new BadCredentialsException("INVALID_CREDENTIALS"))
                .when(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken("wrong@example.com", "wrongpassword"));

        // Tạo request với thông tin đăng nhập sai
        AuthenticationRequest invalidRequest = new AuthenticationRequest("wrong@example.com", "wrongpassword");

        // Gọi API và kiểm tra phản hồi
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized()) // Kiểm tra mã lỗi trả về
                .andExpect(jsonPath("$.EC").value(1)) // Nếu controller không thêm mã EC trong lỗi
                .andExpect(jsonPath("$.MS").value("INVALID_CREDENTIALS")); // Kiểm tra thông báo lỗi
    }



    @Test
    void testSignup_SuccessAdmin() throws Exception {
        // Given
        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "dummy content".getBytes());
        when(adminRepository.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        // When & Then
        mockMvc.perform(multipart("/auth/signup")
                        .file(avatar)
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("address", "address")
                        .param("phone", "1234567890")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EC").value(0))
                .andExpect(jsonPath("$.MS").value("Signup Successfully."));
    }

    @Test
    void testSignup_EmailAlreadyExists() throws Exception {
        // Given
        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "dummy content".getBytes());
        when(customerRepository.findByEmail("test@example.com")).thenReturn(new Customer());

        // When & Then
        mockMvc.perform(multipart("/auth/signup")
                        .file(avatar)
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("address", "address")
                        .param("phone", "1234567890")
                        .param("role", "CUSTOMER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.EC").value(1))
                .andExpect(jsonPath("$.MS").value("Email already exists."));
    }
}

