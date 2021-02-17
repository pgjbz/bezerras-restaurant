package com.pgbezerra.bezerras.repository;

import java.util.Optional;

import com.pgbezerra.bezerras.models.entity.User;

public interface UserRepository extends Repository<User, Long> {

    Optional<User> findByUsername(String username);
}
