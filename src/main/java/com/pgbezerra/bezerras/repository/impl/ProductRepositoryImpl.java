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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

	private static final Logger LOG = Logger.getLogger(ProductRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private CategoryRepository categoryRepository;

	public ProductRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate, CategoryRepository categoryRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.categoryRepository = categoryRepository;
	}

	@Override
	@Transactional
	public Product insert(Product obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append("   TB_PRODUCT(NM_PRODUCT, VL_PRODUCT, ID_CATEGORY) ");
		sql.append(" VALUES ");
		sql.append("   (:name, :value, :category) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", obj.getName());
		paramSource.addValue("value", obj.getValue());

		Category category = obj.getCategory();

		paramSource.addValue("category", nonNull(category) ? category.getId() : null);

		int rowsAffected = 0;
		try {
			rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
			if (rowsAffected > 0) {
				obj.setId(keyHolder.getKey().intValue());
				LOG.info(String.format("New row %s inserted successfuly", obj.toString()));
			} else {
				LOG.error(String.format("Can't insert a new row %s", obj.toString()));
				throw new DatabaseException("Can't insert a new row");
			}
		} catch (DataIntegrityViolationException e) {
			String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), obj.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return obj;
	}

	@Override
	@Transactional
	public Boolean update(Product obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append("   TB_PRODUCT ");
		sql.append(" SET ");
		sql.append("   NM_PRODUCT = :name, ");
		sql.append("   ID_CATEGORY = :category, ");
		sql.append("   VL_PRODUCT = :value ");
		sql.append(" WHERE ");
		sql.append("   ID_PRODUCT = :id ");

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
	@Transactional
	public Boolean deleteById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append("   TB_PRODUCT ");
		sql.append(" WHERE ");
		sql.append("   ID_CATEGORY = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);

		return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NESTED)
	public List<Product> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_PRODUCT, ");
		sql.append("   NM_PRODUCT, ");
		sql.append("   ID_CATEGORY, ");
		sql.append("   VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append("   TB_PRODUCT ");

		final Map<Integer, Category> categories = new HashMap<>();

		List<Product> products = null;
		try {
			products = namedJdbcTemplate.query(sql.toString(), (rs, rownum) -> {
				Product product = new Product();
				product.setId(rs.getInt("ID_PRODUCT"));
				product.setName(rs.getString("NM_PRODUCT"));
				product.setValue(rs.getBigDecimal("VL_PRODUCT"));

				Integer idCategory = rs.getInt("ID_CATEGORY");

				if (categories.containsKey(idCategory)) {
					LOG.info(String.format("The category[%s] has already been found", idCategory));
					product.setCategory(categories.get(idCategory));
				} else {
					LOG.info(String.format("Finding category %s", idCategory));
					Optional<Category> category = categoryRepository.findById(idCategory);
					if (category.isPresent()) {
						categories.put(idCategory, category.get());
						product.setCategory(category.get());
					} else
						categories.put(idCategory, null);
				}
				return product;
			});
			return products;
		} catch (EmptyResultDataAccessException e) {
			products = new ArrayList<>();
		}

		return products;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NESTED)
	public Optional<Product> findById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_PRODUCT, ");
		sql.append("   NM_PRODUCT, ");
		sql.append("   ID_CATEGORY, ");
		sql.append("   VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append("   TB_PRODUCT ");
		sql.append(" WHERE ");
		sql.append("   ID_PRODUCT = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		Product product = null;

		try {
			product = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rownum) -> {
				Product obj = new Product();
				obj.setId(rs.getInt("ID_PRODUCT"));
				obj.setName(rs.getString("NM_PRODUCT"));
				obj.setValue(rs.getBigDecimal("VL_PRODUCT"));

				Optional<Category> category = categoryRepository.findById(rs.getInt("ID_CATEGORY"));

				if (category.isPresent())
					obj.setCategory(category.get());

				return obj;
			});
			LOG.info(String.format("Product with id: %s found successfuly %s", id, product.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No product found with id: %s", id));
		}

		return Optional.ofNullable(product);
	}

	@Override
	@Transactional
	public List<Product> insertAll(List<Product> list) {
		for (Product product : list)
			insert(product);
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Product> findByCategory(Category category) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_PRODUCT, ");
		sql.append("   NM_PRODUCT, ");
		sql.append("   ID_CATEGORY, ");
		sql.append("   VL_PRODUCT ");
		sql.append(" FROM ");
		sql.append("   TB_PRODUCT ");
		sql.append(" WHERE ");
		sql.append("   ID_CATEGORY = :category ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("category", category.getId());

		List<Product> products = null;

		try {
			products = namedJdbcTemplate.query(sql.toString(), paramSource, (rs, rownum) -> {
				Product obj = new Product();
				obj.setId(rs.getInt("ID_PRODUCT"));
				obj.setName(rs.getString("NM_PRODUCT"));
				obj.setValue(rs.getBigDecimal("VL_PRODUCT"));

				obj.setCategory(category);
				return obj;
			});
			LOG.info(String.format("Products category id: %s", category.getId()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No product found with id: %s", category.getId()));
			products = new ArrayList<>();
		}

		return products;
	}

}
