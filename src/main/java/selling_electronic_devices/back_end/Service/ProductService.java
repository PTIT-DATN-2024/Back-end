package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.ProductDto;
import selling_electronic_devices.back_end.Entity.*;
import selling_electronic_devices.back_end.Repository.CategoryRepository;
import selling_electronic_devices.back_end.Repository.ProductImageRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Repository.ProductReviewRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Map<String, Object> createProduct(ProductDto productDto, List<MultipartFile> avatars) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (productRepository.findByName(productDto.getName())) {
                return Map.of("EC", 1, "MS", "Name already exists.");
            }
            
            Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());
            if (optionalCategory.isPresent()) {
//                Product product = new Product(UUID.randomUUID().toString(), optionalCategory.get(),productDto.getProductDiscount(),
//                        productDto.getName(), productDto.getTotal(), 0L, productDto.getDescription(), productDto.getImportPrice(),
//                        productDto.getSellingPrice(), "available", productDto.getWeight(), "null", "False");

                Product product = new Product();
                product.setProductId(UUID.randomUUID().toString());
                //product.setProductId("prod007");
                product.setCategory(optionalCategory.get());
                product.setProductDiscount(productDto.getProductDiscount());
                product.setName(productDto.getName());
                product.setTotal(productDto.getTotal());
                product.setRate(5.0);
                product.setNumberVote(19L);
                product.setDescription(productDto.getDescription());
                product.setImportPrice(productDto.getImportPrice());
                product.setSellingPrice(productDto.getSellingPrice());
                product.setWeight(productDto.getWeight());
                product.setStatus("available");
                product.setPresentImage("null");
                product.setIsDelete("False");

                // Lưu sản phẩm
                productRepository.save(product);

                // Lưu file ảnh
                for (MultipartFile avt : avatars) {
                    // Lưu ảnh vào ProductImage
                    ProductImage productImage = new ProductImage();
                    productImage.setProductImageId(UUID.randomUUID().toString());
                    productImage.setProduct(product);

                    String avtPath = "D:/electronic_devices/uploads/products/" + avt.getOriginalFilename();
                    File avtFile = new File(avtPath);

                    avt.transferTo(avtFile);
                    String urlAvtDb = "http://localhost:8080/uploads/products/" + avt.getOriginalFilename();
                    productImage.setImage(urlAvtDb);

                    productImageRepository.save(productImage);
                }
                response.put("EC", 0);
                response.put("MS", "Created product successfully.");
            } else {
                response.put("EC", 1);
                response.put("MS", "Category not found.");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("EC", 1);
            response.put("MS", "Data integrity violation.");
        } catch (IOException e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("EC", 3);
            response.put("MS", "An unexpected error occurred: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> getAllProducts(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.asc("productId")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get All Products Successfully.");
        response.put("products", productRepository.findAll(pageRequest).getContent());
        return response;
    }

    public Map<String, Object> deleteProduct(String productId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Product> optionalProduct = productRepository.findById(productId);
        response.put("EC", 1);
        response.put("MS", "Not found.");

        optionalProduct.ifPresent(product -> {
            product.setIsDelete("True");
            productRepository.save(product);
            response.put("EC", 0);
            response.put("MS", "Deleted product successfully.");
        });

        return response;
    }

    public Map<String, Object> updateProduct(String productId, ProductDto productDto, List<String> productImageIds, List<MultipartFile> avatars) {
        Map<String, Object> response = new HashMap<>();
        Optional<Product> productOp = productRepository.findById(productId);
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());

        try {
            if (productOp.isPresent() && optionalCategory.isPresent()) {
                Product product = productOp.get();
                product.setCategory(optionalCategory.get());

                if (productDto.getProductDiscount() != null) { // thay đổi gia nếu thêm discount
                    Double oldPrice = product.getSellingPrice();
                    Double newPrice = oldPrice * (1 - productDto.getProductDiscount().getDiscountAmount());
                    product.setSellingPrice(newPrice);
                }
                product.setProductDiscount(product.getProductDiscount());
                product.setName(productDto.getName());
                product.setTotal(productDto.getTotal());
                product.setDescription(productDto.getDescription());
                product.setImportPrice(productDto.getImportPrice());
                product.setSellingPrice(productDto.getSellingPrice());
                product.setWeight(productDto.getWeight());

//                product.setRate(4.5);
//                product.setNumberVote(19L);
//                product.setPresentImage("null");
                product.setStatus(product.getStatus());
                product.setUpdatedAt(LocalDateTime.now());

                productRepository.save(product);

                List<ProductImage> productImages = product.getProductImages();

                int index = 0;
                for(MultipartFile avt : avatars) {
                    if (avt != null && !avt.isEmpty()) {
                        String avtPath = "D:/electronic_devices/uploads/products/" + avt.getOriginalFilename();
                        File avtFile = new File(avtPath);

                        avt.transferTo(avtFile);
                        String urlAvtDb = "http://localhost:8080/uploads/products/" + avt.getOriginalFilename();

                        // lưu vào Product Image
                        Optional<ProductImage> optionalProductImage = productImageRepository.findById(productImageIds.get(index));
                        optionalProductImage.ifPresent(productImage -> {
                            ProductImage changeImage = optionalProductImage.get();
                            changeImage.setImage(urlAvtDb);

                            productImageRepository.save(changeImage);
                        });
                    }
                    index++;
                }

                response.put("EC", 0);
                response.put("MS", "Updated product successfully.");
            } else {
                response.put("EC", 1);
                response.put("MS", "Category not found.");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("EC", 1);
            response.put("MS", "Product name already exist.");
        } catch (IOException e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("EC", 3);
            response.put("MS", "An unexpected error occurred: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> searchProduct(String query, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")));

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Products with query string.");
        response.put("products", productRepository.findBySearchQuery(query, pageRequest).getContent());

        return response;
    }


}