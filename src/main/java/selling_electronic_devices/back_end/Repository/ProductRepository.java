package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // get products theo danh mục (category)
    List<Product> findByCategory(Category category);

    // get product theo id findById()

    // get full product findAll()

    //
}
