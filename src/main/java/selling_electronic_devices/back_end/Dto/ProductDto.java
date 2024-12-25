package selling_electronic_devices.back_end.Dto;

import jakarta.persistence.Column;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Entity.ProductDiscount;

import java.util.List;

public class ProductDto {
    private String categoryId;
    private String name;
    private Long total;
    private String description;
    private Double importPrice;
    private Double sellingPrice;
    private String weight;
    private MultipartFile avatar = null;
    private MultipartFile avatar1 = null;
    private MultipartFile avatar2 = null;

    public ProductDto(String categoryId, String name, Long total, String description, Double importPrice, Double sellingPrice, String weight, MultipartFile avatar, MultipartFile avatar1, MultipartFile avatar2) {
        this.categoryId = categoryId;
        this.name = name;
        this.total = total;
        this.description = description;
        this.importPrice = importPrice;
        this.sellingPrice = sellingPrice;
        this.weight = weight;
        this.avatar = avatar;
        this.avatar1 = avatar1;
        this.avatar2 = avatar2;
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

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }

    public MultipartFile getAvatar1() {
        return avatar1;
    }

    public void setAvatar1(MultipartFile avatar1) {
        this.avatar1 = avatar1;
    }

    public MultipartFile getAvatar2() {
        return avatar2;
    }

    public void setAvatar2(MultipartFile avatar2) {
        this.avatar2 = avatar2;
    }
}