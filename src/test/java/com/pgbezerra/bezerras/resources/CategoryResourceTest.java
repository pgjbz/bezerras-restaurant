package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.dto.CategoryDTO;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.security.JWTUtil;
import com.pgbezerra.bezerras.services.CategoryService;
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
@WebMvcTest(CategoryResource.class)
@WithMockUser(username = "admin", password = "admin")
@Import({BCryptConfiguration.class, JWTUtil.class})
public class CategoryResourceTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category c1;
    private Category c2;
    private Category c3;
    private List<Category> categories = new ArrayList<>();

    @Before
    public void config(){
        c1 = new Category(1, "Category 1");
        c1.setIsMenu(false);
        c2 = new Category(2, "Category 2");
        c2.setIsMenu(true);
        c3 = new Category(3, "Category 3");
        c3.setIsMenu(false);
        categories.addAll(Arrays.asList(c1, c2, c3));
    }

    @Test
    public void insertNewCategoryExpectedCreated() throws Exception {
        CategoryDTO objDTO = new CategoryDTO("Category test", false);
        Mockito.when(categoryService.insert(Mockito.any(Category.class)))
                .thenReturn(new Category(1, "Category test"));
        mockMvc.perform( MockMvcRequestBuilders.post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(categoryService).insert(Mockito.any(Category.class));
    }

    @Test
    public void insertNewCategoryExpectedBadRequest() throws Exception {
        CategoryDTO objDTO = new CategoryDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void insertNewCategoryWithoutNameExpectedBadRequest() throws Exception {
        CategoryDTO objDTO = new CategoryDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findCategoryByIdExpectedNotFound() throws Exception {
        Mockito.when(categoryService.findById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(categoryService).findById(1);
    }

    @Test
    public void findCategoryByIdExpectedOk() throws Exception {
        Mockito.when(categoryService.findById(1)).thenReturn(c1);
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(c1)));
        Mockito.verify(categoryService).findById(1);
    }

    @Test
    public void findAllCategoriesExpectedNotFound() throws Exception {
        Mockito.when(categoryService.findAll()).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(categoryService).findAll();
    }

    @Test
    public void findAllCategoriesExpectedOk() throws Exception {
        Mockito.when(categoryService.findAll()).thenReturn(categories);
        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(categories)));
    }

    @Test
    public void editCategoryExpectedBadRequest() throws Exception {
        Mockito.when(categoryService.update(Mockito.any(Category.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void editCategoryExpectedNotFound() throws Exception {
        CategoryDTO objDTO = new CategoryDTO("Category edited", true);
        Mockito.when(categoryService.update(Mockito.any(Category.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editCategoryExpectedOk() throws Exception {
        CategoryDTO objDTO = new CategoryDTO("Category edited", true);
        Mockito.when(categoryService.update(Mockito.any(Category.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(categoryService).update(Mockito.any(Category.class));
    }

    @Test
    public void deleteCategoryExpectedResourceNotFound() throws Exception {
        Mockito.when(categoryService.deleteById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(categoryService).deleteById(1);
    }

    @Test
    public void deleteCategoryExpectedResourceNoContent() throws Exception {
        Mockito.when(categoryService.deleteById(1)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(categoryService).deleteById(1);
    }

}
