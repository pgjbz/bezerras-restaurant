package com.pgbezerra.bezerras.services.impl;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.pgbezerra.bezerras.entities.enums.DayOfWeek;
import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.repository.MenuRepository;
import com.pgbezerra.bezerras.services.MenuService;
import com.pgbezerra.bezerras.services.exception.BadRequestException;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;

@Service
public class MenuServiceImpl implements MenuService {

	private static final Logger LOG = Logger.getLogger(MenuServiceImpl.class);

	private MenuRepository menuRepository;

	public MenuServiceImpl(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	@Override
	public Menu insert(Menu obj) {
		obj.setId(null);
		try {
			findByDayOfWeek(obj.getDayOfWeek());
		} catch (ResourceNotFoundException e) {
			LOG.info(String.format("Insert a new menu in day of week: %s", obj.getDayOfWeek()));
			return menuRepository.insert(obj);
		}
		String msg = String.format("Alredy exists menu in this day of week: %s", obj.getDayOfWeek());
		LOG.info(msg);
		throw new BadRequestException(msg);
	}

	@Override
	public Boolean update(Menu obj) {
		Menu oldObj = findById(obj.getId());
		updateData(oldObj, obj);
		return menuRepository.update(oldObj);
	}

	private void updateData(Menu oldObj, Menu obj) {
		oldObj.setDayOfWeek(obj.getDayOfWeek().getDayCode());
		oldObj.setName(obj.getName());
	}

	@Override
	public List<Menu> findAll() {

		List<Menu> menus = menuRepository.findAll();
		LOG.info(String.format("%s menus found", menus.size()));

		if (!menus.isEmpty())
			return menus;
		throw new ResourceNotFoundException("No menus found");
	}

	@Override
	public Menu findById(Long id) {
		Optional<Menu> menu = menuRepository.findById(id);
		LOG.info(String.format("Menu with id %s found: %s", id, menu.isPresent()));
		if (menu.isPresent())
			return menu.get();
		throw new ResourceNotFoundException(String.format("No menus found with id: %s", id));
	}

	@Override
	public Boolean deleteById(Long id) {
		findById(id);
		Boolean deleted = menuRepository.deleteById(id);
		LOG.info(String.format("Category %s deleted: %s", id, deleted));
		return deleted;
	}

	@Override
	public Menu findByDayOfWeek(DayOfWeek dayOfWeek) {
		return menuRepository.findByDayOfWeek(dayOfWeek).orElseThrow(
				() -> new ResourceNotFoundException(String.format("No menu in day of week %s found", dayOfWeek)));
	}

}
