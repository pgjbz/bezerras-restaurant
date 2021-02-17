package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.models.dto.MenuDTO;
import com.pgbezerra.bezerras.models.dto.MenuResponseDTO;
import com.pgbezerra.bezerras.models.dto.ProductDTO;
import com.pgbezerra.bezerras.models.entity.Menu;
import com.pgbezerra.bezerras.services.MenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/menus")
public class MenuResource {

    private final MenuService menuService;

    public MenuResource(final MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @ApiOperation(value = "Find all menus", notes = "All authenticated users")
    public ResponseEntity<List<MenuResponseDTO>> findAll() {
        return ResponseEntity.ok(menuService.findAll().stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Find a menu by id", notes = "All authenticated users")
    public ResponseEntity<Menu> findById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(menuService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Create a new menu", notes = "Only admins users")
    public ResponseEntity<Void> insert(@RequestBody @Valid MenuDTO menuDTO) {
        Menu menu = convertToEntity(menuDTO);
        menuService.insert(menu);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(menu.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Update a menu", notes = "Only admins users")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @RequestBody @Valid MenuDTO menuDTO) {
        Menu menu = convertToEntity(menuDTO);
        menu.setId(id);
        menuService.update(menu);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Create a menu", notes = "Only admins users")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        menuService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Menu convertToEntity(MenuDTO menuDTO) {
        return new Menu(null, menuDTO.getName(), menuDTO.getDayOfWeek());
    }

    private MenuResponseDTO convertToDTO(Menu menu) {
        MenuResponseDTO menuResponseDTO = new MenuResponseDTO();
        menuResponseDTO.setDayOfWeek(menu.getDayOfWeek());
        menuResponseDTO.setName(menu.getName());
        menu.getItems().forEach(menuItem -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(menuItem.getProduct().getName());
            productDTO.setValue(menuItem.getProduct().getValue());
            menuResponseDTO.getItems().add(productDTO);
        });

        return menuResponseDTO;
    }


}
