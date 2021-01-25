package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends Service<User, Long>, UserDetailsService {
}
