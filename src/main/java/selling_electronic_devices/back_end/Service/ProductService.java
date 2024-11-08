package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.ProductDto;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Entity.Product;
import selling_electronic_devices.back_end.Entity.ProductReview;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setCategoryId(productDto.getCategoryId());
        product.setProductDiscountId(productDto.getProductDiscountId());
        product.setName(productDto.getName());
        product.setTotal(productDto.getTotal());
        product.setRate(productDto.getRate());
        product.setNumberVote(productDto.getNumberVote());
        product.setDescription(productDto.getDescription());
        product.setImportPrice(productDto.getImportPrice());
        product.setSellingPrice(productDto.getSellingPrice());
        product.setStatus(product.getStatus());

        productRepository.save(product);
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

    public void updateProduct(String productId, ProductDto productDto) {
        Optional<Product> productOp = productRepository.findById(productId);
        if (productOp.isPresent()) {
            Product product = productOp.get();
            product.setCategoryId(productDto.getCategoryId());
            product.setProductDiscountId(productDto.getProductDiscountId());
            product.setName(productDto.getName());
            product.setTotal(productDto.getTotal());
            product.setRate(productDto.getRate());
            product.setNumberVote(productDto.getNumberVote());
            product.setDescription(productDto.getDescription());
            product.setImportPrice(productDto.getImportPrice());
            product.setSellingPrice(productDto.getSellingPrice());
            product.setStatus(product.getStatus());

            productRepository.save(product);
        }
    }

    public Map<String, Object> rateProduct(String productId, ProductReviewDto productReviewDto) {
        ProductReview productReview = new ProductReview();
        productReview.setProductReviewId(UUID.randomUUID().toString());
        productReview.setProductId(productId);
        productReview.setCustomerId(productReviewDto.getCustomerId());
        productReview.setRating(productReviewDto.getRating());
        productReview.setComment(productReviewDto.getComment());

        productReviewRepository.save(productReview);

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Rated successfully.");

        return response;
    }

    public Map<String, Object> searchProduct(String query, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Products with query string.");
        response.put("products", productRepository.findBySearchQuery(query, pageRequest).getContent());

        return response;
    }


}
