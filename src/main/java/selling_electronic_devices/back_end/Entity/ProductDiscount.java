package selling_electronic_devices.back_end.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_discounts")
public class ProductDiscount {

    @Id
    @Column(name = "product_discount_id")
    private String productDiscountId;

    @Column(name = "name")
    private String name;

    @Column(name = "discount_amount", columnDefinition = "NUMERIC(10, 2)")
    private Double discountAmount;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProductDiscount() { // hibernate càn constructor rỗng để tọa object (instance ProductDiscount) rỗng trước khi gán đối giá trị từ csdl cho nó
    }

    public ProductDiscount(String productDiscountId, String name, Double discountAmount, LocalDateTime expiredDate) {
        this.productDiscountId = productDiscountId;
        this.name = name;
        this.discountAmount = discountAmount;
        this.expiredDate = expiredDate;
    }

    public String getProductDiscountId() {
        return productDiscountId;
    }

    public void setProductDiscountId(String productDiscountId) {
        this.productDiscountId = productDiscountId;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
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