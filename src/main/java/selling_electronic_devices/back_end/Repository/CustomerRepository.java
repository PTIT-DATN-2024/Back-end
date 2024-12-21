package selling_electronic_devices.back_end.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selling_electronic_devices.back_end.Entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByEmail(String email);

    @Query("SELECT COUNT(c) FROM Customer c WHERE EXTRACT(YEAR FROM c.createdAt) = :year")
    Long countTotalCustomers(@Param("year") int year);
}
