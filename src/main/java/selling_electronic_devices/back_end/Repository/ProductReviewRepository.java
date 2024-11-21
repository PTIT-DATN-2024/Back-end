package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Product;
import selling_electronic_devices.back_end.Entity.ProductReview;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {
    List<ProductReview> findByProduct(Product product);

}
