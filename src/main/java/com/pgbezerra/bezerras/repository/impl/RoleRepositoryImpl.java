package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.repository.RoleRepository;
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

import java.util.*;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private static final Logger LOG = Logger.getLogger(RoleRepositoryImpl.class);

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public RoleRepositoryImpl(final NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public Role insert(Role role) {
        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO ");
        sql.append("   TB_ROLE(NM_ROLE) ");
        sql.append(" VALUES(:roleName) ");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("roleName", role.getRoleName());

        try {
            int rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
            if (rowsAffected > 0) {
                role.setId(keyHolder.getKey().intValue());
                LOG.info(String.format("New row %s inserted successfully", role.toString()));
            } else {
                LOG.error(String.format("Can't insert a new row %s", role.toString()));
                throw new DatabaseException("Can't insert a new row");
            }
        } catch (DataIntegrityViolationException e) {
            String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), role.toString());
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        } catch (Exception e) {
            String msg = String.format("Error on insert a new row %s", role.toString());
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
        return role;
    }

    @Override
    public Boolean update(Role role) {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE ");
        sql.append("   TB_ROLE ");
        sql.append(" SET ");
        sql.append("   NM_ROLE = :roleName ");
        sql.append(" WHERE ");
        sql.append("   ID_ROLE = :id ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("roleName", role.getRoleName());
        parameters.put("id", role.getId());

        try {
            return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
        } catch (DataIntegrityViolationException e) {
            LOG.error(String.format("Error update register with id %s %s", role.getId(), role.toString()));
            throw new DatabaseException(e.getMessage());
        } catch (Exception e) {
            String msg = String.format("Unexpected error on update register with id %s", role.getId());
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
    }

    @Override
    public Boolean deleteById(Integer id) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM ");
        sql.append("   TB_ROLE ");
        sql.append(" WHERE ");
        sql.append("   ID_ROLE = :id ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        try {
            return namedJdbcTemplate.update(sql.toString(), parameters) > 0;
        } catch (DataIntegrityViolationException e) {
            String msg = String.format("Can't delete role with id %s", id);
            LOG.warn(msg, e);
            throw new DatabaseException(msg);
        } catch (Exception e) {
            String msg = String.format("Error on delete role with id %s", id);
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
    }

    @Override
    public List<Role> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("   ID_ROLE, ");
        sql.append("   NM_ROLE ");
        sql.append(" FROM ");
        sql.append("   TB_ROLE ");
        List<Role> roles = null;

        try {
            return namedJdbcTemplate.query(sql.toString(), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            roles = new ArrayList<>();
        } catch (Exception e){
            String msg = "Error on find all tables";
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
        return roles;
    }

    @Override
    public Optional<Role> findById(Integer id) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("   ID_ROLE, ");
        sql.append("   NM_ROLE ");
        sql.append(" FROM ");
        sql.append("   TB_ROLE ");
        sql.append(" WHERE ");
        sql.append("   ID_ROLE = :id ");
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        Role role = null;

        try {
            role = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
            if(Objects.nonNull(role))
                LOG.info(String.format("Role with id: %s found successfuly %s", id, role.toString()));
        } catch (EmptyResultDataAccessException e) {
            LOG.warn(String.format("No role found with id: %s", id));
        } catch (Exception e){
            String msg = String.format("Error on find role with id: %s", id);
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }

        return Optional.ofNullable(role);
    }

    @Override
    public List<Role> insertAll(List<Role> list) {
        for (Role role : list)
            insert(role);
        return list;
    }

    private final RowMapper<Role> rowMapper = (rs, rowNum) -> {
        Role role = new Role();
        role.setRoleName(rs.getString("NM_ROLE"));
        role.setId(rs.getInt("ID_ROLE"));
        return role;
    };

}
