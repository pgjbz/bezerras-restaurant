package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.TableDTO;
import com.pgbezerra.bezerras.entities.model.Table;
import com.pgbezerra.bezerras.services.TableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Table>> findAll(){
        return ResponseEntity.ok(tableService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Table> findById(@PathVariable(value = "id") Integer id){
        return ResponseEntity.ok(tableService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody @Valid TableDTO tableDto){
        Table table = convertToEntity(tableDto);
        tableService.insert(table);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(table.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Integer id, @RequestBody @Valid TableDTO tableDto){
        Table table = convertToEntity(tableDto);
        table.setId(id);
        tableService.update(table);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id){
        tableService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Table convertToEntity(TableDTO tableDto){
        return new Table(null, tableDto.getName());
    }

}
