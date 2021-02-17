package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.models.entity.Category;
import com.pgbezerra.bezerras.models.entity.Product;

public interface ProductService extends Service<Product, Integer> {
	
	List<Product> findByCategory(Category obj);

}
