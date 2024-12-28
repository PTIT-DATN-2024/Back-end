package selling_electronic_devices.back_end.Controller;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.AddProductToCart;
import selling_electronic_devices.back_end.Entity.Cart;
import selling_electronic_devices.back_end.Entity.CartDetail;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Repository.CartDetailRepository;
import selling_electronic_devices.back_end.Repository.CartRepository;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;
import selling_electronic_devices.back_end.Service.ProductReviewService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductReviewService productReviewService;


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getAllCartDetails(
            @PathVariable String customerId, // PathVariable: bắt buộc phải có tham số, ko như ReuquestParam(required = false) có thể để trông
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        try {
            if (customerId == null || customerId.isEmpty()){ //|| !customerId.matches("[a-zA-Z0-9]+")) {  // bắt exception từ trong (TH ko bị invalid thì work bthuong):  cả khi invalid param ==> thêm "required = false" để cho phép continue vào trong - ngay cả khi invalid parameter.
                throw new IllegalArgumentException("Invalid customerId format.");
            }

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            Cart cart = null;
            if (optionalCustomer.isPresent()) {
                cart = cartRepository.findByCustomer(optionalCustomer.get());
            }

            Map<String, Object> response = new HashMap<>();
                if (cart != null) {
                    PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("cartDetailId")));

                    response.put("EC", 0);
                    response.put("MS", "Get all item form Cart successfully.");
                    response.put("cart", cartDetailRepository.findByCart(cart, pageRequest).getContent());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("EC", 1, "MS", "Not found cart."));
                }

                return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 2, "MS", "An error occurred while get all cartDetails."));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateCart(@RequestBody AddProductToCart addProductToCart) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Customer> optionalCustomer = customerRepository.findById(addProductToCart.getCustomerId());
            Optional<CartDetail> optionalCartDetail = cartDetailRepository.findByProduct(addProductToCart.getProduct());

            Cart cart = cartRepository.findByCustomer(optionalCustomer.orElseGet(null)); // tìm giỏ hàng của customerId

            //Nếu product đã có trong giỏ hàng của customerId
            //CartDetail cartDetail = new CartDetail(); // (1) "có thể" làm nó thay đổi tham chiếu nếu if thỏa mãn => bt lamda (2) báo lỗi
            if (optionalCartDetail.isPresent() && optionalCartDetail.get().getCart().equals(cart)) {
                CartDetail cartDetail = optionalCartDetail.get(); //(1)
                cartDetail.setQuantity(cartDetail.getQuantity() + addProductToCart.getQuantity());

                cartDetailRepository.save(cartDetail);
            } else {
                CartDetail cartDetail = new CartDetail(); // ko lỗi, do ở dưới ko có nguy cơ "có thể" hay hành động làm thay đổi tham chiếu của cartDetail.
                optionalCustomer.ifPresent(customer -> { //(2)
                    cartDetail.setCart(cartRepository.findByCustomer(customer)); // chỉ thay đổi thuộc tính, ko thay đổi tham chiếu
                });

                cartDetail.setCartDetailId(UUID.randomUUID().toString());
//                cartDetail.setCartDetailId("cartDetail007");
                cartDetail.setProduct(addProductToCart.getProduct());
                cartDetail.setQuantity(addProductToCart.getQuantity());
                cartDetail.setTotalPrice(addProductToCart.getTotalPrice());

                cartDetailRepository.save(cartDetail);
            }
            response.put("EC", 0);
            response.put("MS", "Update cart successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "An error occurred when update Cart.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/cartDetail/{cartDetailId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable String cartDetailId) {
        Map<String, Object> response = new HashMap<>();
        Optional<CartDetail> optionalCartDetail = cartDetailRepository.findById(cartDetailId);
        optionalCartDetail.ifPresent(cartDetail -> {
            try {
                cartDetailRepository.deleteById(cartDetailId);// ban đầu ko xóa được do List cartDetails trong Cart (cascade) -> hibernate ko tự xóa (cartDetail) do lúc này Cart đang là chủ thể của cascade -> hoặc bỏ List cartDetails(của Cart) hoặc thêm cascade cho trường cart (trong CartDetail) (Lưu ý khi xóa CartDetail -> xóa mât Cart liên quan(dù quan hệ ManyToOne)
                response.put("EC", 0);
                response.put("MS", "Deleted cartDetail successfully.");
            } catch (Exception e) {
                response.put("EC", 1);
                response.put("MS", "Error Deleting cartDetail! " + e.getMessage());
            }
        });

        return ResponseEntity.ok(response);
    }


    @PutMapping("/cartDetail/{cartDetailId}")
    public ResponseEntity<?> updateItemInCart(@PathVariable String cartDetailId, @RequestParam Long quantity) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra quantity hợp lệ
        if (quantity < 0) {
            response.put("EC", 2);
            response.put("MS", "Invalid quantity. Must be 0 or greater.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<CartDetail> optionalCartDetail = cartDetailRepository.findById(cartDetailId);

        optionalCartDetail.ifPresentOrElse(
                cartDetail -> {
                    if (quantity == 0) {
                        cartDetailRepository.delete(cartDetail);
                    } else {
                        cartDetail.setQuantity(quantity);
                        cartDetailRepository.save(cartDetail);
                    }
                    response.put("EC", 0);
                    response.put("MS", "Updated quantity successfully.");
                },
                () -> {
                    response.put("EC", 1);
                    response.put("MS", "Cart detail with ID" + cartDetailId + " not found.");
                }
        );
        if (response.get("EC").equals(1)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }




}
