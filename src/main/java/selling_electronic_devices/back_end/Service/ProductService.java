package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.ProductDto;
import selling_electronic_devices.back_end.Dto.VoteDto;
import selling_electronic_devices.back_end.Entity.Product;
import selling_electronic_devices.back_end.Entity.Rating;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Repository.RatingRepository;

import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setCategoryId(productDto.getCategoryId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setSeller(productDto.getSeller());
        product.setDescription(productDto.getDescription());

        productRepository.save(product);
    }

    public List<Product> getAllProducts(PageRequest pageRequest) {
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
            product.setDescription(productDto.getDescription());
            product.setName(productDto.getName());
            product.setPrice(product.getPrice());
            product.setStock(product.getStock());
            product.setSeller(product.getSeller());

            productRepository.save(product);
        }
    }

    public String rateProduct(String productId, VoteDto voteDto) {
        Rating rating = new Rating();
        rating.setProductId(productId);
        rating.setUserId(voteDto.getUserId());
        rating.setProductId(voteDto.getProductId());
        rating.setRating(voteDto.getRating());
        rating.setComment(voteDto.getComment());

        ratingRepository.save(rating);

        return "Successful product reviews";
    }

    public List<Product> searchProduct(String query, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));
        Page<Product> products = productRepository.findBySearchQuery(query, pageRequest);

        return products.getContent();
    }


}
