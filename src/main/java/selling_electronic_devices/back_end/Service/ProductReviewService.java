package selling_electronic_devices.back_end.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.ProductReviewDto;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Product;
import selling_electronic_devices.back_end.Entity.ProductReview;
import selling_electronic_devices.back_end.Repository.CartDetailRepository;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;

import java.util.*;

@Service
public class ProductReviewService {

    private static final Logger log = LoggerFactory.getLogger(ProductReviewService.class);
    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    public void createProductReview(ProductReviewDto productReviewDto) {
        ProductReview productReview = new ProductReview();
        productReview.setProductReviewId(UUID.randomUUID().toString());
        Optional<Customer> optionalCustomer = customerRepository.findById(productReviewDto.getCustomerId());
        Optional<Product> optionalProduct = productRepository.findById(productReviewDto.getProductId());
        optionalCustomer.ifPresentOrElse(
                productReview::setCustomer,
                () -> {
                    throw new IllegalArgumentException("Not found customer with ID: " + productReviewDto.getCustomerId());
                });
        optionalProduct.ifPresentOrElse( // update numberVote, rate
                product -> {
                    productReview.setProduct(product);

                    // update numberVote, rate
                    long numberVoteOld = product.getNumberVote();
                    if (numberVoteOld == 0) {
                        product.setRate(Double.parseDouble(productReviewDto.getRating()));
                        product.setNumberVote(1L);
                    } else {
                        double rateOld = product.getRate();
                        double rateNew = ( rateOld * numberVoteOld + Integer.parseInt(productReviewDto.getRating()) ) / (numberVoteOld + 1);

                        product.setRate(rateNew);
                        product.setNumberVote(numberVoteOld + 1);
                    }

                    productRepository.save(product);
                }
                ,
                () -> {
                    throw new IllegalArgumentException("Not found product with ID: " + productReviewDto.getProductId());
                });
        //productReview.setCustomer(optionalCustomer.orElseGet(() -> null)); // Đúng: dung orElseGet với 1 Supplier hợp lệ
        //productReview.setCustomer(optionalCustomer.orElseGet(null)); // SAI: orElseGet yêu cầu 1 đối tượng Supplier (function interface - hàm ko có tham số và nó trả về 1 giá trị, nó chứa 1 method duy nhât là T get()) làm tham số
        //productReview.setProduct(optionalProduct.orElseGet(() -> null)); // Với orElseGet(nulll): java cố gọi get() trên null -> Error NullPointerException, notify "Invoke supplier.get() is null"
        productReview.setRating(productReviewDto.getRating()); // productReview.setRating(String.valueOf(productReviewDto.getRating()));
        productReview.setComment(productReviewDto.getComment());

        productReviewRepository.save(productReview);
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
//            Optional<Customer> optionalCustomer = customerRepository.findById(productReviewDto.getCustomerId());
//            Optional<Product> optionalProduct = productRepository.findById(productReviewDto.getProductId());
//            optionalCustomer.ifPresentOrElse(
//                    productReview::setCustomer,
//                    () -> {
//                        throw new IllegalArgumentException("Not found customer with ID: " + productReviewDto.getCustomerId());
//                    }
//            );
//            optionalProduct.ifPresentOrElse(
//                    productReview::setProduct,
//                    () -> {
//                        throw new IllegalArgumentException("Not found product ID: " + productReviewDto.getProductId());
//                    }
//            );
//            productReview.setCustomer(optionalCustomer.orElseGet(() -> null));
//            productReview.setProduct(optionalProduct.orElseGet(() -> null));

            // update lại rate cho product
            Optional<Product> optionalProduct = productRepository.findById(productReview.getProduct().getProductId());
            optionalProduct.ifPresentOrElse(
                    product -> {
                        long numberVote = product.getNumberVote();
                        double rateOld = product.getRate();

                        double rateNew = (rateOld * numberVote - Integer.parseInt(productReview.getRating()) + Double.parseDouble(productReviewDto.getRating())) / numberVote;
                        product.setRate(rateNew);

                        productRepository.save(product);
                    },
                    () -> {
                        throw new NoSuchElementException("Not found product to update rate.");
                    }
            );
            productReview.setComment(productReviewDto.getComment());
            productReview.setRating(productReviewDto.getRating());

            productReviewRepository.save(productReview);
            return true;
        }
        return false;
    }

    public boolean deleteProductReview(String productReviewId) {
        Optional<ProductReview> optionalProductReview = productReviewRepository.findById(productReviewId);
        if (optionalProductReview.isPresent()) {
            ProductReview productReview = optionalProductReview.get();
            Optional<Product> optionalProduct = productRepository.findById(productReview.getProduct().getProductId());
            optionalProduct.ifPresentOrElse(
                    product -> {
                        long numberVoteOld = product.getNumberVote();
                        double rateOld = product.getRate();

                        if (numberVoteOld > 1) {
                            double rateNew = ( rateOld * numberVoteOld - Double.parseDouble(productReview.getRating()) ) / (numberVoteOld - 1);
                            product.setRate(rateNew);
                            product.setNumberVote(numberVoteOld - 1);
                        } else {
                            product.setRate(0d);
                            product.setNumberVote(0L);
                        }

                        productRepository.save(product);
                    },
                    () -> {
                        throw new IllegalArgumentException("Not found product to update rate.");
                    }
            );

            productReviewRepository.delete(productReview);
            return true;
        } else {
            return false;
        }
    }

}
