package selling_electronic_devices.back_end.Controller;

import org.apache.coyote.Request;
import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.ProductDto;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct( // @ModelAttribute
            @RequestParam(value = "categoryId") String categoryId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "total") Long total,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "importPrice") Double importPrice,
            @RequestParam(value = "sellingPrice") Double sellingPrice,
            @RequestParam(value = "weight") String weight,
            @RequestParam(value = "avatar") MultipartFile avatar,
            @RequestParam(value = "avatar1") MultipartFile avatar1,
            @RequestParam(value = "avatar2") MultipartFile avatar2) {

        ProductDto productDto = new ProductDto(categoryId, name, total, description, importPrice, sellingPrice, weight);
        List<MultipartFile> avatars = new ArrayList<>(Arrays.asList(avatar, avatar1, avatar2));
        //Collections.addAll(avatars, avatar, avatar1, avatar2);
        return ResponseEntity.ok(productService.createProduct(productDto, avatars));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts (
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        return ResponseEntity.ok(productService.getAllProducts(offset, limit));
    }

    @GetMapping("/list-products/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));
        Map<String, Object> response = new HashMap<>();

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            response.put("EC", 0);
            response.put("MS", "Get products by category successfully.");
            Category category = optionalCategory.get();
            response.put("products", productRepository.findByCategory(category, pageRequest).getContent());

            return ResponseEntity.ok(response);
        } else  {
            response.put("EC", 1);
            response.put("MS", "Error Getting products by category.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get product by ID successfully.");
        response.put("product", productRepository.findById(productId));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable String productId,
            @RequestParam(value = "categoryId") String categoryId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "total") Long total,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "importPrice") Double importPrice,
            @RequestParam(value = "sellingPrice") Double sellingPrice,
            @RequestParam(value = "weight") String weight,
//            @RequestParam(value = "productImageIds") List<String> productImageIds,
//            @RequestParam(value = "images") List<MultipartFile> images,
            @RequestParam(value = "productImageId") String productImageId,
            @RequestParam(value = "productImageId1") String productImageId1,
            @RequestParam(value = "productImageId2") String productImageId2,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "avatar1", required = false) MultipartFile avatar1,
            @RequestParam(value = "avatar2", required = false) MultipartFile avatar2) {

        ProductDto productDto = new ProductDto(categoryId, name, total, description, importPrice, sellingPrice, weight);
        List<String> productImageIds = new ArrayList<>(Arrays.asList(productImageId, productImageId1, productImageId2));
        List<MultipartFile> avatars = new ArrayList<>();
        Collections.addAll(avatars, avatar, avatar1, avatar2);

        return ResponseEntity.ok(productService.updateProduct(productId, productDto, productImageIds, avatars)); // productImageId
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


/*
package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.ProductDto;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@ModelAttribute ProductDto productDto) {

        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts (
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        return ResponseEntity.ok(productService.getAllProducts(offset, limit));
    }

    @GetMapping("/list-products/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));
        Map<String, Object> response = new HashMap<>();

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            response.put("EC", 0);
            response.put("MS", "Get products by category successfully.");
            Category category = optionalCategory.get();
            response.put("products", productRepository.findByCategory(category, pageRequest).getContent());

            return ResponseEntity.ok(response);
        } else  {
            response.put("EC", 1);
            response.put("MS", "Error Getting products by category.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productId) {
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get product by ID successfully.");
        response.put("product", productRepository.findById(productId));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@ModelAttribute ProductDto productDto, @PathVariable String productId) {

//        ProductDto productDto = new ProductDto(categoryId, name, total, description, importPrice, sellingPrice, weight);
        return ResponseEntity.ok(productService.updateProduct(productId, productDto)); // productImageId
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

 */