package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Entity.Customer;
import selling_electronic_devices.back_end.Repository.CustomerRepository;

import java.util.ArrayList;

//Tạo bean (CustomUserDetailsService) triển khai interface UserDetailsService ĐỂ CÓ THỂ tạo bean UserDetailsService. Thì các class dependency injection (dj) nó mới tạo bean được, vd:JwtRequestFilter.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //Tìm tài xế theo email trong cơ sở dữ liệu
        Customer customer = customerRepository.findByEmail(email);
        if(customer == null) {
            throw new UsernameNotFoundException("Customer not found with email: " + email);
        }
        //Trả về đối tượng UserDetails với thông tin customer
        return new org.springframework.security.core.userdetails.User(customer.getEmail(), customer.getPassword(), new ArrayList<>());
    }
}
