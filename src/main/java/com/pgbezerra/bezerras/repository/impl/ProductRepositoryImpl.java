package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
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

import java.util.*;

import static java.util.Objects.nonNull;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

	private static final Logger LOG = Logger.getLogger(ProductRepositoryImpl.class);

	private final NamedParameterJdbcTemplate namedJdbcTemplate;
	private final CategoryRepository categoryRepository;

	public ProductRepositoryImpl(
			final NamedParameterJdbcTemplate namedJdbcTemplate,
			final CategoryRepository categoryRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.categoryRepository = categoryRepository;
	}

	@Override
	@Transactional
	public Product insert(Product product) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append("   TB_PRODUCT(NM_PRODUCT, VL_PRODUCT, ID_CATEGORY) ");
		sql.append(" VALUES ");
		sql.append("   (:name, :value, :category) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", product.getName());
		paramSource.addValue("value", product.getValue());

		Category category = product.getCategory();

		paramSource.addValue("category", nonNull(category) ? category.getId() : null);

		try {
			int rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder,  new String[]{"id_product"});
			if (rowsAffected > 0) {
				product.setId(keyHolder.getKey().intValue());
				LOG.info(String.format("New row %s inserted successfuly", product.toString()));
			} else {
				LOG.error(String.format("Can't insert a new row %s", product.toString()));
				throw new DatabaseException("Can't insert a new row");
			}
		} catch (DataIntegrityViolationException e) {
			String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), product.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		} catch (Exception e){
			String msg = String.format("Error on insert a new row %s|%s", e.getMessage(), product.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return product;
	}

	@Override
	@Transactional
	public Boolean update(Product product) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append("   TB_PRODUCT ");
		sql.append(" SET ");
		sql.append("   NM_PRODUCT = :name, ");
		sql.append("   ID_CATEGORY = :category, ");
		sql.append("   VL_PRODUCT = :value ");
		sql.append(" WHERE ");
		sql.append("   ID_PRODUCT = :id ");

		Category category = product.getCategory();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", product.getName());
		parameters.put("category", nonNull(category) ? category.getId() : null);
		parameters.put("value", product.getValue());
		parameters.put("id", product.getId());

		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Error update register with id %s %s", product.getId(), product.toString()));
			throw new DatabaseException(e.getMessage());
		} catch (Exception e){
			String msg = String.format("Error on update product with id %s", product.getId());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
	}

	@Override
	@Transactional
	public Boolean deleteById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append("   TB_PRODUCT ");
		sql.append(" WHERE ");
		sql.append("   ID_PRODUCT = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (Exception e){
			String msg = String.format("Error on delete product with id %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
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
		} catch (Exception e){
			String msg = "Error on find all products";
			LOG.error(msg, e);
			throw new DatabaseException(msg);
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
				Product p = new Product();
				p.setId(rs.getInt("ID_PRODUCT"));
				p.setName(rs.getString("NM_PRODUCT"));
				p.setValue(rs.getBigDecimal("VL_PRODUCT"));

				Optional<Category> category = categoryRepository.findById(rs.getInt("ID_CATEGORY"));

				if (category.isPresent())
					p.setCategory(category.get());

				return p;
			});
			LOG.info(String.format("Product with id: %s found successfuly %s", id, product.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No product found with id: %s", id));
		} catch (Exception e){
			String msg = String.format("Error on find product with id %s", id);
			LOG.error(msg);
			throw new DatabaseException(msg);
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
				Product product = new Product();
				product.setId(rs.getInt("ID_PRODUCT"));
				product.setName(rs.getString("NM_PRODUCT"));
				product.setValue(rs.getBigDecimal("VL_PRODUCT"));

				product.setCategory(category);
				return product;
			});
			LOG.info(String.format("Products category id: %s", category.getId()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No product found with id: %s", category.getId()));
			products = new ArrayList<>();
		} catch (Exception e){
			String msg = String.format("Error on find prodcts by category %s", category.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return products;
	}

}
