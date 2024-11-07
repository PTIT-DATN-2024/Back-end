package selling_electronic_devices.back_end.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
