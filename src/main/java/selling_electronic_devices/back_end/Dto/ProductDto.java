package selling_electronic_devices.back_end.Dto;

import jakarta.persistence.Column;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Entity.ProductDiscount;

public class ProductDto {
    private String categoryId;
    //    private ProductDiscount productDiscount;
    private String name;
    private Long total;
    //    private Double rate;
//    private Long numberVote;
    private String description;
    private Double importPrice;
    private Double sellingPrice;
    private String weight;
//    private String avatar;
//    private String status;


    public ProductDto(String categoryId, String name, Long total, String description, Double importPrice, Double sellingPrice, String weight) {
        this.categoryId = categoryId;
        this.name = name;
        this.total = total;
        this.description = description;
        this.importPrice = importPrice;
        this.sellingPrice = sellingPrice;
        this.weight = weight;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}