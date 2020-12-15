package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.Menu;

import java.time.DayOfWeek;

public interface MenuService extends Service<Menu, Long> {

	Menu findByDayOfWeek(DayOfWeek dayOfWeek);

}
