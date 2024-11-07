package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Entity.ProductReview;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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


    public List<ProductReview> getProductReviewsByProduct(String productId) {
        return productReviewRepository.findByProductId(productId);
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
