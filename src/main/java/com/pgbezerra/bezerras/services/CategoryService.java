package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.entities.model.Category;

public interface CategoryService {
	
	Category insert(Category obj);
	Boolean update(Category obj);
	List<Category> findAll();
	Category findById(Integer id);
	Boolean deleteById(Integer id);
	
}
