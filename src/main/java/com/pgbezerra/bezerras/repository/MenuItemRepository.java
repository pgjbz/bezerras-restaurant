package com.pgbezerra.bezerras.repository;

import java.util.List;
import java.util.Map;

import com.pgbezerra.bezerras.models.entity.Menu;
import com.pgbezerra.bezerras.models.entity.MenuItem;
import com.pgbezerra.bezerras.models.entity.Product;

public interface MenuItemRepository extends Repository<MenuItem, Map<Menu, Product>> {
	
	List<MenuItem> findByMenu(Menu menu);

}
