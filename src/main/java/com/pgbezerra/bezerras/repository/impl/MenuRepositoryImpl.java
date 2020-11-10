package com.pgbezerra.bezerras.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pgbezerra.bezerras.entities.enums.DayOfWeek;
import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.repository.MenuItemRepository;
import com.pgbezerra.bezerras.repository.MenuRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@Repository
public class MenuRepositoryImpl implements MenuRepository {

	private static final Logger LOG = Logger.getLogger(MenuRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private MenuItemRepository menuItemRepository;

	public MenuRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate, MenuItemRepository menuItemRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.menuItemRepository = menuItemRepository;
	}

	@Override
	@Transactional
	public Menu insert(Menu obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO  ");
		sql.append(" 	TB_MENU( ");
		sql.append(" 		NM_MENU, ");
		sql.append(" 		DAY_OF_WEEK) ");
		sql.append(" VALUES( ");
		sql.append(" 	:name, ");
		sql.append(" 	:dayOfWeek) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", obj.getName());
		paramSource.addValue("dayOfWeek", Objects.nonNull(obj.getDayOfWeek()) ? obj.getDayOfWeek().getDayCode() : null);
		int rowsAffected = 0;

		try {
			rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
		} catch (DataIntegrityViolationException e) {
			LOG.error(e.getMessage());
		}

		if (rowsAffected > 0) {
			obj.setId(keyHolder.getKey().longValue());
			LOG.info(String.format("New row %s inserted successfuly", obj.toString()));
		} else {
			LOG.error(String.format("Can't insert a new row %s", obj.toString()));
			throw new DatabaseException("Can't insert a new row");
		}
		return obj;
	}

	@Override
	@Transactional
	public Boolean update(Menu obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append(" 	TB_MENU ");
		sql.append(" SET ");
		sql.append(" 	NM_MENU = :name, ");
		sql.append(" 	DAY_OF_WEEK = :dayOfWeek ");
		sql.append(" WHERE ");
		sql.append(" 	ID_MENU = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", obj.getName());
		paramSource.addValue("dayOfWeek", Objects.nonNull(obj.getDayOfWeek()) ? obj.getDayOfWeek().getDayCode() : null);
		paramSource.addValue("id", obj.getId());

		try {
			return namedJdbcTemplate.update(sql.toString(), paramSource) > 0;
		} catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Error update register with id %s %s", obj.getId(), obj.toString()));
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Boolean deleteById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append(" 	TB_MENU ");
		sql.append(" WHERE ");
		sql.append(" 	ID_MENU = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);

		return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Menu> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_MENU, ");
		sql.append(" 	NM_MENU, ");
		sql.append(" 	DAY_OF_WEEK ");
		sql.append(" FROM ");
		sql.append(" 	TB_MENU ");
		List<Menu> menus = null;
		
		try {
			return namedJdbcTemplate.query(sql.toString(), (rs, rownum) -> {
				Menu menu = new Menu();
				menu.setId(rs.getLong("ID_MENU"));
				menu.setName(rs.getString("NM_MENU"));
				menu.setDayOfWeek(rs.getInt("DAY_OF_WEEK"));
				menu.getItems().addAll(menuItemRepository.findByMenu(menu));
				return menu;
			});
		} catch (EmptyResultDataAccessException e) {
			menus = new ArrayList<>();
		}

		return menus;

	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Menu> findById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_MENU, ");
		sql.append(" 	NM_MENU, ");
		sql.append(" 	DAY_OF_WEEK ");
		sql.append(" FROM ");
		sql.append(" 	TB_MENU ");
		sql.append(" WHERE ");
		sql.append(" 	ID_MENU = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		Menu menu = null;

		try {
			menu = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rownum) -> {
				Menu m = new Menu();
				m.setId(rs.getLong("ID_MENU"));
				m.setName(rs.getString("NM_MENU"));
				m.setDayOfWeek(rs.getInt("DAY_OF_WEEK"));
				m.getItems().addAll(menuItemRepository.findByMenu(m));
				return m;
			});
			LOG.info(String.format("Menu with id: %s found successfuly %s", id, menu.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No menu found with id: %s", id));
		}

		return Optional.ofNullable(menu);
	}

	@Override
	@Transactional
	public List<Menu> insertAll(List<Menu> list) {
		for(Menu menu: list)
			insert(menu);
		return list;
	}

	@Override
	public Optional<Menu> findByDayOfWeek(DayOfWeek dayOfWeek) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_MENU, ");
		sql.append(" 	NM_MENU, ");
		sql.append(" 	DAY_OF_WEEK ");
		sql.append(" FROM ");
		sql.append(" 	TB_MENU ");
		sql.append(" WHERE ");
		sql.append(" 	DAY_OF_WEEK = :dayOfWeek ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("dayOfWeek", dayOfWeek.getDayCode());

		Menu menu = null;

		try {
			menu = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rownum) -> {
				Menu m = new Menu();
				m.setId(rs.getLong("ID_MENU"));
				m.setName(rs.getString("NM_MENU"));
				m.setDayOfWeek(rs.getInt("DAY_OF_WEEK"));
				m.getItems().addAll(menuItemRepository.findByMenu(m));
				return m;
			});
			LOG.info(String.format("Menu with day of week: %s found successfuly %s", dayOfWeek, menu.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No menu found with id: %s", dayOfWeek));
		}

		return Optional.ofNullable(menu);
	}


}
