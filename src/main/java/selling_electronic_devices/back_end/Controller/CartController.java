package selling_electronic_devices.back_end.Controller;

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


    @GetMapping
    public ResponseEntity<?> getAllCartDetails(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("cartDetailId")));
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("EC", 0);
            response.put("MS", "Get all cart details successfully.");
            response.put("cartDetails", cartDetailRepository.findAll(pageRequest).getContent());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "An error occurred while get all cartDetails.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateCart(@RequestParam AddProductToCart addProductToCart) {
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

    @DeleteMapping("/{cartDetailId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable String cartDetailId) {
        Map<String, Object> response = new HashMap<>();
        Optional<CartDetail> optionalCartDetail = cartDetailRepository.findById(cartDetailId);
        optionalCartDetail.ifPresent(cartDetail -> {
            try {
                cartDetailRepository.deleteById(cartDetailId);
                response.put("EC", 0);
                response.put("MS", "Deleted cartDetail successfully.");
            } catch (Exception e) {
                response.put("EC", 1);
                response.put("MS", "Error Deleting cartDetail! " + e.getMessage());
            }
        });

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cartDetailId}")
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
