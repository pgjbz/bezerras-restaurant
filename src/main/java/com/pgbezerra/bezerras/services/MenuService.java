package com.pgbezerra.bezerras.services;

import java.time.DayOfWeek;

import com.pgbezerra.bezerras.models.entity.Menu;

public interface MenuService extends Service<Menu, Long> {

	Menu findByDayOfWeek(DayOfWeek dayOfWeek);

}
