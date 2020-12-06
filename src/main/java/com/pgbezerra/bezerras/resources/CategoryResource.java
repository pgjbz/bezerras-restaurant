package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.CategoryDTO;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Category>> findAll(){
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> findById(@PathVariable(value = "id") Integer id){
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody @Valid CategoryDTO objDTO){
        Category obj = convertToEntity(objDTO);
        categoryService.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody @Valid CategoryDTO objDTO){
        Category obj = convertToEntity(objDTO);
        obj.setId(id);
        categoryService.update(obj);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id){
        categoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Category convertToEntity(CategoryDTO objDTO){
        Category obj = new Category(null, objDTO.getName());
        obj.setIsMenu(objDTO.getIsMenu());
        return obj;
    }

}
