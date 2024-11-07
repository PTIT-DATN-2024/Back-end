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

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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