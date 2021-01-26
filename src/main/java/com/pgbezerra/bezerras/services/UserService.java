package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends Service<User, Long>, UserDetailsService {

    default User authenticated() {
        try {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch(Exception e) {
            return null;
        }
    }
}
