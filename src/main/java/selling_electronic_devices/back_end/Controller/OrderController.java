package selling_electronic_devices.back_end.Controller;


import jakarta.persistence.OptimisticLockException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Order;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.OrderRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Service.OrderService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static final String TMN_CODE = "F1I9CX1U";
    private static final String SECRET_KEY = "LERNB4N4HGL1K59T6QP03Y2TR55VSITH";
    private static final String RETURN_URL = "http://localhost:3000/resultPaymentPage";
    private static final String VERSION = "2.1.0";
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.createOrder(orderDto);
            response.put("EC", 0);
            response.put("MS", "Created order successfully.");
            return ResponseEntity.ok(response);
        } catch (DataAccessException e) {
            response.put("EC", 1);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("EC", 2);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //Tạo url thanh toán VNPay + và lưu newOrder vào database
    @PostMapping("/create-payment-url")
    public ResponseEntity<?> createPaymentUrl(@RequestBody OrderDto orderDto) {
        try {
            // Tạo đơn hàng mới
            Order order = orderService.createOrder(orderDto);

            System.out.println("quantity after update:" + productRepository.findById("prod007").get().getTotal());

//            // Tạo tham số VNPay
//            String vnpCommand = "pay";
//            String orderId = order.getOrderId();
//            String amount = String.valueOf((long) (order.getTotal() * 100));
//            String locale = "vn";
//            String currCode = "VND";
//            String ipAddr = InetAddress.getLocalHost().getHostAddress();
//
//            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//            String createDate = formatter.format(calendar.getTime());
////            String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//            calendar.add(Calendar.MINUTE, 15);
//            String vnp_ExpireDate = formatter.format(calendar.getTime());
//
//            //(dùng TreeMap -> tự sắp xếp)
//            Map<String, String> vnpParams = new TreeMap<>();
//            vnpParams.put("vnp_Version", VERSION);
//            vnpParams.put("vnp_Command", vnpCommand);
//            vnpParams.put("vnp_TmnCode", TMN_CODE);
//            vnpParams.put("vnp_Amount", amount);
//            vnpParams.put("vnp_CreateDate", createDate);
//            vnpParams.put("vnp_CurrCode", currCode);
//            vnpParams.put("vnp_IpAddr", ipAddr);
//            vnpParams.put("vnp_Locale", locale);
//            vnpParams.put("vnp_OrderInfo", "MaGD" + orderId);
//            vnpParams.put("vnp_OrderType", "other");
//            vnpParams.put("vnp_ReturnUrl", RETURN_URL);
//            vnpParams.put("vnp_TxnRef", orderId);
////            vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);
////            vnpParams.put("vnp_BankCode", "NCB");
//
//            /*### Tạo chữ ký 1: ban đầu dùng map(entry -> entry.getKey() + "=" + entry.getValue()).collection(Collectors.joining("&")) => bị lỗi sai chữ ký do Bên thứ 3(VNPay) kiểm tra chữ ký dựa theo tham số ĐÃ MÃ HÓA --> Nếu ko mã hóa sẽ dẫn đến Chữ ký mà Server tạo ra KHÔNG KHỚP VỚI CHỮ ký của VNPay*/
//            // Hiểu một cách đơn giản: VNPay sau khi nhận vnParams -> nó cũng tạo ra signatureVNP theo tt của nó -> sau đó nó đem signatureVNP compare với signature của ta
//            // Mà VNPay nó tạo signatureVNP theo các thông số đã được mã hóa trước >><<< còn ta lại ko ===> 2 signature không khớp
//           String queryString = vnpParams.entrySet().stream()
//                   .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII) + "=" +
//                                 URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
//                   .collect(Collectors.joining("&"));
//
//            // Tạo chữ ký 2
////            StringBuilder queryString1 = new StringBuilder(0 );
////            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
////                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
////                    if (queryString1.length() > 0) {
////                        queryString1.append("&");
////                    }
////                    queryString1.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
////                            .append("=")
////                            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
////                }
////            }
//
//            Mac hmac = Mac.getInstance("HmacSHA512");
//            hmac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
////            String signature = new String(Hex.encode(hmac.doFinal(queryString.getBytes(StandardCharsets.UTF_8)))); // hoặc dùng encodeHexString của Apache Commons Codec
//
//            // Mã hóa hex (dùng Apache Commons Codec)
//            String signature = Hex.encodeHexString(hmac.doFinal(queryString.getBytes(StandardCharsets.UTF_8)));
//            vnpParams.put("vnp_SecureHash", signature);
//
//            // Tạo URL thanh toán
//            String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?" + vnpParams.entrySet().stream()
//                    .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));

            // Trả về kết quả
            String paymentUrl = createUrlPayment(order);
            Map<String, Object> response = new HashMap<>();
            response.put("EC", 0);
            response.put("MS", "Success");
            response.put("paymentUrl", paymentUrl);
            response.put("vnp_ReturnUrl", RETURN_URL);
            return ResponseEntity.ok(response);
        } catch (OptimisticLockingFailureException e) {
            //Thread.sleep();
            int maxAttempts = 0;
            while (maxAttempts < 17) {  // Tăng số lần retry (or use "sleep" before update): nếu muốn xử lý được càng nhiều request đồng thời hơn thay vì ném ngoại lệ sớm BỞI VÌ: với số lần thử nhỏ << trong khi số request đồng thời lớn>> thì dẫn đến việc tất cả các lần thử vẫn bị DÍNH việc tương tranh với Transaction khác.
                try {
                    Order orderRetry = orderService.createOrder(orderDto);
                    String paymentUrl = createUrlPayment(orderRetry);

                    return ResponseEntity.ok(Map.of(
                            "EC", 0,
                            "MS","Retry SUCCESS at number test = " + maxAttempts,
                            "paymentUrl", paymentUrl,
                            "vnp_ReturnUrl", RETURN_URL));
                } catch (OptimisticLockingFailureException optimisticEx) {
                    maxAttempts ++;
                    continue;
                } catch (Exception exception) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("EC", 1, "MS", "Error creating payment URL", "error", exception.getMessage()));
                }
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("EC", 1, "MS", "Optimistic Lock Exception: Product quantity was updated by another transaction.", "error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("EC", 1, "MS", "Error creating payment URL", "error", e.getMessage()));
        }
    }

    public static String createUrlPayment(Order order) {
        try {
            // Tạo tham số VNPay
            String vnpCommand = "pay";
            String orderId = order.getOrderId();
            String amount = String.valueOf((long) (order.getTotal() * 100));
            String locale = "vn";
            String currCode = "VND";
            String ipAddr = InetAddress.getLocalHost().getHostAddress();

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String createDate = formatter.format(calendar.getTime());
//            String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            calendar.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(calendar.getTime());

            //(dùng TreeMap -> tự sắp xếp)
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", VERSION);
            vnpParams.put("vnp_Command", vnpCommand);
            vnpParams.put("vnp_TmnCode", TMN_CODE);
            vnpParams.put("vnp_Amount", amount);
            vnpParams.put("vnp_CreateDate", createDate);
            vnpParams.put("vnp_CurrCode", currCode);
            vnpParams.put("vnp_IpAddr", ipAddr);
            vnpParams.put("vnp_Locale", locale);
            vnpParams.put("vnp_OrderInfo", "MaGD" + orderId);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_ReturnUrl", RETURN_URL);
            vnpParams.put("vnp_TxnRef", orderId);
//            vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);
//            vnpParams.put("vnp_BankCode", "NCB");

            /*### Tạo chữ ký 1: ban đầu dùng map(entry -> entry.getKey() + "=" + entry.getValue()).collection(Collectors.joining("&")) => bị lỗi sai chữ ký do Bên thứ 3(VNPay) kiểm tra chữ ký dựa theo tham số ĐÃ MÃ HÓA --> Nếu ko mã hóa sẽ dẫn đến Chữ ký mà Server tạo ra KHÔNG KHỚP VỚI CHỮ ký của VNPay*/
            // Hiểu một cách đơn giản: VNPay sau khi nhận vnParams -> nó cũng tạo ra signatureVNP theo tt của nó -> sau đó nó đem signatureVNP compare với signature của ta
            // Mà VNPay nó tạo signatureVNP theo các thông số đã được mã hóa trước >><<< còn ta lại ko ===> 2 signature không khớp
            String queryString = vnpParams.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII) + "=" +
                            URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                    .collect(Collectors.joining("&"));

        /* Tạo chữ ký 2
            StringBuilder queryString1 = new StringBuilder(0 );
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (queryString1.length() > 0) {
                        queryString1.append("&");
                    }
                    queryString1.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
                }
            }*/

            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            //String signature = new String(Hex.encode(hmac.doFinal(queryString.getBytes(StandardCharsets.UTF_8)))); // hoặc dùng encodeHexString của Apache Commons Codec

            // Mã hóa hex (dùng Apache Commons Codec)
            String signature = Hex.encodeHexString(hmac.doFinal(queryString.getBytes(StandardCharsets.UTF_8)));
            vnpParams.put("vnp_SecureHash", signature);

            // Tạo URL thanh toán
            String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?" + vnpParams.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));

            return paymentUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error generating payment URL", e);
        }
    }

    @GetMapping("/handle-return")
    public ResponseEntity<?> handleReturn(@RequestParam Map<String, String> vnpParams) {
        try {
            String secureHash = vnpParams.remove("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHashType");

            if (vnpParams.isEmpty() || secureHash == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 4, "MS", "Invalid parameters."));
            }

            // Sắp xếp các tham số
            String sortedParams = vnpParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII) + "="
                                + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                    .collect(Collectors.joining("&"));

            // Tọa lại signature
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            String generatedHash = Hex.encodeHexString(hmac.doFinal(sortedParams.getBytes(StandardCharsets.UTF_8)));

            if (!secureHash.equals(generatedHash)) {
                log.error("Signature mismatch. Received: {}, Generated: {}", secureHash, generatedHash);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 1, "MS", "Invalid signature."));
            }

            // Đảm báo tránh th .get("cartDetails") ko phải là List<CartDetail>
