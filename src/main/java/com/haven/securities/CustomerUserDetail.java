package com.haven.securities;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.haven.entity.Customer;
import com.haven.entity.Manager;

public class CustomerUserDetail implements UserDetails {

    private Object user; // Có thể là Manager hoặc Customer
    private Collection<? extends GrantedAuthority> authorities;

    public CustomerUserDetail(Object user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        if (user instanceof Manager) {
            return ((Manager) user).getPassword();
        } else if (user instanceof Customer) {
            return ((Customer) user).getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        if (user instanceof Manager) {
            return ((Manager) user).getEmail();
        } else if (user instanceof Customer) {
            return ((Customer) user).getEmail();
        }
        return null;
    }
    
    public boolean isEmailVerified() {
        if (user instanceof Customer) {
            return ((Customer) user).isEmailVerified(); 
        }
        return true;
    }

    public Object getUser() {
        return user;
    }
    
    public Integer getUserId() {
        if (user instanceof Manager) {
            return ((Manager) user).getManagerId();
        } else if (user instanceof Customer) {
            return ((Customer) user).getCustomerId();
        }
        return null;
    }

    
    public Manager getManager() {
        if (user instanceof Manager) {
            return (Manager) user;
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
