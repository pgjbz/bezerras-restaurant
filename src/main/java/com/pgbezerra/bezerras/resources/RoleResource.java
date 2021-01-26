package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.RoleDTO;
import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/roles")
public class RoleResource {

    private final RoleService roleService;

    public RoleResource(final RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Role>> findAll(){
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Role> findById(@PathVariable(value = "id") Integer id){
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> insert(@RequestBody @Valid RoleDTO roleDto){
        Role role = convertToEntity(roleDto);
        roleService.insert(role);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(role.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody @Valid RoleDTO roleDto){
        Role role = convertToEntity(roleDto);
        role.setId(id);
        roleService.update(role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id){
        roleService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Role convertToEntity(RoleDTO roleDto){
        return new Role(null, roleDto.getRoleName());
    }

}
