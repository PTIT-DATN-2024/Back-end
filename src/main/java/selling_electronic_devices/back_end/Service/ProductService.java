package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.ProductDto;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Entity.*;
import selling_electronic_devices.back_end.Repository.CategoryRepository;
import selling_electronic_devices.back_end.Repository.ProductImageRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void createProduct(ProductDto productDto, MultipartFile avatar) {//, MultipartFile avatar1, MultipartFile avatar2) {
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());
        if (optionalCategory.isPresent()) {
            Product product = new Product();
            product.setProductId(UUID.randomUUID().toString());
            product.setCategory(optionalCategory.get());
            product.setProductDiscount(null);
            product.setName(productDto.getName());
            product.setTotal(productDto.getTotal());
            product.setRate(4.5);
            product.setNumberVote(19L);
            product.setWeight(productDto.getWeight());
            product.setPresentImage(productDto.getPresentImage());
            product.setDescription(productDto.getDescription());
            product.setImportPrice(productDto.getImportPrice());
            product.setSellingPrice(productDto.getSellingPrice());
            product.setStatus("available");

            // Lưu ảnh vào ProductImage
            ProductImage productImage = new ProductImage();
            productImage.setProductImageId(UUID.randomUUID().toString());
            productImage.setProduct(product);

            // lưu ảnh
            String avtPath = "D:/electronic_devices/uploads/products/" + avatar.getOriginalFilename();
            File avtFile = new File(avtPath);

            try {
                avatar.transferTo(avtFile);
                String urlAvtDb = "http://localhost:8080/uploads/products/" + avatar.getOriginalFilename();
                productImage.setImage(urlAvtDb);

                productImageRepository.save(productImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            productRepository.save(product);
        }
    }

    public Map<String, Object> getAllProducts(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.asc("productId")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get All Products Successfully.");
        response.put("products", productRepository.findAll(pageRequest).getContent());
        return response;
    }

    public Map<String, Object> deleteProduct(String productId) {
        Map<String, Object> response = new HashMap<>();
        productRepository.deleteById(productId);
        response.put("EC", 0);
        response.put("MS", "Deleted product successfully.");
        return response;
    }

    public void updateProduct(String productId, ProductDto productDto, MultipartFile avatar) {
        Optional<Product> productOp = productRepository.findById(productId);
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());
        if (productOp.isPresent() && optionalCategory.isPresent()) {
            Product product = productOp.get();
            product.setCategory(optionalCategory.get());
//            product.setProductDiscount(productDto.getProductDiscount());
            product.setName(productDto.getName());
            product.setTotal(productDto.getTotal());
            product.setRate(4.5);
            product.setNumberVote(19L);
            product.setWeight(productDto.getWeight());
            product.setPresentImage(productDto.getPresentImage());
            product.setDescription(productDto.getDescription());
            product.setImportPrice(productDto.getImportPrice());
            product.setSellingPrice(productDto.getSellingPrice());
            product.setStatus(product.getStatus());

            if (avatar != null && avatar.isEmpty()) {
                String avtPath = "D:/electronic_devices/uploads/products/" + avatar.getOriginalFilename();
                File avtFile = new File(avtPath);

                try {
                    avatar.transferTo(avtFile);
                    String urlAvtDb = "http://localhost:8080/uploads/products/" + avatar.getOriginalFilename();

                    // lưu vào Product Image
                    Optional<ProductImage> optionalProductImage = productImageRepository.findById("productImageId");
                    optionalProductImage.ifPresent(productImage -> {
                        ProductImage changeImage = optionalProductImage.get();
                        changeImage.setImage(urlAvtDb);

                        productImageRepository.save(changeImage);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            productRepository.save(product);
        }
    }

//    public Map<String, Object> rateProduct(String productId, ProductReviewDto productReviewDto) {
//        ProductReview productReview = new ProductReview();
//        Optional<Customer> optionalCustomer = customerRepository.findById(productReviewDto.getCustomerId());
//        Optional<Product> optionalProduct = productRepository.findById(productReviewDto.getProductId());
//        productReview.setCustomer(optionalCustomer.orElseGet(null));
//        productReview.setProduct(optionalProduct.orElseGet(null));
//        productReview.setComment(productReviewDto.getComment());
//        productReview.setRating(productReviewDto.getRating());
//
//        productReviewRepository.save(productReview);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("EC", 0);
//        response.put("MS", "Rated successfully.");
//
//        return response;
//    }

    public Map<String, Object> searchProduct(String query, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Products with query string.");
        response.put("products", productRepository.findBySearchQuery(query, pageRequest).getContent());

        return response;
    }


}
