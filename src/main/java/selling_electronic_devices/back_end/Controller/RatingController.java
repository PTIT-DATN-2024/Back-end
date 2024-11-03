package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import selling_electronic_devices.back_end.Dto.RatingDto;
import selling_electronic_devices.back_end.Service.RatingService;

@RestController
@RequestMapping("/comment")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @PostMapping
    public ResponseEntity<?> createRating(@RequestBody RatingDto ratingDto) {
        return ResponseEntity.ok(ratingService.createRating(ratingDto));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getRatingsByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(ratingService.getRatingsByProduct(productId));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<?> updateRating(@PathVariable Long ratingId, @RequestBody RatingDto ratingDto) {
        try {
            boolean isUpdated = ratingService.updateRating(ratingId, ratingDto);
            if (isUpdated) {
                return ResponseEntity.ok("Updated rating and review successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found rating and review with ID: " + ratingId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error unexpected occurred.");
        }
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId) {
        try {
            boolean isDeleted = ratingService.deleteRating(ratingId);
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
