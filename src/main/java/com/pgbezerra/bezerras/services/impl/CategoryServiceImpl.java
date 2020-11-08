package com.pgbezerra.bezerras.services.impl;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	private static final Logger LOG = Logger.getLogger(CategoryServiceImpl.class);

	@Autowired
	private CategoryRepository categoryRepository;
	
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public Category insert(Category obj) {
		obj.setId(null);
		return categoryRepository.insert(obj);
	}

	@Override
	public Boolean update(Category obj) {
		Category oldObj = findById(obj.getId());
		updateDate(oldObj, obj);
		Boolean updated = categoryRepository.update(oldObj);
		LOG.info(String.format("Category %s updated: %s", obj, updated));
		return updated;
	}

	private void updateDate(Category oldObj, Category obj) {
		oldObj.setName(obj.getName());
	}

	@Override
	public List<Category> findAll() {
		List<Category> categories = categoryRepository.findAll();
		LOG.info(String.format("%s categories found", categories.size()));
		if(!categories.isEmpty()) 
			return categories;
		throw new ResourceNotFoundException("No categories found");
	}

	@Override
	public Category findById(Integer id) {
		Optional<Category> category = categoryRepository.findById(id);
		LOG.info(String.format("Category with id %s found: ", category.isPresent()));
		if(category.isPresent())
			return category.get();
		throw new ResourceNotFoundException(String.format("No categories found with id: %s", id));
	}

	@Override
	public Boolean deleteById(Integer id) {
		findById(id);
		Boolean deleted = categoryRepository.deleteById(id);
		LOG.info(String.format("Category %s deleted: %s", id, deleted));
		return deleted;
	}

}
