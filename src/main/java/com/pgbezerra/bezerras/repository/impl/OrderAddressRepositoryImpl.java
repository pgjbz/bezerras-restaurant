package com.pgbezerra.bezerras.repository.impl;

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
import org.springframework.transaction.annotation.Transactional;

import com.pgbezerra.bezerras.entities.model.OrderAddress;
import com.pgbezerra.bezerras.repository.OrderAddressRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@Repository
public class OrderAddressRepositoryImpl implements OrderAddressRepository {

	private static final Logger LOG = Logger.getLogger(OrderAddressRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedJdbcTemplate;

	public OrderAddressRepositoryImpl(NamedParameterJdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = namedJdbcTemplate;
	}

	@Override
	@Transactional
	public OrderAddress insert(OrderAddress obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ");
		sql.append(" 	TB_ORDER_ADDRESS( ");
		sql.append(" 		NM_CLIENT, ");
		sql.append(" 		NM_STREET, ");
		sql.append(" 		NR_NUMBER, ");
		sql.append(" 		DS_COMPLEMENT, ");
		sql.append(" 		NM_DISTRICT, ");
		sql.append(" 		NM_CITY, ");
		sql.append(" 		NM_STATE) ");
		sql.append(" VALUES ");
		sql.append(" 	(:street, ");
		sql.append(" 	:client, ");
		sql.append(" 	:complement, ");
		sql.append(" 	:number, ");
		sql.append(" 	:district, ");
		sql.append(" 	:city, ");
		sql.append(" 	:state) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("client", obj.getClientName());
		paramSource.addValue("street", obj.getStreet());
		paramSource.addValue("complement", obj.getComplement());
		paramSource.addValue("number", obj.getNumber());
		paramSource.addValue("district", obj.getDistrict());
		paramSource.addValue("city", obj.getCity());
		paramSource.addValue("state", obj.getState());

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
	@Transactional
	public Boolean update(OrderAddress obj) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append(" 	TB_ORDER_ADDRESS ");
		sql.append(" SET ");
		sql.append(" 	NM_STREET = :street, ");
		sql.append(" 	NR_NUMBER = :number, ");
		sql.append(" 	DS_COMPLEMENT = :complement, ");
		sql.append(" 	NM_DISTRICT = :district, ");
		sql.append(" 	NM_CITY = :city, ");
		sql.append(" 	NM_STATE =  :state");
		sql.append(" WHERE ");
		sql.append(" 	ID_ORDER_ADDRESS = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("street", obj.getStreet());
		paramSource.addValue("complement", obj.getComplement());
		paramSource.addValue("number", obj.getNumber());
		paramSource.addValue("district", obj.getDistrict());
		paramSource.addValue("city", obj.getCity());
		paramSource.addValue("state", obj.getState());
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
		sql.append(" 	TB_ORDER_ADDRESS ");
		sql.append(" WHERE ");
		sql.append("  ID_ORDER_ADDRESS = :id ");
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);

		return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderAddress> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_ORDER_ADDRESS, ");
		sql.append(" 	NM_CLIENT, ");
		sql.append(" 	NM_STREET, ");
		sql.append(" 	NR_NUMBER, ");
		sql.append(" 	DS_COMPLEMENT, ");
		sql.append(" 	NM_DISTRICT, ");
		sql.append(" 	NM_CITY, ");
		sql.append(" 	NM_STATE ");
		sql.append(" FROM ");
		sql.append(" 	TB_ORDER_ADDRESS ");

		List<OrderAddress> orderAddresses = null;
		try {
			return namedJdbcTemplate.query(sql.toString(), rowMapper);
		} catch (EmptyResultDataAccessException e) {
			orderAddresses = new ArrayList<>();
		}

		return orderAddresses;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<OrderAddress> findById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ID_ORDER_ADDRESS, ");
		sql.append(" 	NM_CLIENT, ");
		sql.append(" 	NM_STREET, ");
		sql.append(" 	NR_NUMBER, ");
		sql.append(" 	DS_COMPLEMENT, ");
		sql.append(" 	NM_DISTRICT, ");
		sql.append(" 	NM_CITY, ");
		sql.append(" 	NM_STATE ");
		sql.append(" FROM ");
		sql.append(" 	TB_ORDER_ADDRESS ");
		sql.append(" WHERE ");
		sql.append(" 	ID_ORDER_ADDRESS = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		OrderAddress orderAddress = null;

		try {
			orderAddress = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
			LOG.info(String.format("Order address with id: %s found successfuly %s", id, orderAddress.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No order address found with id: %s", id));
		}

		return Optional.ofNullable(orderAddress);
	}

	@Override
	@Transactional
	public List<OrderAddress> insertAll(List<OrderAddress> list) {
		for (OrderAddress orderAddress : list)
			insert(orderAddress);
		return list;
	}

	private RowMapper<OrderAddress> rowMapper = (rs, rownum) -> {

		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setId(rs.getLong("ID_ORDER_ADDRESS"));
		orderAddress.setClientName(rs.getString("NM_CLIENT"));
		orderAddress.setStreet(rs.getString("NM_STREET"));
		orderAddress.setNumber(rs.getString("NR_NUMBER"));
		orderAddress.setComplement(rs.getString("DS_COMPLEMENT"));
		orderAddress.setDistrict(rs.getString("NM_DISTRICT"));
		orderAddress.setCity(rs.getString("NM_CITY"));
		orderAddress.setState(rs.getString("NM_STATE"));
		return orderAddress;

	};

}
