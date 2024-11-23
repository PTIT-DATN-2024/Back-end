package selling_electronic_devices.back_end.Dto;

import java.util.List;

public class OrderDto {
    private String customerId;
    private String staffId;
    private List<CartDetailDto> cartDetails;
    private Double total;

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

    public List<CartDetailDto> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<CartDetailDto> cartDetails) {
        this.cartDetails = cartDetails;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
