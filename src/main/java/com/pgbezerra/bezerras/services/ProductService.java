package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;

import java.util.List;

public interface ProductService extends Service<Product, Integer> {
	
	List<Product> findByCategory(Category obj);

}
