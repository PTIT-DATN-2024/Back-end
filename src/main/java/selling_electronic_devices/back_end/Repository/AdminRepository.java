package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Admin findByEmail(String email);
}
