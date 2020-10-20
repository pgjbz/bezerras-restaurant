package com.pgbezerra.bezerras.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, PK> {
	
	T insert(T obj);
	Boolean update(T obj);
	Boolean deleteById(PK id);
	List<T> findAll();
	Optional<T> findById(PK id);
	List<T> insertAll(List<T> list);

}
