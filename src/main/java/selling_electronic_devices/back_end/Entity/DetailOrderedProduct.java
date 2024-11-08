package selling_electronic_devices.back_end.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "detail_ordered_products")
public class DetailOrderedProduct {

    @Id
    @Column(name = "detail_order_product_id")
    private String detailOrderProductId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "total_price", columnDefinition = "NUMERIC(10, 2)")
    private Double totalPrice;

    public String getDetailOrderProductId() {
        return detailOrderProductId;
    }

    public void setDetailOrderProductId(String detailOrderProductId) {
        this.detailOrderProductId = detailOrderProductId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
