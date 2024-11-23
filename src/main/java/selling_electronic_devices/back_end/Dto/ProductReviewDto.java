package selling_electronic_devices.back_end.Dto;

import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Product;

public class ProductReviewDto {
    private String customerId;
    private String productId;
    private String rating;
    private String comment;

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
}
