package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Service.ProductReviewService;

@RestController
@RequestMapping("/comment")
public class ProductReviewController {
    @Autowired
    private ProductReviewService productReviewService;

    @PostMapping
    public ResponseEntity<?> createRating(@RequestBody ProductReviewDto productReviewDto) {
        return ResponseEntity.ok(productReviewService.createProductReview(productReviewDto));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getRatingsByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productReviewService.getProductReviewsByProduct(productId));
    }

    @PutMapping("/{productReviewId}")
    public ResponseEntity<?> updateRating(@PathVariable String productReviewId, @RequestBody ProductReviewDto productReviewDto) {
        try {
            boolean isUpdated = productReviewService.updateProductReview(productReviewId, productReviewDto);
            if (isUpdated) {
                return ResponseEntity.ok("Updated rating and review successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found rating and review with ID: " + productReviewId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error unexpected occurred.");
        }
    }

    @DeleteMapping("/{productReviewId}")
    public ResponseEntity<?> deleteRating(@PathVariable String productReviewId) {
        try {
            boolean isDeleted = productReviewService.deleteProductReview(productReviewId);
            if (isDeleted) {
                return ResponseEntity.ok("Rating deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found rating.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error unexpected occurred.");
        }
    }



}
