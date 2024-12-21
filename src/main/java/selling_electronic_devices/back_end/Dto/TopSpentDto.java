package selling_electronic_devices.back_end.Dto;

import selling_electronic_devices.back_end.Entity.Customer;

public class TopSpentDto {
    private Customer customer;
    private Double total;

    public TopSpentDto(Customer customer, Double total) {
        this.customer = customer;
        this.total = total;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
