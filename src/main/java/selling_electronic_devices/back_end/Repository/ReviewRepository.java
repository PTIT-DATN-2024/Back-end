package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // lấy ds reviews của productId
    List<Review> findByProductId(Long productId);
}
