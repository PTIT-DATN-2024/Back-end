package selling_electronic_devices.back_end.Entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    //@JsonIgnore
    //@JsonBackReference // chỉ định đây là phía con của relationship
    @JsonIgnoreProperties("products") // Bỏ qua field 'products' của Category (List<Product>) khi serialization Product
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_discount_id", referencedColumnName = "product_discount_id")
    private ProductDiscount productDiscount;

    @OneToMany(mappedBy = "product",fetch = FetchType.EAGER)
    @JsonIgnoreProperties("product")
    private List<ProductImage> productImages;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "total")
    private Long total;

    @Column(name = "rate", columnDefinition = "NUMERIC(10, 2)")
    private Double rate;

    @Column(name = "number_vote")
    private Long numberVote;

    @Column(name = "description")
    private String description;

    @Column(name = "import_price", columnDefinition = "NUMERIC(10, 2)")
    private Double importPrice;

    @Column(name = "selling_price", columnDefinition = "NUMERIC(10, 2)")
    private Double sellingPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "weight")
    private String weight;

    @Column(name = "present_image")
    private String presentImage;

    @Column(name = "is_delete")
    private String isDelete;

    @Column(name = "version")
    @Version // sử dụng cơ chế Optimistic Locking
    private long version;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

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

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPresentImage() {
        return presentImage;
    }

    public void setPresentImage(String presentImage) {
        this.presentImage = presentImage;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
