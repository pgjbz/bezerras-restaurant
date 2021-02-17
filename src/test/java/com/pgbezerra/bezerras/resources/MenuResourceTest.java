package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.models.dto.MenuDTO;
import com.pgbezerra.bezerras.models.dto.MenuResponseDTO;
import com.pgbezerra.bezerras.models.dto.ProductDTO;
import com.pgbezerra.bezerras.models.entity.Menu;
import com.pgbezerra.bezerras.security.JWTUtil;
import com.pgbezerra.bezerras.services.MenuService;
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

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@WebMvcTest(MenuResource.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN"})
@Import({BCryptConfiguration.class, JWTUtil.class})
public class MenuResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    @MockBean
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Menu m1;
    private Menu m2;
    private Menu m3;
    private final List<Menu> menus = new ArrayList<>();

    @Before
    public void config(){
        m1 = new Menu(1L, "Menu 1", DayOfWeek.MONDAY);
        m2 = new Menu(2L, "Menu 2", DayOfWeek.SATURDAY);
        m3 = new Menu(3L, "Menu 3", DayOfWeek.SUNDAY);
        menus.addAll(Arrays.asList(m1, m2, m3));
    }

    private MenuResponseDTO convertToDTO(Menu menu) {
        MenuResponseDTO menuResponseDTO = new MenuResponseDTO();
        menuResponseDTO.setDayOfWeek(menu.getDayOfWeek());
        menuResponseDTO.setName(menu.getName());
        menu.getItems().forEach(menuItem -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setCategory(menuItem.getProduct().getCategory().getId());
            productDTO.setName(menuItem.getProduct().getName());
            productDTO.setValue(menuItem.getProduct().getValue());
            menuResponseDTO.getItems().add(productDTO);
        });

        return menuResponseDTO;
    }

    @Test
    public void insertNewMenuExpectedCreated() throws Exception {
        MenuDTO objDTO = new MenuDTO("Menu test", DayOfWeek.FRIDAY);
        Mockito.when(menuService.insert(Mockito.any(Menu.class)))
                .thenReturn(new Menu(1L, "Menu test", DayOfWeek.FRIDAY));
        mockMvc.perform( MockMvcRequestBuilders.post("/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(menuService).insert(Mockito.any(Menu.class));
    }

    @Test
    public void insertNewMenuExpectedBadRequest() throws Exception {
        MenuDTO objDTO = new MenuDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void insertNewMenuWithoutNameExpectedBadRequest() throws Exception {
        MenuDTO objDTO = new MenuDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findMenuByIdExpectedNotFound() throws Exception {
        Mockito.when(menuService.findById(1L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/menus/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(menuService).findById(1L);
    }

    @Test
    public void findMenuByIdExpectedOk() throws Exception {
        Mockito.when(menuService.findById(1L)).thenReturn(m1);
        mockMvc.perform(MockMvcRequestBuilders.get("/menus/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(m1)));
        Mockito.verify(menuService).findById(1L);
    }

    @Test
    public void findAllMenusExpectedNotFound() throws Exception {
        Mockito.when(menuService.findAll()).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/menus"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(menuService).findAll();
    }

    @Test
    public void findAllMenusExpectedOk() throws Exception {
        List<MenuResponseDTO> menuResponseDTOS = new ArrayList<>(menus.stream().map(this::convertToDTO).collect(Collectors.toList()));
        Mockito.when(menuService.findAll()).thenReturn(menus);
        mockMvc.perform(MockMvcRequestBuilders.get("/menus"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(menuResponseDTOS)));
    }

    @Test
    public void editMenuExpectedBadRequest() throws Exception {
        Mockito.when(menuService.update(Mockito.any(Menu.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/menus/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void editMenuExpectedNotFound() throws Exception {
        MenuDTO objDTO = new MenuDTO("Menu edited", DayOfWeek.FRIDAY);
        Mockito.when(menuService.update(Mockito.any(Menu.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editMenuExpectedOk() throws Exception {
        MenuDTO objDTO = new MenuDTO("Menu edited", DayOfWeek.FRIDAY);
        Mockito.when(menuService.update(Mockito.any(Menu.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(menuService).update(Mockito.any(Menu.class));
    }

    @Test
    public void deleteMenuExpectedResourceNotFound() throws Exception {
        Mockito.when(menuService.deleteById(1L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/menus/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(menuService).deleteById(1L);
    }

    @Test
    public void deleteMenuExpectedResourceNoContent() throws Exception {
        Mockito.when(menuService.deleteById(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/menus/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(menuService).deleteById(1L);
    }

}
