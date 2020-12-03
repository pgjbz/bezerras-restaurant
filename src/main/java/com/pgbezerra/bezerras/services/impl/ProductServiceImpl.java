package com.pgbezerra.bezerras.services.impl;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.ProductService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;

@Service
public class ProductServiceImpl implements ProductService {
	
	private static final Logger LOG = Logger.getLogger(ProductServiceImpl.class);

	private ProductRepository productRepository;
	private CategoryService categoryService;

	public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService) {
		this.productRepository = productRepository;
		this.categoryService = categoryService;
	}

	@Override
	public Product insert(Product obj) {
		obj.setId(null);
		categoryService.findById(obj.getCategory().getId());
		return productRepository.insert(obj);
	}

	@Override
	public Boolean update(Product obj) {
		categoryService.findById(obj.getCategory().getId());
		Product oldObj = findById(obj.getId());
		updateData(oldObj, obj);
		Boolean updated = productRepository.update(oldObj);
		LOG.info(String.format("Product %s updated: %s", obj, updated));
		return updated;
	}

	private void updateData(Product oldObj, Product obj) {
		oldObj.setCategory(obj.getCategory());
		oldObj.setName(obj.getName());
		oldObj.setValue(obj.getValue());
	}

	@Override
	public List<Product> findAll() {
		List<Product> products = productRepository.findAll();
		LOG.info(String.format("%s products found", products.size()));
		if(!products.isEmpty()) 
			return products;
		throw new ResourceNotFoundException("No categories found");
	}

	@Override
	public Product findById(Integer id) {
		Optional<Product> product = productRepository.findById(id);
		LOG.info(String.format("Product with id %s found: %s", id, product.isPresent()));	
		if(product.isPresent()) 
			return product.get();
		throw new ResourceNotFoundException(String.format("No products found with id: %s", id));
	}

	@Override
	public Boolean deleteById(Integer id) {
		findById(id);
		Boolean deleted = productRepository.deleteById(id);
		LOG.info(String.format("Product %s deleted: %s", id, deleted));
		return deleted;
	}

}
