package selling_electronic_devices.back_end.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selling_electronic_devices.back_end.Dto.RevenueDto;
import selling_electronic_devices.back_end.Dto.TopSpentDto;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCustomer(Customer customer);


//    @Query("SELECT COUNT(o) FROM Order WHERE o.createdAt BETWEEN :startOfYear AND :endOfYear")
    // Postgre support FUNCTION('YEAR', o.filed) -> dùng EXTRACT(YEAR FROM field)
    @Query("""
            SELECT COUNT(o) 
            FROM Order o 
            WHERE EXTRACT(YEAR FROM o.createdAt) = :year  
            AND (:month IS NULL OR EXTRACT(MONTH FROM o.createdAt) = :month)  
            """) // trích xuất year -> compare param 'year', nếu tham số month null ->  điều kiện tháng sẽ bỏ qua
    long countTotalOrders(@Param("year") int year, @Param("month") Integer month);

    @Query("SELECT COUNT(o) FROM Order o WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM o.createdAt) = :month) " +
            "AND o.status = 'DG'")
    long countTotalCompleteOrders(@Param("year") int year, @Param("month") Integer month);

    @Query("SELECT COUNT(o) FROM Order o WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM o.createdAt) = :month) " +
            "AND o.status = 'DH'")
    Long countTotalCancelOrders(@Param("year") int year, @Param("month") Integer month);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM o.createdAt) = :month) " +
            "AND o.status = 'DG'")
    Double calculateTotalRevenue(@Param("year") int year, @Param("month") Integer month);

    // trả về list customer kèm totalSpent
    @Query("SELECT o.customer, SUM(o.total) " +
            "FROM Order o " +
            "WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM o.createdAt) = :month) " +
            "GROUP BY o.customer " +
            "ORDER BY SUM(o.total) DESC ")
    List<Object[]> findTop10CustomersByTotalSpent(@Param("year") int year, @Param("month") Integer month); // mỗi phần tử của Object[] gồm {customer và totalSpent)

    @Query("SELECT EXTRACT(MONTH FROM o.createdAt), SUM(o.total) " + // do JPQL unsupported alias nên ko để as month được
            "FROM Order o " +
            "WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM o.createdAt) = :month) " +
            "GROUP BY EXTRACT(MONTH FROM o.createdAt) " +
            "ORDER BY EXTRACT(MONTH FROM o.createdAt) ASC") // theo thứ tự tháng 1, 2, 3 ... 12
    List<Object[]> statsRevenueAndMonthOfYear(@Param("year") int year, @Param("month") Integer month);

    /*@Query("SELECT new selling_electronic_devices.back_end.Dto.TopSpentDto(o.customer, SUM(o.total)) " +
            "FROM Order o " +
            "WHERE EXTRACT(YEAR FROM o.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM o.createdAt) = :month) " +
            "GROUP BY o.customer " +
            "ORDER BY SUM(o.total) DESC ")
    List<TopSpentDto> findTop10CustomersByTotalSpent(@Param("year") int year, @Param("month") Integer month);*/
}
