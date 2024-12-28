package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.ProductDiscountDto;
import selling_electronic_devices.back_end.Entity.ProductDiscount;
import selling_electronic_devices.back_end.Repository.ProductDiscountRepository;
import selling_electronic_devices.back_end.Service.ProductDiscountService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/discount")
public class ProductDiscountController {

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private ProductDiscountService productDiscountService;

    @PostMapping
    public ResponseEntity<?> createProductDiscount(@RequestBody ProductDiscountDto productDiscountDto) {
        try {
            productDiscountService.createProductDiscount(productDiscountDto);

            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Created productDiscount."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 0, "MS", "Created productDiscount.", "error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProductDiscounts(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(value = "limit", defaultValue = "10000") int limit) {
        try {
            PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("expiredDate")));

            return ResponseEntity.ok(Map.of("EC", 0,"MS", "Get all product discount successfully.","productDiscounts", productDiscountService.getAllProductDiscounts(pageRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 1,"MS", "An error occurred while receiving all discounts.","error", e.getMessage()));
        }
    }

    @GetMapping("/{productDiscountId}")
    public ResponseEntity<?> getAProductDiscount(@PathVariable String productDiscountId) {
        Optional<ProductDiscount> optionalProductDiscount = productDiscountRepository.findById(productDiscountId);
        return optionalProductDiscount.map(productDiscount ->  ResponseEntity.ok(Map.of("EC", 0,"MS", "Get a productDiscount successfully.","productDiscount", productDiscount)))
                .orElseGet(() -> ResponseEntity.ok(Map.of("EC", 1,"MS", "An error occurred while getting discount.")));
    }

    @PutMapping("/{productDiscountId}")
    public ResponseEntity<?> updateProductDiscount(@PathVariable String productDiscountId, @RequestBody ProductDiscountDto productDiscountDto) {
        try {
            productDiscountService.updateProductDiscount(productDiscountId, productDiscountDto);

            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Updated discount."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 1, "MS", "Update failed."));
        }
    }

    @DeleteMapping("/{productDiscountId}")
    public ResponseEntity<?> deleteProductDiscount(@PathVariable String productDiscountId) {
        try {
            productDiscountService.deleteProductDiscount(productDiscountId);

            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Deleted discount successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("EC", 0, "MS", "Deleted discount successfully."));
        }
    }

}
