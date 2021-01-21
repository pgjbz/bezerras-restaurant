package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.dto.AddressDTO;
import com.pgbezerra.bezerras.services.AddressService;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.regex.Pattern;

@Service
public class AddressServiceImpl implements AddressService {

    private final WebClient webClient;

    public AddressServiceImpl(@Qualifier(value = "zipcodeWebClient") final WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public AddressDTO findByZipCode(String zipCode) {
        Pattern pattern = Pattern.compile("^\\d{8}$");
        if(!pattern.matcher(zipCode).matches())
            throw new ResourceBadRequestException(String.format("Invalid zipcode %s format", zipCode));
        return webClient.get()
                .uri("/ws/{zip}/json/", zipCode)
                .retrieve()
                .bodyToMono(AddressDTO.class).block();
    }

}
