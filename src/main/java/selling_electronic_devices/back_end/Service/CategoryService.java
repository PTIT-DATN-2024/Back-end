package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Repository.CategoryRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public void createCategory(String name, String description, MultipartFile avatar) {
        Category category = new Category();
        category.setCategoryId(UUID.randomUUID().toString());
        category.setName(name);
        category.setDescription(description);

        String avatarPath = "D:/electronic_devices/uploads/categories/" + avatar.getOriginalFilename();
        File avatarFile = new File(avatarPath);

        try {
            avatar.transferTo(avatarFile);

            String urlAvatarDb = "http://localhost:8080/uploads/categories/" + avatar.getOriginalFilename();
            category.setAvatar(urlAvatarDb);

            categoryRepository.save(category);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Category> getAllCategories(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("categoryId")));
        return categoryRepository.findAll(pageRequest).getContent();
    }

    public void updateCategory(String categoryId, String name, String description, MultipartFile avatar) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(name);
            category.setDescription(description);

            // kiểm tra avatar có thay đổi?
            if (avatar != null && avatar.isEmpty()) { // toán tử ngắn mạch '&&' đảm bảo nếu avatar == null -> ko check avatar.isEmpty() <nếu check -> NullPointerException>
                String avatarPath = "D:/electronic_devices/uploads/categories/" + avatar.getOriginalFilename();
                File avatarFile = new File(avatarPath);

                try {
                    avatar.transferTo(avatarFile);
                    String urlAvtDb = "http://localhost:8080/uploads/categories/" + avatar.getOriginalFilename();
                    category.setAvatar(urlAvtDb);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            categoryRepository.save(category);
        }
    }

    public void deleteCategory(String categoryId) {
        categoryRepository.findById(categoryId);
    }
}
