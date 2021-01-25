package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.entities.model.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    Optional<User> findByUsername(String username);
}
