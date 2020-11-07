package com.pgbezerra.bezerras.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.ResourceNotFoundException;

public class CategoryServiceImpl implements CategoryService {

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
		return categoryRepository.update(oldObj);
	}

	private void updateDate(Category oldObj, Category obj) {
		oldObj.setName(obj.getName());
	}

	@Override
	public List<Category> findAll() {
		List<Category> categories = categoryRepository.findAll();
		if(categories.size() > 0)
			return categories;
		throw new ResourceNotFoundException("No categories found");
	}

	@Override
	public Category findById(Integer id) {
		Optional<Category> category = categoryRepository.findById(id);
		if(category.isPresent())
			return category.get();
		throw new ResourceNotFoundException(String.format("No categories found with id: %s", id));
	}

	@Override
	public Boolean deleteById(Integer id) {
		findById(id);
		return categoryRepository.deleteById(id);
	}

}
