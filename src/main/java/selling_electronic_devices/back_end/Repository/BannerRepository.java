package selling_electronic_devices.back_end.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.Banner;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    // Láy tất cả (ảnh) banner
}
