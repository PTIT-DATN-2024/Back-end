package selling_electronic_devices.back_end.Repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.IceBox;

import java.util.List;
import java.util.Optional;

public interface IceBoxRepository extends JpaRepository<IceBox, String> {

    Optional<IceBox> findByChatBoxId(String chatBoxId);
    Optional<IceBox> findByCustomer(Customer customer);

    List<IceBox> findByStatus(String status, PageRequest pageRequest);
}
