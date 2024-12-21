package selling_electronic_devices.back_end.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selling_electronic_devices.back_end.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);

    // FUNCTION('YEAR', field) -> EXTRACT(YEAR FROM field)
    @Query("SELECT COUNT(c) FROM Category c WHERE EXTRACT(YEAR FROM c.createdAt) = :year " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM c.createdAt) = :month)")
    Long countTotalCategories(@Param("year") int year);
}
