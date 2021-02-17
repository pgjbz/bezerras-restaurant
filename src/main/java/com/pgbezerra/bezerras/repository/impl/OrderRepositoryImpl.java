package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.models.dto.ReportDTO;
import com.pgbezerra.bezerras.models.entity.Order;
import com.pgbezerra.bezerras.models.entity.OrderAddress;
import com.pgbezerra.bezerras.models.entity.Table;
import com.pgbezerra.bezerras.repository.OrderAddressRepository;
import com.pgbezerra.bezerras.repository.OrderItemRepository;
import com.pgbezerra.bezerras.repository.OrderRepository;
import com.pgbezerra.bezerras.repository.TableRepository;
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

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

	private static final Logger LOG = Logger.getLogger(OrderRepositoryImpl.class);

	private final NamedParameterJdbcTemplate namedJdbcTemplate;
	private final OrderAddressRepository orderAddressRepository;
	private final TableRepository tableRepository;
	private final OrderItemRepository orderItemRepository;

	public OrderRepositoryImpl(
			final NamedParameterJdbcTemplate namedJdbcTemplate,
			final OrderAddressRepository orderAddressRepository, TableRepository tableRepository,
			final OrderItemRepository orderItemRepository) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.orderAddressRepository = orderAddressRepository;
		this.tableRepository = tableRepository;
		this.orderItemRepository = orderItemRepository;
	}

	@Override
	@Transactional
	public Order insert(Order order) {
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
		paramSource.addValue("date", order.getDate());
		paramSource.addValue("value", order.getValue());
		paramSource.addValue("table", Objects.nonNull(order.getTable()) ? order.getTable().getId() : null);
		paramSource.addValue("valueDelivery", order.getDeliveryValue());
		paramSource.addValue("status",
				Objects.nonNull(order.getOrderStatus()) ? order.getOrderStatus().getStatusCode() : null);
		paramSource.addValue("type",
				Objects.nonNull(order.getOrderType()) ? order.getOrderType().getOrderTypeCode() : null);
		paramSource.addValue("orderAddress",
				Objects.nonNull(order.getOrderAddress()) ? order.getOrderAddress().getId() : null);

		try {
			int rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[]{"id_order"});
			if (rowsAffected > 0) {
				order.setId(keyHolder.getKey().longValue());
				LOG.info(String.format("New row %s inserted successfuly", order.toString()));
			} else {
				LOG.error(String.format("Can't insert a new row %s", order.toString()));
				throw new DatabaseException("Can't insert a new row");
			}
		} catch (DataIntegrityViolationException e) {
			String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), order.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		} catch (Exception e){
			String msg = String.format("Error a new row %s|%s", e.getMessage(), order.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
		return order;
	}

	@Override
	@Transactional
	public Boolean update(Order order) {
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
		paramSource.addValue("date", order.getDate());
		paramSource.addValue("value", order.getValue());
		paramSource.addValue("table", Objects.nonNull(order.getTable()) ? order.getTable().getId() : null);
		paramSource.addValue("valueDelivery", order.getDeliveryValue());
		paramSource.addValue("status",
				Objects.nonNull(order.getOrderStatus()) ? order.getOrderStatus().getStatusCode() : null);
		paramSource.addValue("type",
				Objects.nonNull(order.getOrderType()) ? order.getOrderType().getOrderTypeCode() : null);
		paramSource.addValue("orderAddress",
				Objects.nonNull(order.getOrderAddress()) ? order.getOrderAddress().getId() : null);
		paramSource.addValue("id", order.getId());

		try {
			return namedJdbcTemplate.update(sql.toString(), paramSource) > 0;
		} catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Error update register with id %s %s", order.getId(), order.toString()));
			throw new DatabaseException(e.getMessage());
		} catch (Exception e){
			String msg = String.format("Error update register with id %s %s", order.getId(), order.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
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
		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (Exception e){
			String msg= String.format("Error on delete order with id %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
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
		} catch (Exception e){
			String msg = "Error on find all orders";
			LOG.error(msg, e);
			throw new DatabaseException(msg);
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
			if(Objects.nonNull(order))
				LOG.info(String.format("Order with id: %s found successfuly %s", id, order.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No order found with id: %s", id));
		} catch (Exception e) {
			String msg = String.format("Error on find order with id %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
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

	@Override
	public List<Order> findPendingOrders() {
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
		sql.append("   ID_ORDER_STATUS = 1 ");

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
		} catch (Exception e){
			String msg = "Error on find pending orders";
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return orders;
	}

	@Override
	public List<ReportDTO> report(Date initialDate, Date finalDate) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   date_trunc('year', DT_ORDER) AS DT_ORDER, ");
		sql.append("   SUM(VL_ORDER) AS VL_TOTAL, ");
		sql.append("   COUNT(ID_ORDER) AS QT_ORDERS ");
		sql.append(" FROM ");
		sql.append("   TB_ORDER ");
		sql.append(" WHERE ");
		sql.append("   DT_ORDER BETWEEN :initial AND :final ");
		sql.append("   AND ID_ORDER_STATUS = 3 ");
		sql.append(" GROUP BY ");
		sql.append("   date_trunc('year', DT_ORDER) ");


		List<ReportDTO> orders = null;
		Map<String, Object> params = new HashMap<>();
		params.put("initial", initialDate);
		params.put("final", finalDate);
		try {
			orders = namedJdbcTemplate.query(sql.toString(), params, (rs, rownum) -> {
				ReportDTO report = new ReportDTO();
				report.setDate(rs.getDate("DT_ORDER"));
				report.setAmount(rs.getBigDecimal("VL_TOTAL"));
				report.setTotalOrders(rs.getInt("QT_ORDERS"));
				return report;
			});
		} catch (EmptyResultDataAccessException e){
			orders =  new ArrayList<>();
		} catch (Exception e){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String msg = String.format("Error on find order report between %s and %s", sdf.format(initialDate), sdf.format(finalDate));
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
		LOG.info(orders.size());
		return orders;
	}
}
