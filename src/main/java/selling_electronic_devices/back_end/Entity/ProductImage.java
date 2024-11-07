package selling_electronic_devices.back_end.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @Column(name = "product_image_id")
    private String productImageId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "image")
    private String image;

    public String getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(String productImageId) {
        this.productImageId = productImageId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}