package selling_electronic_devices.back_end.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @Column(name = "banner_id")
    private String bannerId;

    @Column(name = "admin_id")
    private String adminId;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "description")
    private String description;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

