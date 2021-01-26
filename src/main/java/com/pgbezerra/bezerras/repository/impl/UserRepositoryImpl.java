package com.pgbezerra.bezerras.repository.impl;

import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.entities.model.User;
import com.pgbezerra.bezerras.repository.RoleRepository;
import com.pgbezerra.bezerras.repository.UserRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOG = Logger.getLogger(UserRepositoryImpl.class);

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final RoleRepository roleRepository;

    public UserRepositoryImpl(final NamedParameterJdbcTemplate namedJdbcTemplate, final RoleRepository roleRepository) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.roleRepository = roleRepository;
    }

    @Override
    public User insert(User user) {
        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO ");
        sql.append("   TB_USER( ");
        sql.append("     NM_USER, ");
        sql.append("     DS_USERNAME, ");
        sql.append("     DS_PASSWORD, ");
        sql.append("     ID_ROLE) ");
        sql.append(" VALUES( ");
        sql.append("   :nameuser, ");
        sql.append("   :username, ");
        sql.append("   :password, ");
        sql.append("   :idrole) ");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("nameuser", user.getName());
        paramSource.addValue("username", user.getUsername());
        paramSource.addValue("password", user.getPassword());
        paramSource.addValue("idrole", Objects.nonNull(user.getRole()) ? user.getRole().getId() : null);


        try {
            int rowsAffected = namedJdbcTemplate.update(sql.toString(), paramSource, keyHolder,  new String[]{"id_user"});
            if (rowsAffected > 0) {
                user.setId(keyHolder.getKey().longValue());
                LOG.info(String.format("New row %s inserted successfully", user.toString()));
            } else {
                LOG.error(String.format("Can't insert a new row %s", user.toString()));
                throw new DatabaseException("Can't insert a new row");
            }
        } catch (DataIntegrityViolationException e) {
            String msg = String.format("Can't insert a new row %s|%s", e.getMessage(), user.toString());
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        } catch (Exception e){
            String msg = String.format("Error on insert a new row %s|%s", e.getMessage(), user.toString());
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
        return user;
    }

    @Override
    public Boolean update(User user) {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE ");
        sql.append("   TB_USER ");
        sql.append(" SET ");
        sql.append("   NM_USER = :nameUser, ");
        sql.append("   DS_USERNAME = :username, ");
        sql.append("   DS_PASSWORD = :password, ");
        sql.append("   ID_ROLE = :roleId ");
        sql.append(" WHERE ");
        sql.append("   ID_USER = :idUser ");
        Map<String, Object> params = new HashMap<>();
        params.put("nameUser", user.getName());
        params.put("username", user.getUsername());
        params.put("password", user.getPassword());
        params.put("roleId", Objects.nonNull(user.getRole()) ? user.getRole().getId() : null);
        params.put("idUser", user.getId());
        try {
            return namedJdbcTemplate.update(sql.toString(), params) > 0;
        } catch (DataIntegrityViolationException e){
            LOG.error(String.format("Error update register with id %s %s", user.getId(), user.toString()));
            throw new DatabaseException(e.getMessage());
        } catch (Exception e){
            String msg = String.format("Error on update user with with %s", user.getId());
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
    }

    @Override
    public Boolean deleteById(Long id) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM ");
        sql.append("   TB_USER ");
        sql.append(" WHERE ");
        sql.append("   ID_USER = :id ");

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);
        try {
            return namedJdbcTemplate.update(sql.toString(), params) > 0;
        } catch (Exception e){
            String msg = String.format("Error on delete user with id %s", id);
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
    }

    @Override
    public List<User> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("   ID_USER, ");
        sql.append("   NM_USER, ");
        sql.append("   DS_USERNAME, ");
        sql.append("   ID_ROLE ");
        sql.append(" FROM ");
        sql.append("   TB_USER ");

        final Map<Integer, Role> roles = new HashMap<>();

        List<User> users = null;
        try {
            users = namedJdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getLong("ID_USER"));
                user.setName(rs.getString("NM_USER"));
                user.setUsername(rs.getString("DS_USERNAME"));
                Integer idRole = rs.getInt("ID_ROLE");
                if(roles.containsKey(idRole))
                    user.setRole(roles.get(idRole));
                else {
                    Optional<Role> role = roleRepository.findById(idRole);
                    if(role.isPresent()){
                        roles.put(idRole, role.get());
                        user.setRole(role.get());
                    }
                }
                return user;
            });
        } catch (EmptyResultDataAccessException e) {
            users = new ArrayList<>();
        } catch (Exception e){
            String msg = "Error on find all users";
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("   ID_USER, ");
        sql.append("   NM_USER, ");
        sql.append("   DS_USERNAME, ");
        sql.append("   ID_ROLE ");
        sql.append(" FROM ");
        sql.append("   TB_USER ");
        sql.append(" WHERE ");
        sql.append("   ID_USER = :id ");

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        User user = null;

        try {
            user = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getLong("ID_USER"));
                u.setName(rs.getString("NM_USER"));
                u.setUsername(rs.getString("DS_USERNAME"));
                u.setRole(roleRepository.findById(rs.getInt("ID_ROLE")).get());
                return u;
            });
            if(Objects.nonNull(user))
                LOG.info(String.format("User with id: %s found successfully %s", id, user.toString()));
        } catch (EmptyResultDataAccessException e) {
            LOG.warn(String.format("No user found with id: %s", id));
        } catch (Exception e){
            String msg = String.format("Error on find user with id: %s", id);
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> insertAll(List<User> list) {
        for(User user: list)
            insert(user);
        return list;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("   ID_USER, ");
        sql.append("   NM_USER, ");
        sql.append("   DS_USERNAME, ");
        sql.append("   ID_ROLE, ");
        sql.append("   DS_PASSWORD ");
        sql.append(" FROM ");
        sql.append("   TB_USER ");
        sql.append(" WHERE ");
        sql.append("   DS_USERNAME = :username ");

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("username", username);

        User user = null;

        try {
            user = namedJdbcTemplate.queryForObject(sql.toString(), paramSource, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getLong("ID_USER"));
                u.setName(rs.getString("NM_USER"));
                u.setUsername(rs.getString("DS_USERNAME"));
                u.setRole(roleRepository.findById(rs.getInt("ID_ROLE")).get());
                u.setPassword(rs.getString("DS_PASSWORD"));
                return u;
            });
            if(Objects.nonNull(user))
                LOG.info(String.format("User with username: %s found successfully %s", username, user.toString()));
        } catch (EmptyResultDataAccessException e) {
            LOG.warn(String.format("No user found with username: %s", username));
        } catch (Exception e){
            String msg = String.format("Error on find user with username: %s", username);
            LOG.error(msg, e);
            throw new DatabaseException(msg);
        }

        return Optional.ofNullable(user);
    }
}
