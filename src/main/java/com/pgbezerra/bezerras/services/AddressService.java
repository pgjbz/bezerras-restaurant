package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.dto.AddressDTO;

public interface AddressService {

    AddressDTO findByZipCode(String zipCode);

}
