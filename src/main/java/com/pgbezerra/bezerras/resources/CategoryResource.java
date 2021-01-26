package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.CategoryDTO;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.services.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    private final CategoryService categoryService;

    public CategoryResource(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ApiOperation(value = "Find all categories", notes = "All authenticated users")
    public ResponseEntity<List<Category>> findAll(){
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Find a category by id", notes = "All authenticated users")
    public ResponseEntity<Category> findById(@PathVariable(value = "id") Integer id){
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Create a new category", notes = "Only admins users")
    public ResponseEntity<Void> insert(@RequestBody @Valid CategoryDTO categoryDTO){
        Category category = convertToEntity(categoryDTO);
        categoryService.insert(category);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(category.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Update a category", notes = "Only admins users")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody @Valid CategoryDTO categoryDTO){
        Category category = convertToEntity(categoryDTO);
        category.setId(id);
        categoryService.update(category);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Delete a category", notes = "Only admins users")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id){
        categoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Category convertToEntity(CategoryDTO categoryDTO){
        Category category = new Category(null, categoryDTO.getName());
        category.setIsMenu(categoryDTO.getIsMenu());
        return category;
    }

}
