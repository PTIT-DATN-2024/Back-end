package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Repository.CategoryRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Map<String, Object> createCategory(String name, String description, MultipartFile avatar) {
        Map<String, Object> response = new HashMap<>();

        try {
            // kiểm tra name, ko dùng cho update (vì nếu giữ nguyên name, thay đổi description... => exist(chính nó) => trả ra Category name already exists => ko cho update => sai
            if (categoryRepository.existsByName(name)){ //có thể dùng exception DataIntegrityViolationException được, nhưng yêu cầu đây đúng là ngoại lệ mà Database ném ra., dùng if(...) thì ôm được nhiều case hơn (nhưng ko biết lỗi cụ thể).
                response.put("EC", 2);
                response.put("MS", "Category name already exists.");
                return response;
            }

            Category category = new Category();
//            category.setCategoryId(UUID.randomUUID().toString());
            category.setCategoryId("cate007");
            category.setName(name);
            category.setDescription(description);

            String avatarPath = "D:/electronic_devices/uploads/categories/" + avatar.getOriginalFilename();
            File avatarFile = new File(avatarPath);

            avatar.transferTo(avatarFile);

            String urlAvatarDb = "http://localhost:8080/uploads/categories/" + avatar.getOriginalFilename();
            category.setAvatar(urlAvatarDb);

            response.put("EC", 0);
            response.put("MS", "Created Successfully.");

            categoryRepository.save(category);

        } catch (DataIntegrityViolationException e) {
            response.put("EC", 1);
            response.put("MS", "Category name already exist.");
        } catch (IOException e) {
            e.printStackTrace();
            response.put("EC", 1);
            response.put("MS", "Error saving avatar!");
        } catch (Exception e) {//(DataIntegrityViolationException e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", "Error saving data to database!");
        }

        return response;
    }

    public List<Category> getAllCategories(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("categoryId")));
        return categoryRepository.findAll(pageRequest).getContent();
    }

    public Map<String, Object> updateCategory(String categoryId, String name, String description, MultipartFile avatar) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();
                category.setName(name);
                category.setDescription(description);

                // kiểm tra avatar có thay đổi?
                if (avatar != null && !avatar.isEmpty()) { // toán tử ngắn mạch '&&' đảm bảo nếu avatar == null -> ko check avatar.isEmpty() <nếu check -> NullPointerException>
                    String avatarPath = "D:/electronic_devices/uploads/categories/" + avatar.getOriginalFilename();
                    File avatarFile = new File(avatarPath);

                    avatar.transferTo(avatarFile);
                    String urlAvtDb = "http://localhost:8080/uploads/categories/" + avatar.getOriginalFilename();
                    category.setAvatar(urlAvtDb);
                }

                categoryRepository.save(category);
                response.put("EC", 0);
                response.put("MS", "Updated Successfully.");
            } else {
                response.put("EC", 1);
                response.put("MS", "Updated Error!");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("EC", 1);
            response.put("MS", "Category name already exists.");
        } catch (IOException e) {
            e.printStackTrace();
            response.put("EC", 2);
            response.put("MS", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("EC", 3);
            response.put("MS", "Error updating!");
        }

        return response;
    }

    public Map<String, Object> deleteCategory(String categoryId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        optionalCategory.ifPresentOrElse(
                category -> {
                    if (category.getProducts().isEmpty()) {

                        // xóa ảnh
                        String avatarUrl = category.getAvatar();
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            String avatarFilePath = avatarUrl.replace("http://localhost:8080/", "D:/electronic_devices/");
                            File avatarFile = new File(avatarFilePath);

                            if (avatarFile.exists()) {
                                if (avatarFile.delete()) {
                                    System.out.println("Deleted file.");
                                } else {
                                    System.out.println("Failed to delete file.");
                                }
                            } else {
                                System.out.println("Not found file: " + avatarFilePath);
                            }
                        }

                        categoryRepository.delete(category);

                        response.put("EC", 0);
                        response.put("MS", "Category deleted.");
                    } else {
                        response.put("EC", 1);
                        response.put("MS", "Category cannot be deleted because it has products.");
                    }
                },
                () -> {
                    response.put("EC", 2);
                    response.put("MS", "Category not found.");
                });

        return response;
    }
}
