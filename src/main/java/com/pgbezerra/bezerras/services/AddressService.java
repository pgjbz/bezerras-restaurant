package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.models.dto.AddressDTO;

public interface AddressService {

    AddressDTO findByZipCode(String zipCode);

}
