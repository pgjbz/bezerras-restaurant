package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.models.entity.Menu;
import com.pgbezerra.bezerras.models.entity.MenuItem;
import com.pgbezerra.bezerras.models.entity.Product;

public interface MenuItemService {
	
	MenuItem insert(MenuItem obj);
	List<MenuItem> findAll();
	MenuItem findById(Menu menu, Product product);
	Boolean deleteById(Menu menu, Product product);

}
