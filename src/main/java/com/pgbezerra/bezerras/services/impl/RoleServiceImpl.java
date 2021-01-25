package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.repository.RoleRepository;
import com.pgbezerra.bezerras.services.RoleService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger LOG = Logger.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role insert(Role role) {
        role.setId(null);
        return roleRepository.insert(role);
    }

    @Override
    public Boolean update(Role role) {
        Role oldRole = findById(role.getId());
        updateData(oldRole, role);
        Boolean updated = roleRepository.update(role);
        LOG.info(String.format("Role %s updated: %s", role, updated));
        return updated;
    }

    private void updateData(Role oldRole, Role role) {
        oldRole.setRoleName(role.getRoleName());
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = roleRepository.findAll();
        LOG.info(String.format("%s tables found", roles.size()));
        if(!roles.isEmpty())
            return roles;
        throw new ResourceNotFoundException("No tables found");
    }

    @Override
    public Role findById(Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        LOG.info(String.format("Role with id %s found: %s", id, role.isPresent()));
        if(role.isPresent())
            return role.get();
        throw new ResourceNotFoundException(String.format("No roles found with id: %s", id));
    }

    @Override
    public Boolean deleteById(Integer id) {
        findById(id);
        Boolean deleted = roleRepository.deleteById(id);
        LOG.info(String.format("Role %s deleted: %s", id, deleted));
        return deleted;
    }
}
