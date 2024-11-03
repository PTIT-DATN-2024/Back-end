package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, String> {
    // lấy ds reviews của productId
    List<Rating> findByProductId(String productId);
}
