package selling_electronic_devices.back_end.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "ship_address")
    private String shipAddress;

    @Column(name = "ship_fee")
    private Double shipFee;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "status")
    private String status;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public Double getShipFee() {
        return shipFee;
    }

    public void setShipFee(Double shipFee) {
        this.shipFee = shipFee;
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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
