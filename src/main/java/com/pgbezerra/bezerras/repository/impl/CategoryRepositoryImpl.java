package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

	private static final Logger LOG = Logger.getLogger(CategoryRepositoryImpl.class);

	private final NamedParameterJdbcTemplate namedJdbcTemplate;

	public CategoryRepositoryImpl(final NamedParameterJdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = namedJdbcTemplate;
	}

	@Override
	@Transactional
	public Category insert(Category category) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO  ");
		sql.append("   TB_CATEGORY( ");
		sql.append("   NM_CATEGORY, ");
		sql.append("   FL_MENU) ");
		sql.append(" VALUES( ");
		sql.append("   :name, ");
		sql.append("   :menu) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", category.getName());
		paramSource.addValue("menu", category.getIsMenu());

		try {
			int rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[]{"id_category"});
			if (rowsAffected > 0) {
				category.setId(keyHolder.getKey().intValue());
				LOG.info(String.format("New row %s inserted successfuly", category.toString()));
			} else {
				LOG.error(String.format("Can't insert a new row %s", category.toString()));
				throw new DatabaseException("Can't insert a new row");
			}
		} catch (DataIntegrityViolationException e) {
			String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), category.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		} catch (Exception e){
			String msg = String.format("Error on insert a new row %s", category.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return category;
	}

	@Override
	@Transactional
	public Boolean update(Category category) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append("   TB_CATEGORY ");
		sql.append(" SET ");
		sql.append("   NM_CATEGORY = :name, ");
		sql.append("   FL_MENU = :menu ");
		sql.append(" WHERE ");
		sql.append("   ID_CATEGORY = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", category.getName());
		parameters.put("menu", category.getIsMenu());
		parameters.put("id", category.getId());

		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Error update register with id %s %s", category.getId(), category.toString()));
			throw new DatabaseException(e.getMessage());
		} catch (Exception e){
			String msg = String.format("Error on update category with id %s", category.getId());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
	}

	@Override
	@Transactional
	public Boolean deleteById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append("   TB_CATEGORY ");
		sql.append(" WHERE ");
		sql.append("   ID_CATEGORY = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (Exception e){
			String msg = String.format("Error on delete category with id %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Category> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_CATEGORY, ");
		sql.append("   NM_CATEGORY, ");
		sql.append("   FL_MENU ");
		sql.append(" FROM ");
		sql.append("   TB_CATEGORY ");
		List<Category> categories = null;
		try {
			return namedJdbcTemplate.query(sql.toString(), rowMapper);
		} catch (EmptyResultDataAccessException e) {
			categories = new ArrayList<>();
		} catch (Exception e){
			String msg = "Error on find all category";
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return categories;

	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Category> findById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_CATEGORY, ");
		sql.append("   NM_CATEGORY, ");
		sql.append("   FL_MENU ");
		sql.append(" FROM ");
		sql.append("   TB_CATEGORY ");
		sql.append(" WHERE ");
		sql.append("   ID_CATEGORY = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		Category category = null;

		try {
			category = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
			if(Objects.nonNull(category))
				LOG.info(String.format("Category with id: %s found successfuly %s", id, category.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No category found with id: %s", id));
		} catch(Exception e){
			String msg = String.format("Error on find category with id %s", id);
			LOG.error(msg, e);
			LOG.error(msg);
		}

		return Optional.ofNullable(category);
	}

	@Override
	@Transactional
	public List<Category> insertAll(List<Category> list) {
		for (Category category : list)
			insert(category);
		return list;
	}

	private final RowMapper<Category> rowMapper = (rs, rownum) -> {
		Category category = new Category();
		category.setId(rs.getInt("ID_CATEGORY"));
		category.setName(rs.getString("NM_CATEGORY"));
		category.setIsMenu(rs.getBoolean("FL_MENU"));
		return category;
	};

}
