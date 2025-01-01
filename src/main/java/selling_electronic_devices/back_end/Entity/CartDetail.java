package selling_electronic_devices.back_end.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "cart_details",
        indexes = @Index(name = "idx_cart_product_id", columnList = "cart_id, product_id") // composite index: tối ưu việc tìm kiếm cartDetail (wherer product_id = ? && cart_id = ??) dựa vào cart_id + product_id, sẽ dùng index scan thay vì seq scan (quét toàn bảng).
)
public class CartDetail {

    @Id
    @Column(name = "cart_detail_id")
    private String cartDetailId;

    @ManyToOne(fetch = FetchType.EAGER)//, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id")
    @JsonIgnoreProperties("cartDetails")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)//, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "total_price", columnDefinition = "NUMERIC(10, 2)")
    private Double totalPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getCartDetailId() {
        return cartDetailId;
    }
    public void setCartDetailId(String cartDetailId) {
        this.cartDetailId = cartDetailId;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
