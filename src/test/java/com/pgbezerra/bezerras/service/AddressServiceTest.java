package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.services.AddressService;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import com.pgbezerra.bezerras.services.impl.AddressServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
public class AddressServiceTest {

    @TestConfiguration
    static class AddressServiceTestConfigurarion {
        @Bean
        public AddressService addressService(WebClient webClient) {
            return new AddressServiceImpl(webClient);
        }
    }

    @Autowired
    private AddressService addressService;

    @MockBean
    private WebClient webClient;

    @Test(expected = ResourceBadRequestException.class)
    public void findAddressWithInvalidZipCodeExpectedError(){
        addressService.findByZipCode("114421001");
    }
}
