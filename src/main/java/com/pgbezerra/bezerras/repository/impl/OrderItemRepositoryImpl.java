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

import com.pgbezerra.bezerras.entities.model.Order;
import com.pgbezerra.bezerras.entities.model.OrderItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.OrderItemRepository;
import com.pgbezerra.bezerras.repository.OrderRepository;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

	private static final Logger LOG = Logger.getLogger(OrderItemRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private ProductRepository productRepository;
	private OrderRepository orderRepository;

	public OrderItemRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate, ProductRepository productRepository,
			OrderRepository orderRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
	}

	@Override
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
	public List<OrderItem> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_ORDER_ITEM, ");
		sql.append(" 	ID_PRODUCT, ");
		sql.append(" 	ID_ORDER, ");
		sql.append(" 	QT_ORDER_ITEM, ");
		sql.append(" 	VL_ORDER_ITEM ");
		sql.append(" FROM ");
		sql.append(" 	TB_ORDER_ITEM ");
		
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
				
				if(products.containsKey(idProduct))
					oi.setProduct(products.get(idProduct));
				else {
					Optional<Product> product = productRepository.findById(idProduct);
					if(product.isPresent()) {
						oi.setProduct(product.get());
						products.put(idProduct, product.get());
					}
				}
				
				Long idOrder = rs.getLong("ID_ORDER");
				
				if(orders.containsKey(idOrder)) 
					oi.setOrder(orders.get(idOrder));
				else {
					Optional<Order> order = orderRepository.findById(idOrder);
					
					if(order.isPresent()) {
						oi.setOrder(order.get());
						orders.put(idOrder, order.get());
					}
				}
				
				return oi;
			});
		} catch (EmptyResultDataAccessException e) {
			ordersItems = new ArrayList<>();
		}

		return ordersItems;
	}

	@Override
	public Optional<OrderItem> findById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_ORDER_ITEM, ");
		sql.append(" 	ID_PRODUCT, ");
		sql.append(" 	ID_ORDER, ");
		sql.append(" 	QT_ORDER_ITEM, ");
		sql.append(" 	VL_ORDER_ITEM ");
		sql.append(" FROM ");
		sql.append(" 	TB_ORDER_ITEM ");
		sql.append(" WHERE ");
		sql.append(" 	ID_ORDER_ITEM = :id ");
		
		
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		OrderItem orderItem = null;

		try {
			orderItem = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rownum) -> {
				OrderItem oi = new OrderItem();
				oi.setId(rs.getLong("ID_ORDER_ITEM"));
				oi.setQuantity(rs.getByte("QT_ORDER_ITEM"));
				oi.setValue(rs.getBigDecimal("VL_ORDER_ITEM"));
				
				Optional<Product> product = productRepository.findById(rs.getInt("ID_PRODUCT"));
				Optional<Order> order = orderRepository.findById(rs.getLong("ID_ORDER"));
				
				if(product.isPresent())
					oi.setProduct(product.get());
				
				if(order.isPresent())
					oi.setOrder(order.get());
				
				return oi;
			});
			LOG.info(String.format("Order item with id: %s found successfuly %s", id, orderItem.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No order item found with id: %s", id));
		}

		return Optional.ofNullable(orderItem);
	}

	@Override
	public List<OrderItem> insertAll(List<OrderItem> list) {
		for(OrderItem orderItem: list)
			insert(orderItem);
		return list;
	}

}
