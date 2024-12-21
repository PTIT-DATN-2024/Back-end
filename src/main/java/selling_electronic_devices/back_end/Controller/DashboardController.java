package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Repository.CategoryRepository;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.OrderRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;
import selling_electronic_devices.back_end.Service.OrderService;

import java.lang.reflect.Type;
import java.util.*;

@RestController
@RequestMapping("/stats")
public class DashboardController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> getStats(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year") int year) {

        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", orderRepository.countTotalOrders(year, month));
        response.put("totalCompleteOrders", orderRepository.countTotalCompleteOrders(year, month));
        response.put("totalCancelOrders", orderRepository.countTotalCancelOrders(year, month));
        response.put("totalCustomers", customerRepository.countTotalCustomers(year, month));
        response.put("totalProducts", productRepository.countTotalProducts(year, month));
        response.put("totalCategories", categoryRepository.countTotalCategories(year, month));
        response.put("totalRevenue", orderRepository.calculateTotalRevenue(year, month));

//        List<Integer> labels = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
//        List<Long> revenues = new ArrayList<>();
//        Collections.addAll(revenues, 1L, null, 3L, 4L, null, 6L, 7L, 8L, 9L, 10L, 12L);
//        response.put("labels", labels); // 12 thang của năm
        response.put("revenues", orderService.statsRevenueAndMonthOfYear(year, month)); // 12 giá trị revenue tương ứng
        response.put("topSpent", orderService.findTop10CustomersByTotalSpent(year, month)); // 12 thang của năm
//        response.put("topBestSellers", productRepository.listBestSellers(yearStats));

        response.put("EC", 0);
        response.put("MS", "Success");
        return ResponseEntity.ok(response);
    }

}
