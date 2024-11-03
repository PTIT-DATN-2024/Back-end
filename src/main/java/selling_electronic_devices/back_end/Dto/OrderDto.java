package selling_electronic_devices.back_end.Dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class OrderDto {

    private String userId;
    private double totalAmount;
    private String status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
