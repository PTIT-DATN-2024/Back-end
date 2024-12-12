package selling_electronic_devices.back_end.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.AddProductToCart;
import selling_electronic_devices.back_end.Dto.SignupRequest;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Product;
import selling_electronic_devices.back_end.Repository.CartRepository;
import selling_electronic_devices.back_end.Repository.CategoryRepository;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private AuthController authController;
    @Autowired
    private ProductController productController;


    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        // xóa data trước mỗi test
        cartRepository.deleteAll();
        customerRepository.deleteAll();

        // Thêm data Cust
//        Customer customer = new Customer();
//        customer.setCustomerId("cust007");
//        customer.setEmail("smith@gmail.com");
//        customer.setPassword(new BCryptPasswordEncoder().encode("1234"));
//        //customer.setPassword(passwordEncoder.encode("1234"));
//        customer.setUserName("CUS" + System.currentTimeMillis());
//        customer.setFullName("customer" + System.currentTimeMillis());
//        customer.setAddress("Ha Noi");
//        customer.setPhone("0344248679");
//        customer.setRole("CUSTOMER");
//        customer.setIsDelete("False");
//
//        customerRepository.save(customer);

        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());
        SignupRequest signupRequest = new SignupRequest("smith@gmmail.com", "1234", "Ha Noi", "0343456789", "CUSTOMER", avatar);
        authController.signup(signupRequest);

        // new Category + product
        Category category = new Category("cate007", "Key Board", "Best choise 2024", "avatar.jpg");
        categoryRepository.save(category);

        productController.createProduct("cate007", )
        Product product = new Product();



        AddProductToCart addProductToCart =  new AddProductToCart("cust007", )

    }

    @Test
    void testGetAllItemInCart_Success() throws Exception {
        String customerId = "cust007";

        mockMvc.perform(get("/cart/customer/{customerId}", customerId)//perform(MockMvcRequestBuilders.get("/cart/customer/{customerId}", customerId)
                        .param("offset", "0").param("limit", "10"))
                .andExpect(status().isOk())//.andExpect(status().isOk())
                .andExpect(jsonPath("$.EC").value(0))//.andExpect(MockMvcResultMatchers.jsonPath("$.EC").value(0))
                .andExpect(jsonPath("$.MS").value("Get all item form Cart successfully."));
    }

    @Test
    void testGetAllItemInCart_NotFound() throws Exception {
        String customerId = "wrongId";

        mockMvc.perform(get("/cart/customer/{customerId}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.EC").value(1))
                .andExpect(jsonPath("$.MS").value("Not found cart."));
    }

    @Test
    void testGetAllItemInCart_InvalidParameter() throws Exception {
        String customerId = "!@#";

        mockMvc.perform(get("/cart/customer/{customerId}", customerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.EC").value(2))
                .andExpect(jsonPath("$.MS").value("An error occurred while get all cartDetails."));

    }

    // Add quantity -> change quantity
    @Test
    void testAddItemToCart_Success() throws Exception {

        String cartDetail =
    }
 }
