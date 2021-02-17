package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.models.entity.Table;
import com.pgbezerra.bezerras.repository.TableRepository;
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
public class TableRepositoryImpl implements TableRepository {

	private static final Logger LOG = Logger.getLogger(TableRepositoryImpl.class);

	private final  NamedParameterJdbcTemplate namedJdbcTemplate;

	public TableRepositoryImpl(final NamedParameterJdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = namedJdbcTemplate;
	}

	@Override
	@Transactional
	public Table insert(Table table) {

		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO ");
		sql.append("   TB_TABLE(NM_TABLE) ");
		sql.append(" VALUES ");
		sql.append("   (:name) ");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("name", table.getName());

		try {
			int rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder,  new String[]{"id_table"});
			if (rowsAffected > 0) {
				table.setId(keyHolder.getKey().intValue());
				LOG.info(String.format("New row %s inserted successfuly", table.toString()));
			} else {
				LOG.error(String.format("Can't insert a new row %s", table.toString()));
				throw new DatabaseException("Can't insert a new row");
			}
		} catch (DataIntegrityViolationException e) {
			String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), table.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		} catch(Exception e){
			String msg = String.format("Error on insert a new row %s", table.toString());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return table;
	}

	@Override
	@Transactional
	public Boolean update(Table table) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append("   TB_TABLE ");
		sql.append(" SET ");
		sql.append("   NM_TABLE = :name ");
		sql.append(" WHERE ");
		sql.append("   ID_TABLE = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", table.getName());
		parameters.put("id", table.getId());

		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Error update register with id %s %s", table.getId(), table.toString()));
			throw new DatabaseException(e.getMessage());
		} catch (Exception e){
			String msg = String.format("Unexpected error on update register with id %s", table.getId());
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
	}

	@Override
	@Transactional
	public Boolean deleteById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM ");
		sql.append("   TB_TABLE ");
		sql.append(" WHERE ");
		sql.append("   ID_TABLE = :id ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", id);
		try {
			return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
		} catch (Exception e){
			String msg = String.format("Error on delete table with id %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Table> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_TABLE, ");
		sql.append("   NM_TABLE ");
		sql.append(" FROM ");
		sql.append("   TB_TABLE ");
		List<Table> tables = null;
		try {
			return namedJdbcTemplate.query(sql.toString(), rowMapper);
		} catch (EmptyResultDataAccessException e) {
			tables = new ArrayList<>();
		} catch (Exception e){
			String msg = "Error on find all tables";
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return tables;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Table> findById(Integer id) {

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("   ID_TABLE, ");
		sql.append("   NM_TABLE ");
		sql.append(" FROM ");
		sql.append("   TB_TABLE ");
		sql.append(" WHERE ");
		sql.append("   ID_TABLE = :id ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		Table table = null;

		try {
			table = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
			if(Objects.nonNull(table))
				LOG.info(String.format("Table with id: %s found successfuly %s", id, table.toString()));
		} catch (EmptyResultDataAccessException e) {
			LOG.warn(String.format("No table found with id: %s", id));
		} catch (Exception e){
			String msg = String.format("Error on find table with id: %s", id);
			LOG.error(msg, e);
			throw new DatabaseException(msg);
		}

		return Optional.ofNullable(table);
	}

	@Override
	@Transactional
	public List<Table> insertAll(List<Table> list) {
		for (Table table : list)
			insert(table);
		return list;
	}

	private final RowMapper<Table> rowMapper = (rs, rownum) -> {

		Table table = new Table();
		table.setId(rs.getInt("ID_TABLE"));
		table.setName(rs.getString("NM_TABLE"));
		return table;

	};

}
