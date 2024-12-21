package selling_electronic_devices.back_end.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import selling_electronic_devices.back_end.Entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByEmail(String email);

    @Query("SELECT COUNT(c) FROM Customer c WHERE EXTRACT(YEAR FROM c.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM c.createdAt) = :month) ") //  :month -> điều kiện tháng sẽ bị bỏ
    Long countTotalCustomers(@Param("year") int year, @Param("month") Integer month);
}
