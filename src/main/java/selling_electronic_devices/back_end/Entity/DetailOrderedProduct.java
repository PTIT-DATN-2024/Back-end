package selling_electronic_devices.back_end.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;

@Entity
@Table(name = "detail_ordered_products")
public class DetailOrderedProduct {

    @Id
    @Column(name = "detail_order_product_id")
    private String detailOrderProductId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id") //, insertable = false, updatable = false) -> hibernate sẽ bỏ qua trường product_id khi insert hoặc update -> Error: "null value in column 'product_id' "
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @JsonIgnoreProperties("detailOrderedProducts") // bỏ qua field "detailOrderedProducts" khi serialization Order
    private Order order;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "total_price", columnDefinition = "NUMERIC(10, 2)")
    private Double totalPrice;

    public String getDetailOrderProductId() {
        return detailOrderProductId;
    }

    public void setDetailOrderProductId(String detailOrderProductId) {
        this.detailOrderProductId = detailOrderProductId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
