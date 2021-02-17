package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.models.entity.User;
import com.pgbezerra.bezerras.repository.UserRepository;
import com.pgbezerra.bezerras.services.RoleService;
import com.pgbezerra.bezerras.services.UserService;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(final UserRepository userRepository,  final RoleService roleService, final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User insert(User user) {
        user.setId(null);
        try {
            if (Objects.nonNull(loadUserByUsername(user.getUsername())))
                throw new ResourceBadRequestException(String.format("Username %s already exists", user.getUsername()));
        } catch (UsernameNotFoundException e){
            LOG.info(String.format("Username %s not exists, creating", user.getUsername()));
        }
        user.setRole(roleService.findById(user.getRole().getId()));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.insert(user);
    }

    @Override
    public Boolean update(User user) {
        User oldUser = findById(user.getId());
        updateData(oldUser, user);
        Boolean updated = userRepository.update(oldUser);
        LOG.info(String.format("User %s updated: %s", user, updated));
        return updated;
    }

    private void updateData(User oldUser, User user) {
        oldUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        oldUser.setName(user.getName());
        oldUser.setUsername(user.getUsername());
        oldUser.setRole(user.getRole());
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        LOG.info(String.format("%s users found", users.size()));
        if(!users.isEmpty())
            return users;
        throw new ResourceNotFoundException("No users found");
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        LOG.info(String.format("User with id %s found: %s", id, user.isPresent()));
        if(user.isPresent())
            return user.get();
        throw new ResourceNotFoundException(String.format("No users found with id: %s", id));
    }

    @Override
    public Boolean deleteById(Long id) {
        findById(id);
        Boolean deleted = userRepository.deleteById(id);
        LOG.info(String.format("User %s deleted: %s", id, deleted));
        return deleted;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent())
            return user.get();
        throw new UsernameNotFoundException(String.format("No user founded with username %s", user));
    }
}
