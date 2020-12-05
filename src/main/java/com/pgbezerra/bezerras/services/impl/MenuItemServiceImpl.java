package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.MenuItemRepository;
import com.pgbezerra.bezerras.services.MenuItemService;
import com.pgbezerra.bezerras.services.MenuService;
import com.pgbezerra.bezerras.services.ProductService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class MenuItemServiceImpl implements MenuItemService {
	
	private static final Logger LOG = Logger.getLogger(MenuItemServiceImpl.class);
	
	private final MenuItemRepository menuItemRepository;
	private final MenuService menuService;
	private final ProductService productService;
	
	public MenuItemServiceImpl(
			final MenuItemRepository menuItemRepository,
			final MenuService menuService,
			final ProductService productService) {
		this.menuItemRepository = menuItemRepository;
		this.menuService = menuService;
		this.productService = productService;
	}

	@Override
	public MenuItem insert(MenuItem obj) {
		obj.setMenu(menuService.findById(obj.getMenu().getId()));
		obj.setProduct(productService.findById(obj.getProduct().getId()));
		return menuItemRepository.insert(obj);
	}

	@Override
	public List<MenuItem> findAll() {
		List<MenuItem> menusItem = menuItemRepository.findAll();
		LOG.info(String.format("%s menu item found", menusItem.size()));

		if(!menusItem.isEmpty()) 
			return menusItem;
		throw new ResourceNotFoundException("No menu item found");
	}

	@Override
	public MenuItem findById(Menu menu, Product product) {
		HashMap<Menu, Product> id = new HashMap<>();
		id.put(menu, product);
		Optional<MenuItem> menuItem = menuItemRepository.findById(id);
		LOG.info(String.format("Menu item with menu id %s and product id %s found: %s", menu.getId(), product.getId(), menuItem.isPresent()));
		if(menuItem.isPresent())
			return menuItem.get();
		throw new ResourceNotFoundException(String.format("No menu item with menu id %s and product id %s found", menu.getId(), product.getId()));
	}

	@Override
	public Boolean deleteById(Menu menu, Product product) {
		HashMap<Menu, Product> id = new HashMap<>();
		id.put(menu, product);
		findById(menu, product);
		Boolean deleted = menuItemRepository.deleteById(id);
		LOG.info(String.format("Category %s deleted: %s", id, deleted));
		return deleted;
	}

}
