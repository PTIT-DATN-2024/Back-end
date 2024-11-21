package selling_electronic_devices.back_end.Dto;

import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Product;

public class ProductReviewDto {
    private Customer customer;
    private Product product;
    private String rating;
    private String comment;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
