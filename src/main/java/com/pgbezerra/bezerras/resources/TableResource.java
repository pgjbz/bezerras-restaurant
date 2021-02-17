package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.models.dto.TableDTO;
import com.pgbezerra.bezerras.models.entity.Table;
import com.pgbezerra.bezerras.services.TableService;
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
@RequestMapping(value = "/tables")
public class TableResource {

    private final TableService tableService;

    public TableResource(final TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    @ApiOperation(value = "Find all tables", notes = "All Authenticated users")
    public ResponseEntity<List<Table>> findAll(){
        return ResponseEntity.ok(tableService.findAll());
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Find table by id", notes = "All authenticated users")
    public ResponseEntity<Table> findById(@PathVariable(value = "id") Integer id){
        return ResponseEntity.ok(tableService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Create a new table", notes = "Only admins users")
    public ResponseEntity<Void> insert(@RequestBody @Valid TableDTO tableDto){
        Table table = convertToEntity(tableDto);
        tableService.insert(table);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(table.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Update a table", notes = "Only admins users")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody @Valid TableDTO tableDto){
        Table table = convertToEntity(tableDto);
        table.setId(id);
        tableService.update(table);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "Delete a table", notes = "Only admins users")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id){
        tableService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Table convertToEntity(TableDTO tableDto){
        return new Table(null, tableDto.getName());
    }

}
