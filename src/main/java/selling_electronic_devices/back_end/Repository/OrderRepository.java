package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCustomerId(String customerId);
}
