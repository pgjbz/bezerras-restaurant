package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.repository.MenuItemRepository;
import com.pgbezerra.bezerras.repository.MenuRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;

@Repository
public class MenuRepositoryImpl implements MenuRepository {

	private static final Logger LOG = Logger.getLogger(MenuRepositoryImpl.class);

	private final NamedParameterJdbcTemplate namedJdbcTemplate;
	private final MenuItemRepository menuItemRepository;

	public MenuRepositoryImpl(
			final NamedParameterJdbcTemplate namedJdbcTemplate,
			final MenuItemRepository menuItemRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.menuItemRepository = menuItemRepository;
	}

	@Override
	@Transactional
	public Menu insert(Menu menu) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO  ");
		sql.append("   TB_MENU( ");
		sql.append("     NM_MENU, ");
		sql.append("     DAY_OF_WEEK) ");
		sql.append(" VALUES( ");
		sql.append("   :name, ");
		sql.append("   :dayOfWeek) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", menu.getName());
		paramSource.addValue("dayOfWeek", Objects.nonNull(menu.getDayOfWeek()) ? menu.getDayOfWeek().getValue() : null);

		try {
			int rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
			if (rowsAffected > 0) {
				menu.setId((Long)keyHolder.getKey());
				LOG.info(String.format("New row %s inserted successfuly", menu.toString()));
			} else {
				LOG.error(String.format("Can't insert a new row %s", menu.toString()));
				throw new DatabaseException("Can't insert a new row");
			}
		} catch (DataIntegrityViolationException e) {
			String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), menu.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		} catch (Exception e){
			String msg = String.format("Error insert a new row %s|%s", e.getMessage(), menu.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return menu;
	}

	@Override
	@Transactional
	public Boolean update(Menu menu) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append("   TB_MENU ");
		sql.append(" SET ");
		sql.append("   NM_MENU = :name, ");
		sql.append("   DAY_OF_WEEK = :dayOfWeek ");
		sql.append(" WHERE ");
		sql.append("   ID_MENU = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", menu.getName());
		paramSource.addValue("dayOfWeek", Objects.nonNull(menu.getDayOfWeek()) ? menu.getDayOfWeek().getValue() : null);
		paramSource.addValue("id", menu.getId());

		try {
			return namedJdbcTemplate.update(sql.toString(), paramSource) > 0;
		} catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Error update register with id %s %s", menu.getId(), menu.toString()));
			throw new DatabaseException(e.getMessage());
		} catch (Exception e){
			String msg = String.format("Erro on update menu with id %s", menu.getId());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
	}

	@Override
	@Transactional
	public Boolean deleteById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append("   TB_MENU ");
		sql.append(" WHERE ");
		sql.append("   ID_MENU = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (Exception e){
			String msg = String.format("Erro on delete menu with id %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Menu> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_MENU, ");
		sql.append("   NM_MENU, ");
		sql.append("   DAY_OF_WEEK ");
		sql.append(" FROM ");
		sql.append("   TB_MENU ");
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
		} catch (Exception e){
			String msg = "Error on find all menus";
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return menus;

	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Menu> findById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_MENU, ");
		sql.append("   NM_MENU, ");
		sql.append("   DAY_OF_WEEK ");
		sql.append(" FROM ");
		sql.append("   TB_MENU ");
		sql.append(" WHERE ");
		sql.append("   ID_MENU = :id ");

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
			if(Objects.nonNull(menu))
				LOG.info(String.format("Menu with id: %s found successfuly %s", id, menu.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No menu found with id: %s", id));
		} catch (Exception e){
			String msg = String.format("Error on find menu with id %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return Optional.ofNullable(menu);
	}

	@Override
	@Transactional
	public List<Menu> insertAll(List<Menu> list) {
		for (Menu menu : list)
			insert(menu);
		return list;
	}

	@Override
	public Optional<Menu> findByDayOfWeek(DayOfWeek dayOfWeek) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_MENU, ");
		sql.append("   NM_MENU, ");
		sql.append("   DAY_OF_WEEK ");
		sql.append(" FROM ");
		sql.append("   TB_MENU ");
		sql.append(" WHERE ");
		sql.append("   DAY_OF_WEEK = :dayOfWeek ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("dayOfWeek", dayOfWeek.getValue());

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
			if(Objects.nonNull(menu))
				LOG.info(String.format("Menu with day of week: %s found successfuly %s", dayOfWeek, menu.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No menu found with day of week: %s", dayOfWeek));
		} catch (Exception e){
			String msg = String.format(String.format("Error on find menu with day of week: %s", dayOfWeek));
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return Optional.ofNullable(menu);
	}

}
