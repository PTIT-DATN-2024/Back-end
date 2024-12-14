package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Cart;
import selling_electronic_devices.back_end.Entity.Customer;

public interface CartRepository extends JpaRepository<Cart, String> {
    Cart findByCustomer(Customer customer);

}
