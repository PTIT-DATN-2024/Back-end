package selling_electronic_devices.back_end.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.AddProductToCart;
import selling_electronic_devices.back_end.Dto.SignupRequest;
import selling_electronic_devices.back_end.Entity.CartDetail;
import selling_electronic_devices.back_end.Entity.Product;
import selling_electronic_devices.back_end.Repository.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private CartDetailRepository cartDetailRepository;


    @Autowired
    private AuthController authController;
    @Autowired
    private ProductController productController;
    @Autowired
    private CategoryController categoryController;


    @Autowired
    private EntityManager entityManager;

    @BeforeAll
    static void setupOnce() {

    }

    @BeforeEach
    void setup() {
        // xóa data trước mỗi test
        cartDetailRepository.deleteAll();
        cartRepository.deleteAll();
        customerRepository.deleteAll();
        // use Mockito to mock data bỏ qua logic service, cơ cấu đầu ra (.when().then())

        // dùng clas controller thay cho tạo thủ công customer, cart, category, product

        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());
        SignupRequest signupRequest = new SignupRequest("smith@gmmail.com", "1234", "Ha Noi", "0343456789", "CUSTOMER", "fullName", "CUSTOMER", avatar);
        authController.signup(signupRequest);
//        Category category = new Category("cate007", "Key Board", "Best choose 2024", "avatar.jpg");
//        categoryRepository.save(category);

        //### add Category + product
        categoryController.createCategory("Electronics", "Best choose 2024", avatar);
        productController.createProduct("cate007", "Ban Phim Rapoo", 12L, "Best", 200.00, 250.00, "500", avatar);


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

    // Add item -> change quantity
    @Test
    void testAddItemToCartNUpdateQuantity_Success() throws Exception {
        // Tạo đối tượng add
        Product product = productRepository.findById("prod007").orElseGet(null);
        AddProductToCart addProductToCart = new AddProductToCart("cust007", product, 10L, 200.00);

        //String jsonContent = "{\"name\":\"Product Name\", \"price\":100.0}";

        //api add
        mockMvc.perform(put("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProductToCart)))// convert object -> strin
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EC").value(0))
                .andExpect(jsonPath("$.MS").value("Update cart successfully."));

        Long quantityBeforeChange = cartDetailRepository.findByProduct(product)
                .map(CartDetail::getQuantity)//.map(cartDetail -> cartDetail.getQuantity())//.map(cartDetail -> {return cartDetail.getQuantity()})
                .orElse(0L);

        System.out.println("==> Quantity before change: " + quantityBeforeChange);

        //api update quantity
        String cartDetailId = "cartDetail007";
        long quantity = 3;
        mockMvc.perform(put("/cart/cartDetail/{cartDetailId}", cartDetailId)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EC").value(0))
                .andExpect(jsonPath("$.MS").value("Updated quantity successfully."));

        long quantityAfterChange = cartDetailRepository.findByProduct(product)
                .map(cartDetail -> cartDetail.getQuantity())
                .orElse(0L);

        System.out.println("===> Quantity after change: " + quantityAfterChange);

        //check quantity[before:after]
        // dùng thư viện AssertJ xác nhận
        assertThat(quantityAfterChange).isEqualTo(quantity);

        // api remove cartDetailId
    }


 }
