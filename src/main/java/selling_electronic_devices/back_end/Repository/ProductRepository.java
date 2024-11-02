package selling_electronic_devices.back_end.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selling_electronic_devices.back_end.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    // get products theo danh mục (category)
//    List<Product> findByCategory(Category category);

    @Query("SELECT p FROM Product p JOIN Category c ON p.categoryId = c.categoryId WHERE p.name LIKE %:query% OR p.description LIKE %:query% OR c.name LIKE %:query% " +
            "ORDER BY CASE " +
            "WHEN p.name = :query THEN 0 " +
            "WHEN p.name LIKE :query% THEN 1 " +
            "WHEN p.name LIKE %:query THEN 2 " +
            "ELSE 3 END, " +
            "c.name DESC")
    Page<Product> findBySearchQuery(@Param("query") String query, Pageable pageable);

    Page<Product> findByCategoryId(String categoryId, Pageable pageable);
}
