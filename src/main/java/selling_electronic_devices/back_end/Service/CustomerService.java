package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.CustomerDto;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("customerId")));

        return customerRepository.findAll(pageRequest).getContent();
    }

    public boolean updateCustomer(String customerId, CustomerDto customerDto) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setEmail(customerDto.getEmail());
            customer.setPassword(customerDto.getPassword());
            customer.setUserName(customerDto.getUserName());
            customer.setFullName(customerDto.getFullName());
            customer.setAvatar(customerDto.getAvatar());
            customer.setPhone(customerDto.getPhone());

            customerRepository.save(customer);

            return true;
        }
        return false;
    }

    public boolean deleteUser(String customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isPresent()) {
            optionalCustomer.get().setCustomerId("1");
//            customerRepository.delete(optionalCustomer.get());
            return true;
        }
        return false;
    }
}
