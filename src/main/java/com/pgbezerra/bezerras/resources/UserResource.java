package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.UserDTO;
import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.entities.model.User;
import com.pgbezerra.bezerras.services.UserService;
import com.pgbezerra.bezerras.services.exception.AuthorizationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

    private final UserService userService;

    public UserResource(@Qualifier("userServiceImpl") final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findById(@PathVariable(value = "id") Long id){
        User userRequest = userService.authenticated();
        if(Objects.isNull(userRequest) || !userRequest.getRole().getRoleName().equals("ROLE_ADMIN") && !userRequest.getId().equals(id))
            throw new AuthorizationException("Access denied");
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> insert(@RequestBody @Valid UserDTO userDto){
        User user = convertToEntity(userDto);
        userService.insert(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @RequestBody @Valid UserDTO userDto){
        User userRequest = userService.authenticated();
        if(Objects.isNull(userRequest) || !userRequest.getRole().getRoleName().equals("ROLE_ADMIN") && !userRequest.getId().equals(id))
            throw new AuthorizationException("Access denied");
        User user = convertToEntity(userDto);
        user.setId(id);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id){
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private User convertToEntity(UserDTO userDto){
        User user = new User();
        user.setRole(new Role(userDto.getRole(), null));
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        return user;
    }

}
