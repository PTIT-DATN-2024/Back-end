package selling_electronic_devices.back_end.Dto;

public class RevenueDto {
    private int month;
    private Double revenue;

    public RevenueDto(int month, Double revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
