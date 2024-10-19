package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Order;
import selling_electronic_devices.back_end.Entity.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // dannh sách item theo orderId
    List<OrderItem> findByOrderId(Long orderId);
}
