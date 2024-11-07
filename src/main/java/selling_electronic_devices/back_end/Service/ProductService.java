package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Product> getAllProducts(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.asc("productId")));
        return productRepository.findAll(pageRequest).getContent();
    }

    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
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

    public String rateProduct(String productId, ProductReviewDto productReviewDto) {
        ProductReview productReview = new ProductReview();
        productReview.setProductReviewId(UUID.randomUUID().toString());
        productReview.setProductId(productId);
        productReview.setCustomerId(productReviewDto.getCustomerId());
        productReview.setRating(productReviewDto.getRating());
        productReview.setComment(productReviewDto.getComment());

        productReviewRepository.save(productReview);

        return "Successful product reviews";
    }

    public List<Product> searchProduct(String query, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));
        Page<Product> products = productRepository.findBySearchQuery(query, pageRequest);

        return products.getContent();
    }


}
