package com.pgbezerra.bezerras.repository;

import java.util.List;
import java.util.Map;

import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
import com.pgbezerra.bezerras.entities.model.Product;

public interface MenuItemRepository extends Repository<MenuItem, Map<Menu, Product>> {
	
	List<MenuItem> findByMenu(Menu menu);

}
