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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Order;
import com.pgbezerra.bezerras.entities.model.OrderItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.OrderItemRepository;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

	private static final Logger LOG = Logger.getLogger(OrderItemRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private ProductRepository productRepository;

	public OrderItemRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate, ProductRepository productRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.productRepository = productRepository;
	}

	@Override
	@Transactional
	public OrderItem insert(OrderItem obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append(" 	TB_ORDER_ITEM( ");
		sql.append(" 		ID_PRODUCT, ");
		sql.append(" 		ID_ORDER, ");
		sql.append(" 		QT_ORDER_ITEM, ");
		sql.append(" 		VL_ORDER_ITEM) ");
		sql.append(" VALUES( ");
		sql.append(" 	:product, ");
		sql.append(" 	:order, ");
		sql.append(" 	:quantity, ");
		sql.append(" 	:value) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("product", Objects.nonNull(obj.getProduct()) ? obj.getProduct().getId() : null);
		paramSource.addValue("order", Objects.nonNull(obj.getOrder()) ? obj.getOrder().getId() : null);
		paramSource.addValue("quantity", obj.getQuantity());
		paramSource.addValue("value", obj.getValue());
		int rowsAffected = 0;

		try {
			rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
			if (rowsAffected > 0) {
				obj.setId(keyHolder.getKey().longValue());
				LOG.info(String.format("New row %s inserted successfuly", obj.toString()));
			}  else {
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
	public Boolean update(OrderItem obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append(" 	TB_ORDER_ITEM ");
		sql.append(" SET ");
		sql.append(" 	ID_PRODUCT = :product, ");
		sql.append(" 	ID_ORDER = :order, ");
		sql.append(" 	QT_ORDER_ITEM = :quantity, ");
		sql.append(" 	VL_ORDER_ITEM = :value ");
		sql.append(" WHERE ");
		sql.append(" 	ID_ORDER_ITEM = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("product", Objects.nonNull(obj.getProduct()) ? obj.getProduct().getId() : null);
		paramSource.addValue("order", Objects.nonNull(obj.getOrder()) ? obj.getOrder().getId() : null);
		paramSource.addValue("quantity", obj.getQuantity());
		paramSource.addValue("value", obj.getValue());
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
		sql.append(" 	TB_ORDER_ITEM ");
		sql.append(" WHERE ");
		sql.append(" 	ID_ORDER_ITEM = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);

		return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NESTED)
	public List<OrderItem> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	OI.ID_ORDER_ITEM, ");
		sql.append(" 	OI.ID_PRODUCT, ");
		sql.append(" 	OI.ID_ORDER, ");
		sql.append(" 	OI.QT_ORDER_ITEM, ");
		sql.append(" 	OI.VL_ORDER_ITEM, ");
		sql.append(" 	O.ID_ORDER, ");
		sql.append(" 	O.DT_ORDER, ");
		sql.append(" 	O.VL_ORDER, ");
		sql.append(" 	O.ID_ORDER_TYPE, ");
		sql.append(" 	O.VL_DELIVERY, ");
		sql.append(" FROM ");
		sql.append(" 	TB_ORDER_ITEM OI ");
		sql.append(" 	LEFT JOIN ");
		sql.append(" 		TB_ORDER O ");
		sql.append(" 	ON O.ID_ORDER = OI.ID_ORDER ");

		final Map<Integer, Product> products = new HashMap<>();
		final Map<Long, Order> orders = new HashMap<>();

		List<OrderItem> ordersItems = null;
		try {
			return namedJdbcTemplate.query(sql.toString(), (rs, rownum) -> {
				OrderItem oi = new OrderItem();
				oi.setId(rs.getLong("ID_ORDER_ITEM"));
				oi.setQuantity(rs.getByte("QT_ORDER_ITEM"));
				oi.setValue(rs.getBigDecimal("VL_ORDER_ITEM"));

				Integer idProduct = rs.getInt("ID_PRODUCT");
				Long idOrder = rs.getLong("ID_ORDER");

				if (orders.containsKey(idOrder))
					oi.setOrder(orders.get(idOrder));
				else {
					Order order = new Order();
					order.setId(rs.getLong("ID_ORDER"));
					order.setDate(rs.getDate("DT_ORDER"));
					order.setOrderType(rs.getInt("ID_ORDER_TYPE"));
					order.setValue(rs.getBigDecimal("VL_ORDER"));
					order.setDeliveryValue(rs.getBigDecimal("VL_DELIVERY"));
					oi.setOrder(order);
					orders.put(idOrder, order);
				}

				if (products.containsKey(idProduct))
					oi.setProduct(products.get(idProduct));
				else {
					Optional<Product> product = productRepository.findById(idProduct);
					if (product.isPresent()) {
						oi.setProduct(product.get());
						products.put(idProduct, product.get());
					} else
						products.put(idProduct, null);
				}

				return oi;
			});
		} catch (EmptyResultDataAccessException e) {
			ordersItems = new ArrayList<>();
		}

		return ordersItems;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NESTED)
	public Optional<OrderItem> findById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	OI.ID_ORDER_ITEM, ");
		sql.append(" 	OI.ID_PRODUCT, ");
		sql.append(" 	OI.ID_ORDER, ");
		sql.append(" 	OI.QT_ORDER_ITEM, ");
		sql.append(" 	OI.VL_ORDER_ITEM, ");
		sql.append(" 	O.ID_ORDER, ");
		sql.append(" 	O.DT_ORDER, ");
		sql.append(" 	O.VL_ORDER, ");
		sql.append(" 	O.ID_ORDER_TYPE, ");
		sql.append(" 	O.VL_DELIVERY ");
		sql.append(" FROM ");
		sql.append(" 	TB_ORDER_ITEM OI ");
		sql.append(" 	LEFT JOIN ");
		sql.append(" 		TB_ORDER O ");
		sql.append(" 	ON O.ID_ORDER = OI.ID_ORDER ");
		sql.append(" WHERE ");
		sql.append(" 	OI.ID_ORDER_ITEM = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		OrderItem orderItem = null;
		final Map<Long, Order> orders = new HashMap<>();

		try {
			orderItem = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rownum) -> {
				OrderItem oi = new OrderItem();
				oi.setId(rs.getLong("ID_ORDER_ITEM"));
				oi.setQuantity(rs.getByte("QT_ORDER_ITEM"));
				oi.setValue(rs.getBigDecimal("VL_ORDER_ITEM"));

				Optional<Product> product = productRepository.findById(rs.getInt("ID_PRODUCT"));

				if (product.isPresent())
					oi.setProduct(product.get());

				Long idOrder = rs.getLong("ID_ORDER");
				if (orders.containsKey(idOrder))
					oi.setOrder(orders.get(idOrder));
				else {
					Order order = new Order();
					order.setId(idOrder);
					order.setDate(rs.getDate("DT_ORDER"));
					order.setOrderType(rs.getInt("ID_ORDER_TYPE"));
					order.setValue(rs.getBigDecimal("VL_ORDER"));
					order.setDeliveryValue(rs.getBigDecimal("VL_DELIVERY"));
					oi.setOrder(order);
					orders.put(idOrder, order);
				}

				return oi;
			});
			LOG.info(String.format("Order item with id: %s found successfuly %s", id, orderItem.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No order item found with id: %s", id));
		}

		return Optional.ofNullable(orderItem);
	}

	@Override
	@Transactional
	public List<OrderItem> insertAll(List<OrderItem> list) {
		for (OrderItem orderItem : list)
			insert(orderItem);
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderItem> findByOrder(Order order) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	OI.ID_ORDER_ITEM, ");
		sql.append(" 	OI.ID_PRODUCT, ");
		sql.append(" 	OI.QT_ORDER_ITEM, ");
		sql.append(" 	OI.VL_ORDER_ITEM, ");
		sql.append("	P.ID_PRODUCT, ");
		sql.append(" 	P.NM_PRODUCT, ");
		sql.append("	P.VL_PRODUCT, ");
		sql.append(" 	C.ID_CATEGORY, ");
		sql.append(" 	C.NM_CATEGORY ");
		sql.append(" FROM ");
		sql.append(" 	TB_ORDER_ITEM OI ");
		sql.append(" 	LEFT JOIN ");
		sql.append(" 		TB_PRODUCT P ");
		sql.append(" 	ON OI.ID_PRODUCT = P.ID_PRODUCT ");
		sql.append(" 	LEFT JOIN ");
		sql.append(" 		TB_CATEGORY C ");
		sql.append(" 		ON C.ID_CATEGORY = P.ID_CATEGORY");
		sql.append(" WHERE ");
		sql.append(" 	ID_ORDER = :order ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("order", order.getId());

		List<OrderItem> orderItems = null;

		final Map<Integer, Product> products = new HashMap<>();
		final Map<Integer, Category> categories = new HashMap<>();

		try {
			orderItems = namedJdbcTemplate.query(sql.toString(), paramSource, (rs, rownum) -> {
				OrderItem orderItem = new OrderItem();

				orderItem.setId(rs.getLong("ID_ORDER_ITEM"));
				orderItem.setValue(rs.getBigDecimal("VL_ORDER_ITEM"));
				orderItem.setQuantity(rs.getByte("QT_ORDER_ITEM"));

				Integer idProduct = rs.getInt("ID_PRODUCT");
				Integer idCategory = rs.getInt("ID_CATEGORY");

				if (products.containsKey(idProduct))
					orderItem.setProduct(products.get(idProduct));
				else {
					Product product = new Product();
					product.setId(rs.getInt("ID_PRODUCT"));
					product.setName(rs.getString("NM_PRODUCT"));
					product.setValue(rs.getBigDecimal("VL_PRODUCT"));
					if (categories.containsKey(idCategory))
						product.setCategory(categories.get(idCategory));
					else {
						Category category = new Category();
						category.setId(rs.getInt("ID_CATEGORY"));
						category.setName(rs.getString("NM_CATEGORY"));
						product.setCategory(category);
						categories.put(idCategory, category);
					}
					orderItem.setProduct(product);
					products.put(idProduct, product);
				}
				return orderItem;
			});
		} catch (EmptyResultDataAccessException e) {
			LOG.error(String.format("No order items founded for order: %s", order.getId()));
			orderItems = new ArrayList<>();
		}

		return orderItems;
	}

}
