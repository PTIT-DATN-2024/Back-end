package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Product;
import selling_electronic_devices.back_end.Entity.ProductReview;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;

import java.util.*;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public String createProductReview(ProductReviewDto productReviewDto) {
        ProductReview productReview = new ProductReview();
        productReview.setProductReviewId(UUID.randomUUID().toString());
        Optional<Customer> optionalCustomer = customerRepository.findById(productReviewDto.getCustomerId());
        Optional<Product> optionalProduct = productRepository.findById(productReviewDto.getProductId());
        //optionalCustomer.ifPresent(productReview::setCustomer);
        productReview.setCustomer(optionalCustomer.orElseGet(null));
        productReview.setProduct(optionalProduct.orElseGet(null));
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
        response.put("productReview", productReviewRepository.findAll(pageRequest).getContent());
        return response;
    }

    public Map<String, Object> getAllReviewOfProduct(String productId, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("productReviewId")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get all review of Product.");
        response.put("reviews", productReviewRepository.findByProduct(productRepository.findById(productId).orElseGet(null)));

        return response;
    }

    public Map<String, Object> getProductReviewsByProduct(String productId) {
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get product_reviews by productId successfully");
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            response.put("productReviews", productReviewRepository.findByProduct(optionalProduct.get()));
        } else {
            //response.put("productReviews", Collections.emptyList());
            response.put("productReviews", new ArrayList<>());
        }
        return response;
    }

    public boolean updateProductReview(String productReviewId, ProductReviewDto productReviewDto) {
        Optional<ProductReview> optionalProductReview = productReviewRepository.findById(productReviewId);
        if (optionalProductReview.isPresent()) {
            ProductReview productReview = optionalProductReview.get();
            Optional<Customer> optionalCustomer = customerRepository.findById(productReviewDto.getCustomerId());
            Optional<Product> optionalProduct = productRepository.findById(productReviewDto.getProductId());
            productReview.setCustomer(optionalCustomer.orElseGet(null));
            productReview.setProduct(optionalProduct.orElseGet(null));
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
