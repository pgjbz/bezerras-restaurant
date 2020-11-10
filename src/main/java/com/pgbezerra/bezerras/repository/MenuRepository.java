package com.pgbezerra.bezerras.repository;

import java.util.Optional;

import com.pgbezerra.bezerras.entities.enums.DayOfWeek;
import com.pgbezerra.bezerras.entities.model.Menu;

public interface MenuRepository extends Repository<Menu, Long> {
	
	Optional<Menu> findByDayOfWeek(DayOfWeek dayOfWeek);

}
