package selling_electronic_devices.back_end.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.CategoryDto;
import selling_electronic_devices.back_end.Entity.Category;
import selling_electronic_devices.back_end.Service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryDto categoryDto, @RequestParam MultipartFile avatar) {
        Map<String, Object> response = new HashMap<>();
        try {
            categoryService.createCategory(categoryDto, avatar);
            response.put("EC", 0);
            response.put("MS", "Created Successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "Error While Creating!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
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

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable String categoryId, @RequestBody CategoryDto categoryDto, @RequestParam MultipartFile avatar) {
        Map<String, Object> response = new HashMap<>();
        try {
            categoryService.updateCategory(categoryId, categoryDto, avatar);
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
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("EC", 0);
            response.put("MS", "Category deleted successfully.");
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("EC", 1);
            response.put("MS", "Error while deleting!");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
