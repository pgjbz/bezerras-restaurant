package com.pgbezerra.bezerras.services;

import java.util.List;

public interface Service <T, PK> {

    T insert(T obj);
    Boolean update(T obj);
    List<T> findAll();
    T findById(PK id);
    Boolean deleteById(PK id);
}
