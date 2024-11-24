package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.CartDetail;
import selling_electronic_devices.back_end.Entity.Product;

import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, String> {
    Optional<CartDetail> findByProduct(Product product);
}
