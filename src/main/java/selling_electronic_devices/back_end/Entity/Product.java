package selling_electronic_devices.back_end.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "product_discount_id")
    private String productDiscountId;

    @Column(name = "name")
    private String name;

    @Column(name = "total")
    private Long total;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "number_vote")
    private Long numberVote;

    @Column(name = "description")
    private String description;

    @Column(name = "import_price")
    private Double importPrice;

    @Column(name = "selling_price")
    private Double sellingPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // Getters and Setters

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductDiscountId() {
        return productDiscountId;
    }

    public void setProductDiscountId(String productDiscountId) {
        this.productDiscountId = productDiscountId;
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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
