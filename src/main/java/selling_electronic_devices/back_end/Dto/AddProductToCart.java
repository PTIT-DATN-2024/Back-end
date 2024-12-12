package selling_electronic_devices.back_end.Dto;

import selling_electronic_devices.back_end.Entity.Cart;
import selling_electronic_devices.back_end.Entity.Product;

public class AddProductToCart {
    private String customerId;
    private Product product;
    private Long quantity;
    private Double totalPrice;

    public AddProductToCart(String customerId, Product product, Long quantity, Double totalPrice) {
        this.customerId = customerId;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
}
