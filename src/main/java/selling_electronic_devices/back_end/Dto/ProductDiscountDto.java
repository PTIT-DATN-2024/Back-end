package selling_electronic_devices.back_end.Dto;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public class ProductDiscountDto {

    private String name;
    private Double discountAmount;
    private LocalDateTime expiredDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }
}
