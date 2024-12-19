package selling_electronic_devices.back_end.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", referencedColumnName = "staff_id")
    private Staff staff;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("order") // bỏ qua field "order" khi serialization DetailOrderedProduct
    private List<DetailOrderedProduct> detailOrderedProducts;

    @Column(name = "ship_address")
    private String shipAddress;

    @Column(name = "total", columnDefinition = "NUMERIC(10, 2)")
    private Double total;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Order() {
    }

    public Order(String orderId, Customer customer, Staff staff, String shipAddress, Double total, String paymentType, String status) {
        this.orderId = orderId;
        this.customer = customer;
        this.staff = staff;
        this.shipAddress = shipAddress;
        this.total = total;
        this.paymentType = paymentType;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public List<DetailOrderedProduct> getDetailOrderedProducts() {
        return detailOrderedProducts;
    }

    public void setDetailOrderedProducts(List<DetailOrderedProduct> detailOrderedProducts) {
        this.detailOrderedProducts = detailOrderedProducts;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
