package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.MenuItemDTO;
import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.services.MenuItemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/menuitems")
public class MenuItemResource {

    private final MenuItemService menuService;

    public MenuItemResource(final MenuItemService menuService) {
        this.menuService = menuService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Create a new menu item", notes = "Only admins users")
    public ResponseEntity<Void> insert(@RequestBody @Valid MenuItemDTO menuDTO) {
        MenuItem menu = convertToEntity(menuDTO);
        menuService.insert(menu);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{productId}/{menuId}")
                .buildAndExpand(menu.getProduct(), menu.getMenu()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(value = "/{productId}/{menuId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Delete a menu item", notes = "Only admins users")
    public ResponseEntity<Void> deleteById(@PathVariable("productId") Integer productId, @PathVariable("menuId") Long menuId) {
        Menu menu = new Menu(menuId, null, null);
        Product product = new Product(productId, null, null, null);
        menuService.deleteById(menu, product);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private MenuItem convertToEntity(MenuItemDTO menuDTO) {
        return new MenuItem(new Menu(menuDTO.getMenu(), null, null),
                new Product(menuDTO.getProduct(), null, null, null));
    }


}
