package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Lấy danh mục các loại sản phẩm findAll();
}
