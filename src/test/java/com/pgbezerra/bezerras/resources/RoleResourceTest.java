package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.dto.RoleDTO;
import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.security.JWTUtil;
import com.pgbezerra.bezerras.services.RoleService;
import com.pgbezerra.bezerras.services.UserService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(RoleResource.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN"})
@Import({BCryptConfiguration.class, JWTUtil.class})
public class RoleResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role r1;
    private Role r2;
    private Role r3;
    private List<Role> roles = new ArrayList<>();

    @Before
    public void config(){
        r1 = new Role(1, "ROLE_ADMIN");
        r2 = new Role(2, "ROLE_EMPL");
        r3 = new Role(3, "Role 3");
        roles.addAll(Arrays.asList(r1, r2, r3));
    }

    @Test
    public void insertNewRoleExpectedCreated() throws Exception {
        RoleDTO objDTO = new RoleDTO("ROLE_ADMIN");
        Mockito.when(roleService.insert(Mockito.any(Role.class)))
                .thenReturn(roles.get(0));
        mockMvc.perform( MockMvcRequestBuilders.post("/roles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(roleService).insert(Mockito.any(Role.class));
    }

    @Test
    public void insertNewRoleExpectedBadRequest() throws Exception {
        RoleDTO objDTO = new RoleDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void insertNewRoleInvalidRoleNameExpectedBadRequest() throws Exception {
        RoleDTO objDTO = new RoleDTO("Role 3");
        mockMvc.perform( MockMvcRequestBuilders.post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findRoleByIdExpectedNotFound() throws Exception {
        Mockito.when(roleService.findById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/roles/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(roleService).findById(1);
    }

    @Test
    public void findRoleByIdExpectedOk() throws Exception {
        Mockito.when(roleService.findById(1)).thenReturn(r1);
        mockMvc.perform(MockMvcRequestBuilders.get("/roles/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(r1)));
        Mockito.verify(roleService).findById(1);
    }

    @Test
    public void findAllRolesExpectedNotFound() throws Exception {
        Mockito.when(roleService.findAll()).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/roles"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(roleService).findAll();
    }

    @Test
    public void findAllRolesExpectedOk() throws Exception {
        Mockito.when(roleService.findAll()).thenReturn(roles);
        mockMvc.perform(MockMvcRequestBuilders.get("/roles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(roles)));
    }

    @Test
    public void editRoleExpectedBadRequest() throws Exception {
        Mockito.when(roleService.update(Mockito.any(Role.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/roles/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void editRoleExpectedNotFound() throws Exception {
        RoleDTO objDTO = new RoleDTO("ROLE_ROOT");
        Mockito.when(roleService.update(Mockito.any(Role.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editRoleExpectedOk() throws Exception {
        RoleDTO objDTO = new RoleDTO("ROLE_ROOT");
        Mockito.when(roleService.update(Mockito.any(Role.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(roleService).update(Mockito.any(Role.class));
    }

    @Test
    public void deleteRoleExpectedResourceNotFound() throws Exception {
        Mockito.when(roleService.deleteById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(roleService).deleteById(1);
    }

    @Test
    public void deleteRoleExpectedResourceNoContent() throws Exception {
        Mockito.when(roleService.deleteById(1)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(roleService).deleteById(1);
    }

}
