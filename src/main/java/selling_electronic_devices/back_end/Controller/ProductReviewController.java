package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Service.ProductReviewService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class ProductReviewController {
    @Autowired
    private ProductReviewService productReviewService;

    @PostMapping
    public ResponseEntity<?> createRating(@RequestBody ProductReviewDto productReviewDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            productReviewService.createProductReview(productReviewDto);
            response.put("EC", 0);
            response.put("MS", "Created successfully.");
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

    @GetMapping
    public ResponseEntity<?> getAllProductReviews(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(productReviewService.getAllProductReviews(offset, limit));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getRatingsByProduct(@PathVariable String productId) {
        try {
            return ResponseEntity.ok(productReviewService.getProductReviewsByProduct(productId));
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("EC", 0);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{productReviewId}")
    public ResponseEntity<?> updateRating(@PathVariable String productReviewId, @RequestBody ProductReviewDto productReviewDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isUpdated = productReviewService.updateProductReview(productReviewId, productReviewDto);
            if (isUpdated) {
                response.put("EC", 0);
                response.put("MS", "Get productReview by ID successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("EC", 1);
                response.put("MS", "Not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{productReviewId}")
    public ResponseEntity<?> deleteRating(@PathVariable String productReviewId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDeleted = productReviewService.deleteProductReview(productReviewId);
            if (isDeleted) {
                response.put("EC", 0);
                response.put("MS", "Deleted successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("EC", 1);
                response.put("MS", "Not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
