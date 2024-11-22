package selling_electronic_devices.back_end.Dto;

import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.DetailOrderedProduct;
import selling_electronic_devices.back_end.Entity.Staff;

import java.util.List;

public class OrderDto {
    private String customerId;
    private String staffId;
    private List<DetailOrderedProduct> detailOrderedProducts;
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
}
