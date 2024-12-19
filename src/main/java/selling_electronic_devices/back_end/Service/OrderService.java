package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
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

//    @Transactional
//    public Order createOrder(OrderDto orderDto) {
//        Optional<Customer> optionalCustomer = customerRepository.findById(orderDto.getCustomerId());
//        Optional<Staff> optionalStaff = staffRepository.findById(orderDto.getStaffId());
//
//        if (optionalCustomer.isEmpty() || optionalStaff.isEmpty()) {
//            throw new RuntimeException("Invalid customer or staff ID.");
//        }
//
//        Order order = new Order();
//        order.setOrderId(UUID.randomUUID().toString());
//        order.setCustomer(optionalCustomer.get());
//        order.setStaff(optionalStaff.get());
//        order.setShipAddress("address");
//        order.setTotal(orderDto.getTotal());
//        order.setPaymentType("cash");
//        order.setStatus("CXN");
//
//        orderRepository.save(order);
//
//        // lưu detailOrderedProduct từ các sản phầm customer chon để Order từ giỏ hàng-Cart (chi tiết dược lưu trong CartDetails)
//        //List<CartDetailDto> items = orderDto.getCartDetails();
//        List<CartDetail> items = orderDto.getCartDetails();
//        for (CartDetail item : items) {
//            System.out.println("Quantity Product : quantity item -> " + item.getProduct().getTotal() + " : " + item.getQuantity());
////            //1. Cập nhật quantity
////            Product product = item.getProduct();
////
////            // check quantity hiện tại
////            if (product.getTotal() < item.getQuantity()) {
////                throw new IllegalArgumentException("Not enough stock for product: " + product.getProductId());
////            }
////
////            // cập nhật
////            product.setTotal(product.getTotal() - item.getQuantity());
////            try {
////                productRepository.save(product);
////            } catch (OptimisticLockingFailureException e) {
//            // check lại (read) lại quantity mới nhất
////                Product lastestProduct = productRepository.findById(product.getProductId()).orElseThrow();
////                if (lastestProduct.getTotal() >= item.getQuantity()) {
////                    lastestProduct.setTotal(lastestProduct.getTotal() - item.getQuantity());
////                    productRepository.save(lastestProduct);
////                } else {
////                    throw e; // không đủ hàng, ném ngoại ệ
////                }
////            }
//            retryUpdateProduct(item.getProduct(), item.getQuantity());
//
//            // 2. Lưu các detail ordered product
//            DetailOrderedProduct detailOrderedProduct = new DetailOrderedProduct();
//            detailOrderedProduct.setDetailOrderProductId(UUID.randomUUID().toString());
//            detailOrderedProduct.setOrder(order);
//            detailOrderedProduct.setProduct(item.getProduct());
//            detailOrderedProduct.setQuantity(item.getQuantity());
//            detailOrderedProduct.setTotalPrice(item.getTotalPrice());
//
//            detailOrderedProductRepository.save(detailOrderedProduct);
//        }
//
//        return order;
//    }
//
//    private void retryUpdateProduct(Product product, long quantity) {
//        int attempts = 0;
//        boolean updated = false;
//
//        while (!updated && attempts < 3) {
//            try {
//                if (product.getTotal() < quantity) {
//                    throw new IllegalArgumentException("Not enough stock for product: " + product.getProductId());
//                }
//                product.setTotal(product.getTotal() - quantity);
//                productRepository.save(product);
//                updated = true;
//                System.out.println("----------");
//            } catch (OptimisticLockingFailureException e) {
//                // Reload product for retry, version, quantity đã được update vì có bởi transaction trước đó đã write xong
//                Product lastestProduct = productRepository.findById(product.getProductId()).orElseThrow();
//                System.out.println("Total : Version after previous_transaction wrote: " + lastestProduct.getTotal() + " : " + lastestProduct.getVersion());
//                if (lastestProduct.getTotal() >= quantity) {
//                    lastestProduct.setTotal(lastestProduct.getTotal() - quantity);
//                    productRepository.save(lastestProduct);
//
//                    attempts = 3;
//                    updated = true;
//                } else {
//                    continue;
//                }
//            }
//        }
//    }

    // Updated OrderService
    @Transactional//(rollbackFor = {Exception.class}) // đảm bảo hủy bỏ mọi thao tác nếu có thao tác lôi <Atomic>
    public Order createOrder(OrderDto orderDto) {
        Customer customer = customerRepository.findById(orderDto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID: " + orderDto.getCustomerId()));
        Staff staff = staffRepository.findById(orderDto.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid staff ID: " + orderDto.getStaffId()));

//        if (optionalCustomer.isEmpty() || optionalStaff.isEmpty()) {
//            throw new RuntimeException("Invalid customer or staff ID.");
//        }

        Order order = new Order(UUID.randomUUID().toString(), customer, staff, "Address", orderDto.getTotal(), "Cash", "CXN");
//        order.setOrderId(UUID.randomUUID().toString());
//        order.setCustomer(optionalCustomer.get());
//        order.setStaff(optionalStaff.get());
//        order.setShipAddress("address");
//        order.setTotal(orderDto.getTotal());
//        order.setPaymentType("cash");
//        order.setStatus("CXN");

        orderRepository.save(order);

        List<CartDetail> items = orderDto.getCartDetails();
        for (CartDetail item : items) {
            if (item.getQuantity() > item.getProduct().getTotal()) {
                throw new IllegalArgumentException("Not enough stock for product: " + item.getProduct().getProductId());
            }

            updateTotalProduct(item.getProduct().getProductId(), item.getQuantity());

            DetailOrderedProduct detailOrderedProduct = new DetailOrderedProduct(UUID.randomUUID().toString(), item.getProduct(), order, item.getQuantity(), item.getTotalPrice());
//            detailOrderedProduct.setDetailOrderProductId(UUID.randomUUID().toString());
//            detailOrderedProduct.setOrder(order);
//            detailOrderedProduct.setProduct(item.getProduct());
//            detailOrderedProduct.setQuantity(item.getQuantity());
//            detailOrderedProduct.setTotalPrice(item.getTotalPrice());
            detailOrderedProductRepository.save(detailOrderedProduct);
        }

        return order;
    }

//    @Retryable( // auto retry func catch khi excp value xảy ra
//            value = OptimisticLockingFailureException.class,
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 1000)
//    )
    public void updateTotalProduct(String productId, Long quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (product.getTotal() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + productId);
        }

        product.setTotal(product.getTotal() - quantity);
        productRepository.save(product);
        System.out.println("quantity update = " + quantity);
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

//                        if (status.equals("CLH")) {
//                            if (product.getTotal() - detailOrderedProduct.getQuantity() == 0) {
//                                product.setStatus("Unavailable");
//                            }
//                            product.setTotal(product.getTotal() - detailOrderedProduct.getQuantity());
//                            productRepository.save(product);
//                        }

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
