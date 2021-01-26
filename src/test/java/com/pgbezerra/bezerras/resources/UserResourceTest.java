package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.dto.UserDTO;
import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.entities.model.User;
import com.pgbezerra.bezerras.security.JWTUtil;
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
@WebMvcTest(UserResource.class)
@WithMockUser(username = "admin", password = "admin")
@Import({BCryptConfiguration.class, JWTUtil.class})
public class UserResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User u1;
    private User u2;
    private User u3;
    private final List<User> users = new ArrayList<>();
    private Role r1;

    @Before
    public void config(){
        r1 = new Role(1, "ROLE_ADMIN");
        u1 = new User(1L, "Admin", "Admin", "admin", r1);
        u2 = new User(2L, "root", "root", "admin", r1);
        u3 = new User(3L, "empl", "empl", "empl", r1);
        users.addAll(Arrays.asList(u1, u2, u3));
    }

    @Test
    public void insertNewUserExpectedCreated() throws Exception {
        UserDTO objDTO = new UserDTO("admin", "admin", "admin", 1);
        Mockito.when(userService.insert(Mockito.any(User.class)))
                .thenReturn(users.get(0));
        mockMvc.perform( MockMvcRequestBuilders.post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(userService).insert(Mockito.any(User.class));
    }

    @Test
    public void insertNewUserExpectedBadRequest() throws Exception {
        UserDTO objDTO = new UserDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void insertNewUserWithoutNameExpectedBadRequest() throws Exception {
        UserDTO objDTO = new UserDTO("admin", null, "admin", 1);
        mockMvc.perform( MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findUserByIdExpectedNotFound() throws Exception {
        Mockito.when(userService.findById(1L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(userService).findById(1L);
    }

    @Test
    public void findUserByIdExpectedOk() throws Exception {
        Mockito.when(userService.findById(1L)).thenReturn(u1);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(u1)));
        Mockito.verify(userService).findById(1L);
    }

    @Test
    public void findAllUsersExpectedNotFound() throws Exception {
        Mockito.when(userService.findAll()).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(userService).findAll();
    }

    @Test
    public void findAllUsersExpectedOk() throws Exception {
        Mockito.when(userService.findAll()).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    public void editUserExpectedBadRequest() throws Exception {
        Mockito.when(userService.update(Mockito.any(User.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void editUserExpectedNotFound() throws Exception {
        UserDTO objDTO = new UserDTO("admin", "admin", "admin", 1);
        Mockito.when(userService.update(Mockito.any(User.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editUserExpectedOk() throws Exception {
        UserDTO objDTO = new UserDTO("admin", "admin", "admin", 1);
        Mockito.when(userService.update(Mockito.any(User.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(userService).update(Mockito.any(User.class));
    }

    @Test
    public void deleteUserExpectedResourceNotFound() throws Exception {
        Mockito.when(userService.deleteById(1L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(userService).deleteById(1L);
    }

    @Test
    public void deleteUserExpectedResourceNoContent() throws Exception {
        Mockito.when(userService.deleteById(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(userService).deleteById(1L);
    }

}
