package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.models.dto.UserDTO;
import com.pgbezerra.bezerras.models.entity.Role;
import com.pgbezerra.bezerras.models.entity.User;
import com.pgbezerra.bezerras.services.UserService;
import com.pgbezerra.bezerras.services.exception.AuthorizationException;
import io.swagger.annotations.ApiOperation;
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Find all users", notes = "Only admins users")
    public ResponseEntity<List<User>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Find user by id", notes = "Only admins users can find any user by id, others users only find yourself")
    public ResponseEntity<User> findById(@PathVariable(value = "id") Long id){
        User userRequest = userService.authenticated();
        if(Objects.isNull(userRequest) || !userRequest.getRole().getRoleName().equals("ROLE_ADMIN") && !userRequest.getId().equals(id))
            throw new AuthorizationException("Access denied");
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Create a new user", notes = "Only admins users")
    public ResponseEntity<Void> insert(@RequestBody @Valid UserDTO userDto){
        User user = convertToEntity(userDto);
        userService.insert(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Update a user", notes = "Only admins users can update any user, others users only update yourself")
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
    @ApiOperation(value = "Delete a user", notes = "Only admins users")
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
