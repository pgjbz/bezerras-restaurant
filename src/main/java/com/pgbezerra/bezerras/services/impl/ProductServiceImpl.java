package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.MenuService;
import com.pgbezerra.bezerras.services.ProductService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.utils.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

	private static final Logger LOG = Logger.getLogger(ProductServiceImpl.class);

	private final ProductRepository productRepository;
	private final CategoryService categoryService;
	private final MenuService menuService;

	public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService,
			MenuService menuService) {
		this.productRepository = productRepository;
		this.categoryService = categoryService;
		this.menuService = menuService;
	}

	@Override
	public Product insert(Product product) {
		product.setId(null);
		categoryService.findById(product.getCategory().getId());
		return productRepository.insert(product);
	}

	@Override
	public Boolean update(Product product) {
		categoryService.findById(product.getCategory().getId());
		Product oldObj = findById(product.getId());
		updateData(oldObj, product);
		Boolean updated = productRepository.update(oldObj);
		LOG.info(String.format("Product %s updated: %s", product, updated));
		return updated;
	}

	private void updateData(Product oldObj, Product product) {
		oldObj.setCategory(product.getCategory());
		oldObj.setName(product.getName());
		oldObj.setValue(product.getValue());
	}

	@Override
	public List<Product> findAll() {
		List<Product> products = productRepository.findAll();
		LOG.info(String.format("%s products found", products.size()));
		if (!products.isEmpty())
			return products;
		throw new ResourceNotFoundException("No categories found");
	}

	@Override
	public List<Product> findByCategory(Category cat) {
		Category category = categoryService.findById(cat.getId());
		List<Product> products;
		if (category.getIsMenu()) {
			Menu menu = menuService.findByDayOfWeek(DateUtil.currentDayOfWeek());
			products = menu.getItems().stream().map(MenuItem::getProduct).collect(Collectors.toList());
		} else
			products = productRepository.findByCategory(category);
		
		if(!products.isEmpty())
			return products;
		String msg = String.format("No products found for the category %s", cat.getId());
		LOG.info(msg);
		throw new ResourceNotFoundException(msg);
	}

	@Override
	public Product findById(Integer id) {
		Optional<Product> product = productRepository.findById(id);
		LOG.info(String.format("Product with id %s found: %s", id, product.isPresent()));
		if (product.isPresent())
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
