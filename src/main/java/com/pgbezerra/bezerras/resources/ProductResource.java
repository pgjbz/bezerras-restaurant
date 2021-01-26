package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.ProductDTO;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    private final ProductService productService;

    public ProductResource(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> findById(@PathVariable(value = "id") Integer id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping(value = "/category/{id}")
    public ResponseEntity<List<Product>> findByCategory(@PathVariable(value = "id") Integer id){
        Category category = new Category(id, null);
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> insert(@RequestBody @Valid ProductDTO productDto){
        Product product = convertToEntity(productDto);
        productService.insert(product);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody @Valid ProductDTO productDto){
        Product product = convertToEntity(productDto);
        product.setId(id);
        productService.update(product);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id){
        productService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Product convertToEntity(ProductDTO productDto){
        return new Product(null, productDto.getName(), productDto.getValue(), new Category(productDto.getCategory(), null));
    }

}
