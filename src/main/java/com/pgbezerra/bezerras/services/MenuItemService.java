package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
import com.pgbezerra.bezerras.entities.model.Product;

public interface MenuItemService {
	
	MenuItem insert(MenuItem obj);
	List<MenuItem> findAll();
	MenuItem findById(Menu menu, Product product);
	Boolean deleteById(Menu menu, Product product);

}
