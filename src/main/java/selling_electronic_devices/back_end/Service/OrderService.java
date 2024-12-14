package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import selling_electronic_devices.back_end.Dto.OrderDto;
import selling_electronic_devices.back_end.Entity.*;
import selling_electronic_devices.back_end.Repository.*;

import java.util.*;

@Service
public class OrderService {


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DetailOrderedProductRepository detailOrderedProductRepository;

    @Autowired
    private StaffRepository staffRepository;



    @Autowired
    private ProductRepository productRepository;

    public Order createOrder(OrderDto orderDto) {
        Optional<Customer> optionalCustomer = customerRepository.findById(orderDto.getCustomerId());
        Optional<Staff> optionalStaff = staffRepository.findById(orderDto.getStaffId());
        Order order;
        if (optionalCustomer.isPresent() && optionalStaff.isPresent()) {

            order = new Order();
            order.setOrderId(UUID.randomUUID().toString());
            order.setCustomer(optionalCustomer.get());
            order.setStaff(optionalStaff.get());
            order.setShipAddress("address");
            order.setTotal(orderDto.getTotal());
            order.setPaymentType("cash");
            order.setStatus("CXN");

            orderRepository.save(order);

            // lưu detailOrderedProduct từ các sản phầm customer chon để Order từ giỏ hàng-Cart (chi tiết dược lưu trong CartDetails)
            //List<CartDetailDto> items = orderDto.getCartDetails();
            List<CartDetail> items = orderDto.getCartDetails();
            for (CartDetail item : items) {
                DetailOrderedProduct detailOrderedProduct = new DetailOrderedProduct();
                detailOrderedProduct.setDetailOrderProductId(UUID.randomUUID().toString());
                detailOrderedProduct.setOrder(order);
                detailOrderedProduct.setProduct(item.getProduct());
                //detailOrderedProduct.setProduct(optionalProduct.orElseGet(null));
                detailOrderedProduct.setQuantity(item.getQuantity());
                detailOrderedProduct.setTotalPrice(item.getTotalPrice());

                detailOrderedProductRepository.save(detailOrderedProduct);
            }
        } else {
            throw new RuntimeException();
        }

        return order;
    }


    public Map<String, Object> getAllOrders(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("orderId")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get All Orders Successfully.");
        response.put("orders", orderRepository.findAll(pageRequest).getContent());
        return response;
    }

    public void updateOrder(String orderId, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        optionalOrder.ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }

    public Map<String, Object> deleteOrder(String orderId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        optionalOrder.ifPresent(order -> orderRepository.delete(order));
        response.put("EC", 0);
        response.put("MS", "Deleted order successfully.");
        return response;
    }

    @Transactional // Nếu ko để transaction -> vi phạm consistency: chẳng hạn lõi ở phần cuối cartDetail.delete() nhưng phần trước đó update quantity product vẫn được thực hiện và lưu vào cơ sở dữ liệu
    // Có transactional: nhờ có atomic "all or nothing": cartDetail.delete() lỗi -> rollback all
    public void updateStatus(String orderId, String status) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID is invalid: " + orderId);
        }

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        optionalOrder.ifPresentOrElse(
                order -> {
                    // Update status order
                    order.setStatus(status);
                    orderRepository.save(order);

                    // Remove cartDetail khỏi Cart = product + cartId (có được dựa vào orderId)
                    Customer customer = order.getCustomer();
                    Cart cart = cartRepository.findByCustomer(customer);

                    // update quantity product + delete cartDetail, remove khỏi cart
                    List<DetailOrderedProduct> detailOrderedProducts = order.getDetailOrderedProducts();
                    for (DetailOrderedProduct detailOrderedProduct : detailOrderedProducts) {
                        Product product = detailOrderedProduct.getProduct();

                        if (status.equals("CLH")) {
                            if (product.getTotal() - detailOrderedProduct.getQuantity() == 0) {
                                product.setStatus("Unavailable");
                            }
                            product.setTotal(product.getTotal() - detailOrderedProduct.getQuantity());
                            productRepository.save(product);
                        }

                        cartDetailRepository.deleteByCartAndProduct(cart, product);

                        // Cách 2: Dùng cart -> lọc cartDetals1, từ cartDetails1 + product -> cartDetail duy nhất. Ngược lại cũng xóa được.
                        /*// Nên lọc theo cart trước vì: cart tương đương customer, một thằng customer có nhiều cũng chỉ add tầm 10-20 (cartDetail) vào giỏ (cart) -> result = 10-20 cartDetails
                        // Nhưng nếu lọc theo findByProduct -> vì một product đấy có thể dượd rất nhiều thằng customer (vd: 500) add vào Cart -> result = 500 cartDetails, rất nhiều
                        List<CartDetail> cartDetails = cartDetailRepository.findByCart(cart);
                        if (cartDetails.isEmpty()) {
                            System.out.println("CartDetail---------------------- Empty.");
                        }
                        // Lọc theo product -> xđ duy nhât 1 cartDetailId
                        CartDetail cartDetail = cartDetails.stream()
                                .filter(cd -> cd.getProduct().equals(product))
                                .findFirst().orElseThrow(() -> new IllegalArgumentException("Not found cartDetail match with product."));

                        if (cartDetail == null) {
                            System.out.println("CartDetail--------------NULL.");
                        }
                        // xóa cartDetail khỏi cart
                        cartDetailRepository.delete(cartDetail);
                        */
                    }
                },
                () -> {
                    throw new NoSuchElementException("Order with ID: " + orderId + " not found.");
                }
        );

    }
}
