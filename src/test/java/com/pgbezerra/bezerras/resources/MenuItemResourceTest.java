package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.dto.MenuItemDTO;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.services.MenuItemService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@RunWith(SpringRunner.class)
@WebMvcTest(MenuItemResource.class)
@Import(BCryptConfiguration.class)
public class MenuItemResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService menuItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private MenuItem mi1;
    private MenuItem mi2;
    private MenuItem mi3;
    private Menu m1;
    private Product p1;
    private Product p2;
    private Product p3;

    @Before
    public void config() {
        m1 = new Menu(1L, "MenuItem 1", DayOfWeek.FRIDAY);
        p1 = new Product(1, "Product 1", BigDecimal.valueOf(10d), new Category(1, "Category 1"));
        p2 = new Product(1, "Product 2", BigDecimal.valueOf(10d), new Category(1, "Category 1"));
        p3 = new Product(1, "Product 3", BigDecimal.valueOf(10d), new Category(1, "Category 1"));
        mi1 = new MenuItem(m1, p1);
        mi2 = new MenuItem(m1, p2);
        mi3 = new MenuItem(m1, p3);
    }

    @Test
    public void insertNewMenuItemExpectedCreated() throws Exception {
        MenuItemDTO objDTO = new MenuItemDTO(1, 1L);
        Mockito.when(menuItemService.insert(Mockito.any(MenuItem.class)))
                .thenReturn(mi1);
        mockMvc.perform(MockMvcRequestBuilders.post("/menuitems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(menuItemService).insert(Mockito.any(MenuItem.class));
    }

    @Test
    public void insertNewMenuItemExpectedBadRequest() throws Exception {
        MenuItemDTO objDTO = new MenuItemDTO();
        mockMvc.perform(MockMvcRequestBuilders.post("/menuitems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void deleteMenuItemExpectedResourceNotFound() throws Exception {
        Mockito.when(menuItemService.deleteById(m1, p1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/menuitems/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteMenuItemExpectedResourceNoContent() throws Exception {
        Mockito.when(menuItemService.deleteById(Mockito.any(Menu.class), Mockito.any(Product.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/menuitems/1/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(menuItemService).deleteById(Mockito.any(Menu.class), Mockito.any(Product.class));
    }

}
