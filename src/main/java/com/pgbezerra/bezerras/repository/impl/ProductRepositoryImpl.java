package com.pgbezerra.bezerras.repository.impl;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@Repository
public class ProductRepositoryImpl implements ProductRepository{
	
	private static final Logger LOG = Logger.getLogger(CategoryRepositoryImpl.class);
	
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private CategoryRepository categoryRepository;
	private Map<Integer, Category> categories;
	
	public ProductRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate,
			CategoryRepository categoryRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.categoryRepository = categoryRepository;
		
		categories = new HashMap<>();
	}

	@Override
	public Product insert(Product obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append(" 	TB_PRODUCT(NM_PRODUCT, VL_PRODUCT, ID_CATEGORY) ");
		sql.append(" VALUES ");
		sql.append(" 	(:name, :value, :category) ");
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", obj.getName());
		paramSource.addValue("value", obj.getValue());
		
		Category category = obj.getCategory();
		
		paramSource.addValue("category", nonNull(category) ? category.getId() : null);
		
		int rowsAffected = 0;
		
		try {
			rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
		} catch (DataIntegrityViolationException e) {
			LOG.error(e.getMessage());
		}

		if (rowsAffected > 0) {
			obj.setId(keyHolder.getKey().intValue());
			LOG.info(String.format("New row %s inserted successfuly", obj.toString()));
		} else {
			LOG.error(String.format("Can't insert a new row %s", obj.toString()));
			throw new DatabaseException("Can't insert a new row");
		}
		return obj;
	}

	@Override
	public Boolean update(Product obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append(" 	TB_PRODUCT ");
		sql.append(" SET ");
		sql.append(" 	NM_PRODUCT = :name, ");
		sql.append(" 	ID_CATEGORY = :category, ");
		sql.append(" 	VL_PRODUCT = :value ");
		sql.append(" WHERE ");
		sql.append(" 	ID_PRODUCT = :id ");
		
		Category category = obj.getCategory();
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", obj.getName());
		parameters.put("category", nonNull(category) ? category.getId() : null);
		parameters.put("value", obj.getValue());
		parameters.put("id", obj.getId());
		
		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Error update register with id %s %s", obj.getId(), obj.toString()));
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public Boolean deleteById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append(" 	TB_PRODUCT ");
		sql.append(" WHERE ");
		sql.append(" 	ID_CATEGORY = :id ");
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		
		return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
	}

	@Override
	public List<Product> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_PRODUCT, ");
		sql.append(" 	NM_PRODUCT, ");
		sql.append(" 	ID_CATEGORY, ");
		sql.append(" 	VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append(" 	TB_PRODUCT ");
		
		List<Product> products = null;
		try {
			return namedJdbcTemplate.query(sql.toString(), rowMapper);
		} catch (EmptyResultDataAccessException e) {
			products = new ArrayList<>();
		}

		return products;
	}

	@Override
	public Optional<Product> findById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_PRODUCT, ");
		sql.append(" 	NM_PRODUCT, ");
		sql.append(" 	ID_CATEGORY, ");
		sql.append(" 	VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append(" 	TB_PRODUCT ");
		sql.append(" WHERE ");
		sql.append(" 	ID_PRODUCT = :id ");
		
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		
		Product product = null;

		try {
			product = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
			LOG.info(String.format("Category with id: %s found successfuly %s", id, product.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No category found with id: %s", id));
		}

		return Optional.ofNullable(product);
	}

	@Override
	public List<Product> insertAll(List<Product> list) {
		for(Product product: list)
			insert(product);
		return list;
	}
	
	
	private RowMapper<Product> rowMapper = (rs, rownum) -> {
		Product product = new Product();
		product.setId(rs.getInt("ID_PRODUCT"));
		product.setName(rs.getString("NM_PRODUCT"));
		product.setValue(rs.getBigDecimal("VL_PRODUCT"));
		
		Integer idCategory = rs.getInt("ID_CATEGORY");
		
		if(categories.containsKey(idCategory))
			product.setCategory(categories.get(idCategory));
		else {
			Optional<Category>  category = categoryRepository.findById(idCategory);
			if(category.isPresent()) {
				categories.put(idCategory, category.get());
				product.setCategory(category.get());
			}
		}
		return product;
	};
	
}
