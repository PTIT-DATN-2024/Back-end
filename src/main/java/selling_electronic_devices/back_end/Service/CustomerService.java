package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.SignupRequest;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Staff;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.StaffRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    public List<?> getAllUsers(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("isDelete")));

        List<Staff> staffList = staffRepository.findAll(pageRequest).getContent();
        List<Customer> customerList = customerRepository.findAll(pageRequest).getContent();

        List<Object> users = new ArrayList<>();
        users.addAll(staffList);
        users.addAll(customerList);

        return users;
    }

    public boolean updateInfoUser(String id, SignupRequest updateDto) {
        try {
            if (updateDto.getRole().equals("CUSTOMER")) {
                Optional<Customer> optionalCustomer = customerRepository.findById(id);
                if (optionalCustomer.isPresent()) {
                    Customer customer = optionalCustomer.get();
                    customer.setEmail(updateDto.getEmail());
                    customer.setPassword(updateDto.getPassword());
                    customer.setAddress(updateDto.getAddress());
                    customer.setPhone(updateDto.getPhone());
                    customer.setUsername(updateDto.getUsername());
                    customer.setFullName(updateDto.getFullName());
//                    customer.setRole(customerDto.getRole());

                    MultipartFile avatar = updateDto.getAvatar();
                    if (avatar != null && !avatar.isEmpty()) { // avatar = null -> keep old avatar
                        String avtPath = "D:/electronic_devices/uploads/users/customers/" + avatar.getOriginalFilename();
                        File avtFile = new File(avtPath);
                        avatar.transferTo(avtFile);

                        String urlAvtDb = "http://localhost:8080/uploads/users/customers/" + avatar.getOriginalFilename();
                        customer.setAvatar(urlAvtDb);
                    }

                    customerRepository.save(customer);
                    return true;
                }
                return false;
            } else {
                Optional<Staff> optionalStaff = staffRepository.findById(id);
                if (optionalStaff.isPresent()) {
                    Staff staff = optionalStaff.get();
                    staff.setEmail(updateDto.getEmail());
                    staff.setPassword(updateDto.getPassword());
                    staff.setAddress(updateDto.getAddress());
                    staff.setPhone(updateDto.getPhone());
                    staff.setUsername(updateDto.getUsername());
                    staff.setFullName(updateDto.getFullName());
//                    staff.setRole(customerDto.getRole());

                    MultipartFile avatar = updateDto.getAvatar();
                    if (avatar != null && !avatar.isEmpty()) { // avatar = null -> keep old avatar
                        String avtPath = "D:/electronic_devices/uploads/users/staffs/" + avatar.getOriginalFilename();
                        File avtFile = new File(avtPath);
                        avatar.transferTo(avtFile);

                        String urlAvtDb = "http://localhost:8080/uploads/users/staffs/" + avatar.getOriginalFilename();
                        staff.setAvatar(urlAvtDb);
                    }

                    staffRepository.save(staff);
                    return true;
                }
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteCustomer(String customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setIsDelete("True");
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    public boolean deleteStaff(String staffId) {
        Optional<Staff> optionalStaff = staffRepository.findById(staffId);
        return optionalStaff.map(staff -> {
            staff.setIsDelete("True");
            staffRepository.save(staff);
            return true;
        }).orElseGet(() -> false);
    }
}
