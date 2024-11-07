package selling_electronic_devices.back_end.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews")
public class ProductReview {

    @Id
    @Column(name = "product_review_id")
    private String productReviewId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "rating")
    private String rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    public String getProductReviewId() {
        return productReviewId;
    }

    public void setProductReviewId(String productReviewId) {
        this.productReviewId = productReviewId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}