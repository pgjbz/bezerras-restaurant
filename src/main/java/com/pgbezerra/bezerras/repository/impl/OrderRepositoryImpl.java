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

import com.pgbezerra.bezerras.entities.model.Order;
import com.pgbezerra.bezerras.entities.model.OrderAddress;
import com.pgbezerra.bezerras.entities.model.Table;
import com.pgbezerra.bezerras.repository.OrderAddressRepository;
import com.pgbezerra.bezerras.repository.OrderItemRepository;
import com.pgbezerra.bezerras.repository.OrderRepository;
import com.pgbezerra.bezerras.repository.TableRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

	private static final Logger LOG = Logger.getLogger(OrderRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private OrderAddressRepository orderAddressRepository;
	private TableRepository tableRepository;
	private OrderItemRepository orderItemRepository;

	public OrderRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate,
			OrderAddressRepository orderAddressRepository, TableRepository tableRepository,
			OrderItemRepository orderItemRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.orderAddressRepository = orderAddressRepository;
		this.tableRepository = tableRepository;
		this.orderItemRepository = orderItemRepository;
	}

	@Override
	@Transactional
	public Order insert(Order obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append("   TB_ORDER( ");
		sql.append("     DT_ORDER, ");
		sql.append("     VL_ORDER, ");
		sql.append("     ID_TABLE, ");
		sql.append("     VL_DELIVERY, ");
		sql.append("     ID_ORDER_STATUS, ");
		sql.append("     ID_ORDER_TYPE, ");
		sql.append("     ID_ORDER_ADDRESS) ");
		sql.append(" VALUES ");
		sql.append("   (:date, ");
		sql.append("   :value, ");
		sql.append("   :table, ");
		sql.append("   :valueDelivery, ");
		sql.append("   :status, ");
		sql.append("   :type, ");
		sql.append("   :orderAddress) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("date", obj.getDate());
		paramSource.addValue("value", obj.getValue());
		paramSource.addValue("table", Objects.nonNull(obj.getTable()) ? obj.getTable().getId() : null);
		paramSource.addValue("valueDelivery", obj.getDeliveryValue());
		paramSource.addValue("status",
				Objects.nonNull(obj.getOrderStatus()) ? obj.getOrderStatus().getStatusCode() : null);
		paramSource.addValue("type",
				Objects.nonNull(obj.getOrderType()) ? obj.getOrderType().getOrderTypeCode() : null);
		paramSource.addValue("orderAddress",
				Objects.nonNull(obj.getOrderAddress()) ? obj.getOrderAddress().getId() : null);
		int rowsAffected = 0;

		try {
			rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
			if (rowsAffected > 0) {
				obj.setId(keyHolder.getKey().longValue());
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
	public Boolean update(Order obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append("   TB_ORDER ");
		sql.append(" SET ");
		sql.append("   DT_ORDER = :date, ");
		sql.append("   VL_ORDER = :value, ");
		sql.append("   ID_TABLE = :table, ");
		sql.append("   VL_DELIVERY = :valueDelivery, ");
		sql.append("   ID_ORDER_STATUS = :status, ");
		sql.append("   ID_ORDER_TYPE = :type, ");
		sql.append("   ID_ORDER_ADDRESS = :orderAddress ");
		sql.append(" WHERE ");
		sql.append("   ID_ORDER = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("date", obj.getDate());
		paramSource.addValue("value", obj.getValue());
		paramSource.addValue("table", Objects.nonNull(obj.getTable()) ? obj.getTable().getId() : null);
		paramSource.addValue("valueDelivery", obj.getDeliveryValue());
		paramSource.addValue("status",
				Objects.nonNull(obj.getOrderStatus()) ? obj.getOrderStatus().getStatusCode() : null);
		paramSource.addValue("type",
				Objects.nonNull(obj.getOrderType()) ? obj.getOrderType().getOrderTypeCode() : null);
		paramSource.addValue("orderAddress",
				Objects.nonNull(obj.getOrderAddress()) ? obj.getOrderAddress().getId() : null);
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
		sql.append("   TB_ORDER ");
		sql.append(" WHERE ");
		sql.append("   ID_ORDER = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);

		return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NESTED)
	public List<Order> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_ORDER, ");
		sql.append("   DT_ORDER, ");
		sql.append("   VL_ORDER, ");
		sql.append("   ID_TABLE, ");
		sql.append("   VL_DELIVERY, ");
		sql.append("   ID_ORDER_STATUS, ");
		sql.append("   ID_ORDER_TYPE, ");
		sql.append("   ID_ORDER_ADDRESS ");
		sql.append(" FROM ");
		sql.append("   TB_ORDER ");
		List<Order> orders = null;

		final HashMap<Long, OrderAddress> orderAddresses = new HashMap<>();
		final HashMap<Integer, Table> tables = new HashMap<>();

		try {
			orders = namedJdbcTemplate.query(sql.toString(), (rs, rownum) -> {
				Order order = new Order();
				order.setId(rs.getLong("ID_ORDER"));
				order.setDate(rs.getDate("DT_ORDER"));
				order.setValue(rs.getBigDecimal("VL_ORDER"));
				order.setDeliveryValue(rs.getBigDecimal("VL_DELIVERY"));
				order.setOrderStatus(rs.getInt("ID_ORDER_STATUS"));
				order.setOrderType(rs.getInt("ID_ORDER_TYPE"));

				Integer idTable = rs.getInt("ID_TABLE");
				Long idOrderAddress = rs.getLong("ID_ORDER_ADDRESS");

				if (tables.containsKey(idTable))
					order.setTable(tables.get(idTable));
				else {

					Optional<Table> table = tableRepository.findById(idTable);
					if (table.isPresent()) {
						order.setTable(table.get());
						tables.put(idTable, table.get());
					} else
						tables.put(idTable, null);

				}

				if (orderAddresses.containsKey(idOrderAddress))
					order.setOrderAddress(orderAddresses.get(idOrderAddress));
				else {
					Optional<OrderAddress> orderAddress = orderAddressRepository.findById(idOrderAddress);
					if (orderAddress.isPresent()) {
						order.setOrderAddress(orderAddress.get());
						orderAddresses.put(idOrderAddress, orderAddress.get());
					} else
						orderAddresses.put(idOrderAddress, null);
				}

				order.getItems().addAll(orderItemRepository.findByOrder(order));

				return order;
			});
		} catch (EmptyResultDataAccessException e) {
			orders = new ArrayList<>();
		}

		return orders;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NESTED)
	public Optional<Order> findById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_ORDER, ");
		sql.append("   DT_ORDER, ");
		sql.append("   VL_ORDER, ");
		sql.append("   ID_TABLE, ");
		sql.append("   VL_DELIVERY, ");
		sql.append("   ID_ORDER_STATUS, ");
		sql.append("   ID_ORDER_TYPE, ");
		sql.append("   ID_ORDER_ADDRESS ");
		sql.append(" FROM ");
		sql.append("   TB_ORDER ");
		sql.append(" WHERE ");
		sql.append("   ID_ORDER = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		Order order = null;

		try {
			order = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rownum) -> {
				Order o = new Order();
				o.setId(rs.getLong("ID_ORDER"));
				o.setDate(rs.getDate("DT_ORDER"));
				o.setValue(rs.getBigDecimal("VL_ORDER"));
				o.setDeliveryValue(rs.getBigDecimal("VL_DELIVERY"));
				o.setOrderStatus(rs.getInt("ID_ORDER_STATUS"));
				o.setOrderType(rs.getInt("ID_ORDER_TYPE"));

				Integer idTable = rs.getInt("ID_TABLE");
				Long idOrderAddress = rs.getLong("ID_ORDER_ADDRESS");

				Optional<Table> table = tableRepository.findById(idTable);

				if (table.isPresent())
					o.setTable(table.get());
				Optional<OrderAddress> orderAddress = orderAddressRepository.findById(idOrderAddress);
				if (orderAddress.isPresent())
					o.setOrderAddress(orderAddress.get());

				o.getItems().addAll(orderItemRepository.findByOrder(o));

				return o;
			});
			LOG.info(String.format("Order with id: %s found successfuly %s", id, order.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No order found with id: %s", id));
		}

		return Optional.ofNullable(order);
	}

	@Override
	@Transactional
	public List<Order> insertAll(List<Order> list) {
		for (Order order : list)
			insert(order);
		return list;
	}

}
