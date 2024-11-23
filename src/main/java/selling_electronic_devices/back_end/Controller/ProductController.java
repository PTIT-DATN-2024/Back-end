package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.ProductDto;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Repository.CategoryRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Service.ProductService;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.createProduct(productDto);
            response.put("EC", 0);
            response.put("MS", "Created product successfully.");
            return ResponseEntity.ok(response);
        } catch (DataAccessException e) {
            response.put("EC", 1);
            response.put("MS", "Error while creating: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("EC", 2);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts (
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(productService.getAllProducts(offset, limit));
    }

    @GetMapping("/list-products/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get products by category successfully.");
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            response.put("products", productRepository.findByCategory(category, pageRequest).getContent());
        } else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found category.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get product by ID successfully.");
        response.put("product", productRepository.findById(productId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestBody ProductDto productDto) {
        try {
            productService.updateProduct(productId, productDto);
            return ResponseEntity.ok("Updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found with ID " + productId + " to update.");
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        try {
            return ResponseEntity.ok(productService.deleteProduct(productId));
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("EC", 1);
            response.put("MS", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(@RequestParam String query, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "7") int limit) {

        return ResponseEntity.ok(productService.searchProduct(query, offset, limit));
    }

//    @PostMapping("/rateProduct/{productId}")
//    public ResponseEntity<?> rateProduct(@PathVariable String productId, @RequestBody ProductReviewDto productReviewDto) {
//        try {
//            return ResponseEntity.ok(productService.rateProduct(productId, productReviewDto));
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("EC", 1);
//            response.put("MS", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }
}
