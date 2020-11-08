package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.entities.model.Table;

public interface TableService {
	
	Table insert(Table obj);
	Boolean update(Table obj);
	List<Table> findAll();
	Table findById(Integer id);
	Boolean deleteById(Integer id);
	
}