//                Object rawCartDetails = vnpParams.get("cartDetails");
//                List<CartDetail> cartDetails;
//                if (rawCartDetails instanceof List<?>) {
//                    cartDetails = ((List<?>) rawCartDetails).stream()
//                            .filter(CartDetail.class::isInstance)
//                            .map(CartDetail.class::cast)
//                            .collect(Collectors.toList());
//                } else {
//                    cartDetails = Collections.emptyList();
//                }
            // cách 2:
//                Object rawCartDetails = vnpParams.get("cartDetails");
//
//                List<CartDetail> cartDetails = Optional.ofNullable(rawCartDetails)
//                        .filter(List.class::isInstance)
//                        .map(list -> ((List<?>) list).stream()
//                                .filter(CartDetail.class::isInstance)
//                                .map(CartDetail.class::cast)
//                                .collect(Collectors.toList()))
//                        .orElse(Collections.emptyList());
////                @SuppressWarnings("unchecked")

//                //Cách 3
//                List<CartDetail> cartDetails = (List<CartDetail>) vnpParams.get("cartDetails");
//                if (cartDetails == null) {
//                    cartDetails = Collections.emptyList();
//                }

            // Kiểm tra kết quả giao dịch

            // vnp_ResponseCode="24" = Khách hàng hủy
            if ("00".equals(vnpParams.get("vnp_ResponseCode"))) {
                String orderId  = vnpParams.get("vnp_TxnRef");
                orderService.updateStatus(orderId, "CLH");
                return ResponseEntity.ok(Map.of("EC", 0, "MS", "Payment successfully."));
            } else if ("24".equals(vnpParams.get("vnp_ResponseCode"))){
                String orderId = vnpParams.get("vnp_TxnRef");
                orderService.updateStatus(orderId, "HTT");
                return ResponseEntity.ok(Map.of("EC", 0, "MS", "Customer cancel payment."));
            }else {
                return ResponseEntity.ok(Map.of("EC", 1, "MS", "Payment failed", "code", vnpParams.get("vnp_ResponseCode")));
            }

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("EC", 2, "MS", "Not found order", "error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 2, "MS", "Invalid argument: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("EC", 3, "MS", "Error handling payment return", "error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        return ResponseEntity.ok(orderService.getAllOrders(offset, limit));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable String orderId){
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get an Order success!");
        response.put("product", orderRepository.findById(orderId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrderByCustomerId(@PathVariable String customerId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        return optionalCustomer.map(customer -> {
            response.put("EC", 0);
            response.put("MS", "Get order by customerId successfully.");
            response.put("orders", orderRepository.findByCustomer(customer));
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("EC", 0);
            response.put("MS", "The Customer has no orders.");
            response.put("orders", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    //*@PutMapping("/{orderId}")
    /**
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestBody OrderDto orderDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.updateOrder(orderId, orderDto);
            response.put("EC", 0);
            response.put("MS", "Updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "Error while updating!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }*/

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.updateOrder(orderId, status);
            response.put("EC", 0);
            response.put("MS", "Updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "Error while updating!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(orderService.deleteOrder(orderId));
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("EC", 1);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
