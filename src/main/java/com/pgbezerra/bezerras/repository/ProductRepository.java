package com.pgbezerra.bezerras.repository;

import java.util.List;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;

public interface ProductRepository extends Repository<Product, Integer> {
	List<Product> findByCategory(Category category);
}
