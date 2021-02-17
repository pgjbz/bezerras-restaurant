package com.pgbezerra.bezerras.resources;


import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pgbezerra.bezerras.models.dto.CredentialsDTO;

@RestController
public class Login {

    @PostMapping("/login")
    @ApiOperation(value = "Authentication endpoint")
    public ResponseEntity<Void> login(@RequestBody CredentialsDTO credentials){
        return ResponseEntity.ok().build();
    }
}
