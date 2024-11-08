package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Entity.ProductReview;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;

import java.util.*;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    public String createProductReview(ProductReviewDto productReviewDto) {
        ProductReview productReview = new ProductReview();
        productReview.setProductReviewId(UUID.randomUUID().toString());
        productReview.setCustomerId(productReviewDto.getCustomerId());
        productReview.setProductId(productReviewDto.getProductId());
        productReview.setRating(productReviewDto.getRating());
        productReview.setComment(productReviewDto.getComment());

        productReviewRepository.save(productReview);
        return "Created rating successfully.";
    }

    public Map<String, Object> getAllProductReviews(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("productReviewId")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get all product_reviews successfully.");
        response.put("orders", productReviewRepository.findAll(pageRequest).getContent());
        return response;
    }

    public Map<String, Object> getProductReviewsByProduct(String productId) {
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get product_reviews by productId successfully");
        response.put("productReviews", productReviewRepository.findByProductId(productId));
        return response;
    }

    public boolean updateProductReview(String productReviewId, ProductReviewDto productReviewDto) {
        Optional<ProductReview> optionalProductReview = productReviewRepository.findById(productReviewId);
        if (optionalProductReview.isPresent()) {
            ProductReview productReview = new ProductReview();
            productReview.setCustomerId(productReviewDto.getCustomerId());
            productReview.setProductId(productReviewDto.getProductId());
            productReview.setComment(productReviewDto.getComment());
            productReview.setRating(productReviewDto.getRating());

            productReviewRepository.save(productReview);
            return true;
        }
        return false;
    }

    public boolean deleteProductReview(String productReviewId) {
        Optional<ProductReview> optionalProductReview = productReviewRepository.findById(productReviewId);
        //optionalRating.ifPresent(rating -> ratingRepository.delete(rating));
        if (optionalProductReview.isPresent()) {
            productReviewRepository.delete(optionalProductReview.get());
            return true;
        }
        return false;
    }


}
