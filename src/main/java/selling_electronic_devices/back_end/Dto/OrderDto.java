package selling_electronic_devices.back_end.Dto;

public class OrderDto {
    private String customerId;
    private String staffId;
    private String shipAddress;
    private Double shipFee;
    private String paymentType;
    private String status;

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
}
