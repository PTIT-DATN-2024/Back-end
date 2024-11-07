package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
    // Lấy danh mục các loại sản phẩm findAll();
}
