package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import selling_electronic_devices.back_end.Repository.CategoryRepository;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.OrderRepository;
import selling_electronic_devices.back_end.Repository.ProductRepository;

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

    @PostMapping
    public ResponseEntity<?> getStats(//@RequestParam (value = "startDay") LocalDateTime startDay,
                                      //@RequestParam(value = "endDay") LocalDateTime endDay,
                                      @RequestParam(value = "year") int year) {

        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", orderRepository.countTotalOrders(year));
        response.put("totalCompleteOrders", orderRepository.countTotalCompleteOrders(year));
        response.put("totalCancelOrders", orderRepository.countTotalCancelOrders(year));
        response.put("totalCustomers", customerRepository.countTotalCustomers(year));
        response.put("totalProducts", productRepository.countTotalProducts(year));
        response.put("totalCategories", categoryRepository.countTotalCategories(year));
        response.put("totalRevenue", orderRepository.calculateTotalRevenue(year));

        List<Integer> labels = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        List<Long> revenues = new ArrayList<>();
        Collections.addAll(revenues, 1L, null, 3L, 4L, null, 6L, 7L, 8L, 9L, 10L, 12L);
        response.put("labels", labels); // 12 thang của năm
        response.put("revenues", orderRepository.statsRevenueAndMonthOfYear(year)); // 12 giá trị revenue tương ứng
        response.put("topSpent", orderRepository.findTop10CustomersByTotalSpent(year)); // 12 thang của năm
//        response.put("topBestSellers", productRepository.listBestSellers(yearStats));

        return ResponseEntity.ok(response);
    }

}
