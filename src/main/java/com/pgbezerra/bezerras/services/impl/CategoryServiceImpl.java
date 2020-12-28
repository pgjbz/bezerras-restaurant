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
	public Category insert(Category category) {
		category.setId(null);
		return categoryRepository.insert(category);
	}

	@Override
	public Boolean update(Category category) {
		Category oldObj = findById(category.getId());
		updateDate(oldObj, category);
		Boolean updated = categoryRepository.update(oldObj);
		LOG.info(String.format("Category %s updated: %s", category, updated));
		return updated;
	}

	private void updateDate(Category oldObj, Category category) {
		oldObj.setName(category.getName());
		oldObj.setIsMenu(category.getIsMenu());
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
			return category.orElseThrow(() ->
		new ResourceNotFoundException(String.format("No categories found with id: %s", id)));
	}

	@Override
	public Boolean deleteById(Integer id) {
		findById(id);
		Boolean deleted = categoryRepository.deleteById(id);
		LOG.info(String.format("Category %s deleted: %s", id, deleted));
		return deleted;
	}

}
