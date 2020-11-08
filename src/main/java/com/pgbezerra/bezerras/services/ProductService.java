package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.entities.model.Product;

public interface ProductService {
	
	Product insert(Product obj);
	Boolean update(Product obj);
	List<Product> findAll();
	Product findById(Integer id);
	Boolean deleteById(Integer id);

}
