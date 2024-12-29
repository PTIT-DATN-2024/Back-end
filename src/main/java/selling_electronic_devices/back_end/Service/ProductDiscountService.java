package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.ProductDiscountDto;
import selling_electronic_devices.back_end.Entity.ProductDiscount;
import selling_electronic_devices.back_end.Repository.ProductDiscountRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductDiscountService {

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    public void createProductDiscount(ProductDiscountDto productDiscountDto) {

        ProductDiscount productDiscount = new ProductDiscount(UUID.randomUUID().toString(), productDiscountDto.getName(), productDiscountDto.getDiscountAmount(), productDiscountDto.getExpiredDate());
        productDiscountRepository.save(productDiscount);
    }

    public void updateProductDiscount(String productDiscountId, ProductDiscountDto productDiscountDto) {
        ProductDiscount productDiscount = productDiscountRepository.findById(productDiscountId).orElseThrow(
                () -> new IllegalArgumentException("Not found discount with ID.")
        );

        productDiscount.setName(productDiscountDto.getName());
        productDiscount.setDiscountAmount(productDiscountDto.getDiscountAmount());
        productDiscount.setExpiredDate(productDiscountDto.getExpiredDate());
        productDiscount.setUpdatedAt(LocalDateTime.now());

        productDiscountRepository.save(productDiscount);
    }

    public void deleteProductDiscount(String productDiscountId) {
        ProductDiscount productDiscount = productDiscountRepository.findById(productDiscountId).orElseThrow(
                () -> new IllegalArgumentException("Not found discount with ID.")
        );

        productDiscountRepository.delete(productDiscount);
    }

    public List<ProductDiscount> getAllProductDiscounts(PageRequest pageRequest) {
        return productDiscountRepository.findAll(pageRequest).getContent();
    }
}
