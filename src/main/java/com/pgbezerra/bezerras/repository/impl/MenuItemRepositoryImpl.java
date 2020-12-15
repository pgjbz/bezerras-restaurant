package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.MenuItemRepository;
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

import java.util.*;

@Repository
public class MenuItemRepositoryImpl implements MenuItemRepository {

	private static final Logger LOG = Logger.getLogger(MenuItemRepositoryImpl.class);

	private final NamedParameterJdbcTemplate namedJdbcTemplate;

	public MenuItemRepositoryImpl(final NamedParameterJdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = namedJdbcTemplate;
	}

	@Override
	@Transactional
	public MenuItem insert(MenuItem menuItem) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO  ");
		sql.append("   TB_MENU_ITEM( ");
		sql.append("   ID_MENU, ");
		sql.append("   ID_PRODUCT) ");
		sql.append(" VALUES( ");
		sql.append("   :menu, ");
		sql.append("   :product) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("menu", menuItem.getMenu().getId());
		paramSource.addValue("product", menuItem.getProduct().getId());
		int rowsAffected = 0;

		try {
			rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
			if (rowsAffected > 0)
				LOG.info(String.format("New row %s inserted successfuly", menuItem.toString()));
			else {
				LOG.error(String.format("Can't insert a new row %s", menuItem.toString()));
				throw new DatabaseException("Can't insert a new row");
			}
		} catch (DataIntegrityViolationException e) {
			String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), menuItem.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return menuItem;
	}

	@Override
	public Boolean update(MenuItem menuItem) {
		return Boolean.FALSE;
	}

	@Override
	@Transactional
	public Boolean deleteById(Map<Menu, Product> id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append("   TB_MENU_ITEM ");
		sql.append(" WHERE ");
		sql.append("   ID_PRODUCT = :product ");
		sql.append("   AND ID_MENU = :menu ");

		Map<String, Object> parameters = new HashMap<>();
		Menu menu = id.keySet().stream().findFirst().orElse(new Menu());
		Product product = id.get(menu);
		parameters.put("product", product);
		parameters.put("menu", menu.getId());

		return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuItem> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   P.ID_PRODUCT, ");
		sql.append("   P.NM_PRODUCT, ");
		sql.append("   P.VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append("   TB_MENU_ITEM MI ");
		sql.append("   JOIN TB_MENU M ");
		sql.append("     ON M.ID_MENU = MI.ID_MENU ");
		sql.append("   JOIN TB_PRODUCT P ");
		sql.append("     ON P.ID_PRODUCT = MI.ID_PRODUCT ");

		final Map<Integer, Product> products = new HashMap<>();

		List<MenuItem> menuItems = null;
		try {
			return namedJdbcTemplate.query(sql.toString(), (rs, rownum) -> {
				MenuItem menuItem = new MenuItem();

				Integer idProduct = rs.getInt("ID_PRODUCT");

				if (products.containsKey(idProduct))
					menuItem.setProduct(products.get(idProduct));
				else {
					Product product = new Product();
					product.setId(idProduct);
					product.setName(rs.getString("ID_PRODUCT"));
					product.setValue(rs.getBigDecimal("VL_PRODUCT"));
					menuItem.setProduct(product);
					products.put(idProduct, product);
				}
				return menuItem;

			});
		} catch (EmptyResultDataAccessException e) {
			menuItems = new ArrayList<>();
		}

		return menuItems;

	}

	@Override
	@Transactional(readOnly = true)
	public Optional<MenuItem> findById(Map<Menu, Product> id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   P.ID_PRODUCT, ");
		sql.append("   P.NM_PRODUCT, ");
		sql.append("   P.VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append("   TB_MENU_ITEM MI ");
		sql.append("   JOIN TB_MENU M ");
		sql.append("     ON M.ID_MENU = MI.ID_MENU ");
		sql.append("   JOIN TB_PRODUCT P ");
		sql.append("     ON P.ID_PRODUCT = MI.ID_PRODUCT ");
		sql.append(" WHERE ");
		sql.append("   P.ID_PRODUCT = :product ");
		sql.append("   AND MI.ID_MENU = :menu ");

		Map<String, Object> parameters = new HashMap<>();
		Menu menu = id.keySet().stream().findFirst().orElse(new Menu());
		Product product = id.get(menu);
		parameters.put("product", product.getId());
		parameters.put("menu", menu.getId());

		MenuItem menuItem = null;

		try {
			menuItem = namedJdbcTemplate.queryForObject(sql.toString(), parameters, (rs, rownum) -> {
				MenuItem mi = new MenuItem();

				Integer idProduct = rs.getInt("ID_PRODUCT");

				Product p = new Product();
				p.setId(idProduct);
				p.setName(rs.getString("NM_PRODUCT"));
				p.setValue(rs.getBigDecimal("VL_PRODUCT"));
				mi.setProduct(p);

				return mi;

			});
			LOG.info(String.format("MenuItem with id: %s found successfuly %s", id, menuItem.toString()));
		} catch (

		EmptyResultDataAccessException e) {
			LOG.warn(String.format("No menuItem found with id: %s", id));
		}

		return Optional.ofNullable(menuItem);
	}

	@Override
	@Transactional
	public List<MenuItem> insertAll(List<MenuItem> list) {
		for (MenuItem menuItem : list)
			insert(menuItem);
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuItem> findByMenu(Menu menu) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   P.ID_PRODUCT, ");
		sql.append("   P.NM_PRODUCT, ");
		sql.append("   P.VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append("   TB_MENU_ITEM MI ");
		sql.append("   JOIN TB_MENU M ");
		sql.append("     ON M.ID_MENU = MI.ID_MENU ");
		sql.append("   JOIN TB_PRODUCT P ");
		sql.append("     ON P.ID_PRODUCT = MI.ID_PRODUCT ");
		sql.append(" WHERE ");
		sql.append("   MI.ID_MENU = :menu ");

		final Map<Integer, Product> products = new HashMap<>();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("menu", menu.getId());

		List<MenuItem> menuItems = null;
		try {
			return namedJdbcTemplate.query(sql.toString(), parameters, (rs, rownum) -> {
				MenuItem menuItem = new MenuItem();

				Integer idProduct = rs.getInt("ID_PRODUCT");

				if (products.containsKey(idProduct))
					menuItem.setProduct(products.get(idProduct));
				else {
					Product product = new Product();
					product.setId(idProduct);
					product.setName(rs.getString("NM_PRODUCT"));
					product.setValue(rs.getBigDecimal("VL_PRODUCT"));
					menuItem.setProduct(product);
					products.put(idProduct, product);
				}
				return menuItem;

			});
		} catch (EmptyResultDataAccessException e) {
			menuItems = new ArrayList<>();
		}

		return menuItems;
	}

}
