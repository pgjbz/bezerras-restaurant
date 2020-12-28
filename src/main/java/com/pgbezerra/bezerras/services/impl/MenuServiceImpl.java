package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.repository.MenuRepository;
import com.pgbezerra.bezerras.services.MenuService;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger LOG = Logger.getLogger(MenuServiceImpl.class);

    private final MenuRepository menuRepository;

    public MenuServiceImpl(final MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public Menu insert(Menu menu) {
        menu.setId(null);
        try {
            findByDayOfWeek(menu.getDayOfWeek());
        } catch (ResourceNotFoundException e) {
            LOG.info(String.format("Insert a new menu in day of week: %s", menu.getDayOfWeek()));
            return menuRepository.insert(menu);
        }
        String msg = String.format("Alredy exists menu in this day of week: %s", menu.getDayOfWeek());
        LOG.info(msg);
        throw new ResourceBadRequestException(msg);
    }

    @Override
    public Boolean update(Menu menu) {
        LOG.info(String.format("Updating menu %s", menu.toString()));
        Menu oldObj = findById(menu.getId());
        updateData(oldObj, menu);
        return menuRepository.update(oldObj);
    }

    private void updateData(Menu oldObj, Menu menu) {
        oldObj.setDayOfWeek(menu.getDayOfWeek().getValue());
        oldObj.setName(menu.getName());
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
        return menu.orElseThrow(() ->
                new ResourceNotFoundException(String.format("No menus found with id: %s", id)));
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
        Optional<Menu> menu = menuRepository.findByDayOfWeek(dayOfWeek);
        LOG.info(String.format("Menu in day of week %s founded %s", dayOfWeek, menu.isPresent()));
        return menu.orElseThrow(
                () -> new ResourceNotFoundException(String.format("No menu in day of week %s found", dayOfWeek)));
    }

}
