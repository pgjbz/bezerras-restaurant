package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.AddressDTO;
import com.pgbezerra.bezerras.services.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/zip")
public class AddressResource {

    private final AddressService addressService;

    public AddressResource(final AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping(value = "/{zip}")
    public ResponseEntity<AddressDTO> findByZipCode(@PathVariable(value = "zip") String zipCode){
        return ResponseEntity.ok(addressService.findByZipCode(zipCode));
    }
}
