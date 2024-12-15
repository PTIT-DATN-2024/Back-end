package selling_electronic_devices.back_end.Controller;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Entity.CartDetail;
import selling_electronic_devices.back_end.Entity.Order;
import selling_electronic_devices.back_end.Jwt.JwtUtil;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.OrderRepository;
import selling_electronic_devices.back_end.Service.OrderService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private JwtUtil jwtUtil;

    private static final String SECRET_KEY = "Ghd73mJ2mX9sR3pQ5L8vY2cS7kE4rT9fW1xQ7hN6bZ3pD2gF4uZ";

    @Test
    void testCreatePaymentUrlSuccess() throws Exception {
        // Mock OrderDto input
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerId("customer1");
        orderDto.setStaffId("staff1");
        orderDto.setTotal(560000.0);

        List<CartDetail> cartDetails = List.of(new CartDetail());
        orderDto.setCartDetails(cartDetails);

        // Mock Order output
        Order mockOrder = new Order();
        mockOrder.setOrderId("testOrder123");
        mockOrder.setTotal(560000.0);

        Mockito.when(orderService.createOrder(Mockito.any(OrderDto.class))).thenReturn(mockOrder);

        mockMvc.perform(post("/create-payment-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"customer1\", \"staffId\":\"staff1\", \"total\":560000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EC").value(0))
                .andExpect(jsonPath("$.MS").value("Success"))
                .andExpect(jsonPath("$.paymentUrl").exists())
                .andExpect(jsonPath("$.vnp_ReturnUrl").exists());
    }

    @Test
    void testCreatePaymentUrlFailure() throws Exception {
        Mockito.when(orderService.createOrder(Mockito.any(OrderDto.class)))
                .thenThrow(new RuntimeException("Failed to create order"));

        mockMvc.perform(post("/create-payment-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"customer1\", \"staffId\":\"staff1\", \"total\":560000.0}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.EC").value(1))
                .andExpect(jsonPath("$.MS").value("Error creating payment URL"));
    }

    @Test
    void testHandleReturnSuccess() throws Exception {
        // Mock valid signature generation
        String validHash = generateValidHash("vnp_ResponseCode=00&vnp_TxnRef=testOrder123");

        Mockito.doNothing().when(orderService).updateStatus("testOrder123", "Paid");

        mockMvc.perform(get("/handle-return?vnp_ResponseCode=00&vnp_TxnRef=testOrder123&vnp_SecureHash=" + validHash))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EC").value(0))
                .andExpect(jsonPath("$.MS").value("Payment successfully."));
    }

    @Test
    void testHandleReturnInvalidSignature() throws Exception {
        mockMvc.perform(get("/handle-return?vnp_ResponseCode=00&vnp_TxnRef=testOrder123&vnp_SecureHash=invalidHash"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.EC").value(1))
                .andExpect(jsonPath("$.MS").value("Invalid signature."));
    }

    @Test
    void testHandleReturnOrderNotFound() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Order not found"))
                .when(orderService).updateStatus("nonExistentOrder", "Paid");

        String validHash = generateValidHash("vnp_ResponseCode=00&vnp_TxnRef=nonExistentOrder");

        mockMvc.perform(get("/handle-return?vnp_ResponseCode=00&vnp_TxnRef=nonExistentOrder&vnp_SecureHash=" + validHash))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.EC").value(2))
                .andExpect(jsonPath("$.MS").value("Not found order"));
    }

    private String generateValidHash(String sortedParams) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        hmac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        return Hex.encodeHexString(hmac.doFinal(sortedParams.getBytes(StandardCharsets.UTF_8)));
    }
}
