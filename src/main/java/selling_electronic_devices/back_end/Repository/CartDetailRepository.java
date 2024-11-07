package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.CartDetail;

public interface CartDetailRepository extends JpaRepository<CartDetail, String> {
}
