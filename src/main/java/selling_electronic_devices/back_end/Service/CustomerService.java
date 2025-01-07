package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import selling_electronic_devices.back_end.Dto.SignupRequest;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Entity.Staff;
import selling_electronic_devices.back_end.Repository.CustomerRepository;
import selling_electronic_devices.back_end.Repository.StaffRepository;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

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
            if ("CUSTOMER".equals(updateDto.getRole())) {
                Optional<Customer> optionalCustomer = customerRepository.findById(id);
                if (optionalCustomer.isPresent()) {
                    Customer customer = optionalCustomer.get();
                    customer.setEmail(updateDto.getEmail());
                    if (!updateDto.getPassword().isEmpty()) {
                        customer.setPassword(passwordEncoder.encode(updateDto.getPassword()));
                    }
                    customer.setAddress(updateDto.getAddress());
                    customer.setPhone(updateDto.getPhone());
                    customer.setUsername(updateDto.getUsername());
                    customer.setFullName(updateDto.getFullName());
                    customer.setUpdatedAt(LocalDateTime.now());

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
                    staff.setUpdatedAt(LocalDateTime.now());

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

    public boolean changePassword(String id, String password) {
        Customer customer = customerRepository.findById(id).orElse(null);
        Staff staff = staffRepository.findById(id).orElse(null);
        if (customer != null) {
            customer.setPassword(passwordEncoder.encode(password));
            customerRepository.save(customer);

            return true;
        } else if (staff != null) {
            staff.setPassword(passwordEncoder.encode(password));
            staffRepository.save(staff);

            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean getNewPassword(String email) {
        Customer customer = customerRepository.findByEmail(email);
        Staff staff = staffRepository.findByEmail(email);

        if (customer != null) {
            String randomPassword = generateRandomPassword(6);
            customer.setPassword(passwordEncoder.encode(randomPassword));
            customerRepository.save(customer);

            // sent to user email
            sendEmail(customer.getEmail(), "Password reset", "Your new password is: " + randomPassword);

            return true;
        } else if (staff != null) {
            String randomPassword = generateRandomPassword(6);
            staff.setPassword(passwordEncoder.encode(randomPassword));
            staffRepository.save(staff);

            // sent to user email
            sendEmail(staff.getEmail(), "Password reset", "Your new password is " + randomPassword);

            return true;
        } else {
            return false;
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
   }
}
