package selling_electronic_devices.back_end.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCustomer(Customer customer);


//    @Query("SELECT COUNT(o) FROM Order WHERE o.createdAt BETWEEN :startOfYear AND :endOfYear")
    // Postgre support FUNCTION('YEAR', o.filed) -> dùng EXTRACT(YEAR FROM field)
    @Query("SELECT COUNT(o) FROM Order o WHERE EXTRACT(YEAR FROM o.createdAt) = :year") // trích xuất year -> compare param 'year'
    long countTotalOrders(@Param("year") int year);

    @Query("SELECT COUNT(o) FROM Order o WHERE EXTRACT(YEAR FROM o.createdAt) = :year AND o.status = 'DG'")
    long countTotalCompleteOrders(@Param("year") int year);

    @Query("SELECT COUNT(o) FROM Order o WHERE EXTRACT(YEAR FROM o.createdAt) = :year AND o.status = 'DH'")
    Long countTotalCancelOrders(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE EXTRACT(YEAR FROM o.createdAt) = :year AND o.status = 'DG'")
    Double calculateTotalRevenue(@Param("year") int year);

    // trả về list customer kèm totalSpent
    @Query("SELECT o.customer, SUM(o.total) " +
            "FROM Order o " +
            "WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "GROUP BY o.customer " +
            "ORDER BY SUM(o.total) DESC ")
    List<Object[]> findTop10CustomersByTotalSpent(@Param("year") int year); // mỗi phần tử của Object[] gồm {customer và totalSpent)

    // trả về list customers
//    @Query("SELECT o.customer " +
//            "FROM Order o " +
//            "GROUP BY o.customer " +
//            "ORDER BY SUM(o.total) DESC")
//    List<Customer> findTop10Customers();

    @Query("SELECT EXTRACT(MONTH FROM o.createdAt), SUM(o.total) " + // do JPQL unsupported alias nên ko để as month được
            "FROM Order o " +
            "WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "GROUP BY EXTRACT(MONTH FROM o.createdAt) " +
            "ORDER BY EXTRACT(MONTH FROM o.createdAt) ASC") // theo thứ tự tháng 1, 2, 3 ... 12
    List<Object[]> statsRevenueAndMonthOfYear(@Param("year") int year);
}
