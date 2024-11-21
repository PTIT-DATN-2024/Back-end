package selling_electronic_devices.back_end.Dto;

import jakarta.persistence.Column;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Entity.ProductDiscount;

public class ProductDto {
    private Category category;
    private ProductDiscount productDiscount;
    private String name;
    private Long total;
    private Double rate;
    private Long numberVote;
    private String description;
    private Double importPrice;
    private Double sellingPrice;
    private String status;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ProductDiscount getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(ProductDiscount productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Long getNumberVote() {
        return numberVote;
    }

    public void setNumberVote(Long numberVote) {
        this.numberVote = numberVote;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(Double importPrice) {
        this.importPrice = importPrice;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
