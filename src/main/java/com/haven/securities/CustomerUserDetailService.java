package com.haven.securities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.haven.entity.Manager;
import com.haven.entity.Customer;
import com.haven.repository.ManagerRepository;
import com.haven.repository.CustomerRepository;

@Service
public class CustomerUserDetailService implements UserDetailsService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Manager> manager = managerRepository.findByEmail(email);
        if (manager.isPresent()) {
            Manager mgr = manager.get();
            String roleName = mgr.getRole().getRoleName().name();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            return new CustomerUserDetail(mgr, authorities);  // Trả về với Manager
        }

        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
            return new CustomerUserDetail(customer.get(), authorities);  // Trả về với Customer
        }

        throw new UsernameNotFoundException("Tài khoản không hợp lệ với : " + email);
    }
}