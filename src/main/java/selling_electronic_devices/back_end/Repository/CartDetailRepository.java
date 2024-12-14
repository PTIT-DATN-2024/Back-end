package selling_electronic_devices.back_end.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import selling_electronic_devices.back_end.Entity.Cart;
import selling_electronic_devices.back_end.Entity.CartDetail;
import selling_electronic_devices.back_end.Entity.Product;

import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, String> {
    Optional<CartDetail> findByProduct(Product product);

    Page<CartDetail> findByCart(Cart cart, Pageable pageable);

    List<CartDetail> findByCart(Cart cart);

    // xóa dùng hàm Query Derivation của Spring Data JPA Cách 1.1
    @Transactional // Đã liên quan đến delete, update -> PHẢI CÓ Transaction
    void deleteByCartAndProduct(Cart cart, Product product);

    // xóa dùng JPQL Cách 1.2
    @Modifying// trong JPA update, delete ko trả về kết quả dữ liệu -> phải dùng Modifying để DÁNH DẤU đây là thao tác update, delete, nếu ko đánh dâu spring jpa sẽ cố gắng xử lý như select (có kq trả vê)
    @Transactional //  update, delete LUÔN LUÔN yêu cầu thực hiện trong 1 TRANSACTION, nếu ko có -> error: Executing an update/delete
    @Query("DELETE FROM CartDetail cd WHERE cd.cart = :cart AND cd.product = :product")
    void deleteByCartAndProductCustom(@Param("cart") Cart cart, @Param("product") Product product);
}
