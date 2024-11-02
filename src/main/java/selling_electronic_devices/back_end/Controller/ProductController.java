package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Dto.ProductDto;
import selling_electronic_devices.back_end.Dto.VoteDto;
import selling_electronic_devices.back_end.Entity.Product;
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

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto) {
        productService.createProduct(productDto);
        return ResponseEntity.ok("Added product successfully.");
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts (
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.asc("productId")));
        return ResponseEntity.ok(productService.getAllProducts(pageRequest));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));

        return ResponseEntity.ok(productRepository.findByCategoryId(categoryId, pageRequest).getContent());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productId) {
        return ResponseEntity.ok((productRepository.findById(productId)));
//        Optional<Product> productOp = productRepository.findById(productId);
//        if (productOp.isPresent()) {
//            Product product = productOp.get();
//
//            return ResponseEntity.ok(product);
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found product with product ID: " + productId);
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
            productService.deleteProduct(productId);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found product to delete.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String query, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "7") int limit) {

        return ResponseEntity.ok(productService.searchProduct(query, offset, limit));
    }

    @PostMapping("/rateProduct/{productId}")
    public ResponseEntity<?> rateProduct(@PathVariable String productId, @RequestBody VoteDto voteDto) {
        return ResponseEntity.ok(productService.rateProduct(productId, voteDto));
    }



}
