package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.entities.enums.DayOfWeek;
import com.pgbezerra.bezerras.entities.model.Menu;

public interface MenuService {
	
	Menu insert(Menu obj);
	Boolean update(Menu obj);
	List<Menu> findAll();
	Menu findById(Long id);
	Menu findByDayOfWeek(DayOfWeek dayOfWeek);
	Boolean deleteById(Long id);

}
