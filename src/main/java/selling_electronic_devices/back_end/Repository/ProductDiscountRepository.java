package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.ProductDiscount;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, String> {
}
