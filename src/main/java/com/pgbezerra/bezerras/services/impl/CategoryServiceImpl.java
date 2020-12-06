package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	private static final Logger LOG = Logger.getLogger(CategoryServiceImpl.class);

	@Autowired
	private final  CategoryRepository categoryRepository;
	
	public CategoryServiceImpl(final CategoryRepository categoryRepository) {
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
		oldObj.setIsMenu(obj.getIsMenu());
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
		LOG.info(String.format("Category with id %s found: %s", id, category.isPresent()));
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
