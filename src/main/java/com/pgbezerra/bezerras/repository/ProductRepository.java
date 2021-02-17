package com.pgbezerra.bezerras.repository;

import java.util.List;

import com.pgbezerra.bezerras.models.entity.Category;
import com.pgbezerra.bezerras.models.entity.Product;

public interface ProductRepository extends Repository<Product, Integer> {
	List<Product> findByCategory(Category category);
}
