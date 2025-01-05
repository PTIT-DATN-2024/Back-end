package selling_electronic_devices.back_end.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ice_box") // chat_box
public class IceBox {

//    @Id
//    @Column(name = "ice_box_id")
//    private String iceBoxId;

    @Id
    @Column(name = "chat_box_id")
    private String chatBoxId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", referencedColumnName = "staff_id")
    @JsonIgnoreProperties("iceBoxes")
    private Staff staff;

    @Column(name = "status")
    private String status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public IceBox() {
    }

//    public String getIceBoxId() {
//        return iceBoxId;
//    }
//
//    public void setIceBoxId(String iceBoxId) {
//        this.iceBoxId = iceBoxId;
//    }

    public String getChatBoxId() {
        return chatBoxId;
    }

    public void setChatBoxId(String chatBoxId) {
        this.chatBoxId = chatBoxId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}