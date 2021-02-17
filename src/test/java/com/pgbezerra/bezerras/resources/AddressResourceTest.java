package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.models.dto.AddressDTO;
import com.pgbezerra.bezerras.security.JWTUtil;
import com.pgbezerra.bezerras.services.AddressService;
import com.pgbezerra.bezerras.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(AddressResource.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN"})
@Import({BCryptConfiguration.class, JWTUtil.class})
public class AddressResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Test
    public void findCategoryByIdExpectedOk() throws Exception {
        AddressDTO addressDTO = new AddressDTO();
        Mockito.when(addressService.findByZipCode("01001000")).thenReturn(addressDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/zip/01001000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(addressDTO)));
        Mockito.verify(addressService).findByZipCode("01001000");
    }
}
