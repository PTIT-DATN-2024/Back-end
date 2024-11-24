package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Staff;

public interface StaffRepository extends JpaRepository<Staff, String> {
    Staff findByEmail(String email);
}
