package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Service.CategoryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)// chỉ định API này nhận dữ liệu dạng multipart/form-data, dùng @RequestParam có thể bỏ qua nó
    public ResponseEntity<?> createCategory(
            @RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart("avatar") MultipartFile avatar) {

        return ResponseEntity.ok(categoryService.createCategory(name, description, avatar));
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10000") int limit) {

        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get All Categories Successfully.");
        response.put("categories", categoryService.getAllCategories(offset, limit));
        return ResponseEntity.ok(response);
//        return ResponseEntity.ok(categoryService.getAllCategories(offset, limit));
    }

    @PutMapping(value = "/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(
            @PathVariable String categoryId,
            @RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        Map<String, Object> response = new HashMap<>();
        try {
            categoryService.updateCategory(categoryId, name, description, avatar);
            response.put("EC", 0);
            response.put("MS", "Updated Successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "Updated Error!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable String categoryId) {

        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

}
