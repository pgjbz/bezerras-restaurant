package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.dto.ProductDTO;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.resources.exception.StandardError;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.MenuService;
import com.pgbezerra.bezerras.services.ProductService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductResource.class)
@Import(BCryptConfiguration.class)
public class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private MenuService menuService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product p1;
    private Product p2;
    private Product p3;
    private Category c1;
    private Category c2;
    private List<Product> products = new ArrayList<>();

    @Before
    public void config(){
        p1 = new Product(1, "Product 1", BigDecimal.valueOf(10.0), c1);
        p2 = new Product(2, "Product 2", BigDecimal.valueOf(10.0), c2);
        p3 = new Product(3, "Product 3", BigDecimal.valueOf(10.0), c2);
        c1 = new Category(1, "Category 1");
        c1.setIsMenu(false);
        c2 = new Category(2, "Category 2");
        c2.setIsMenu(true);
        products.addAll(Arrays.asList(p1, p2, p3));
    }

    @Test
    public void insertNewProductExpectedCreated() throws Exception {
        ProductDTO objDTO = new ProductDTO("Product test", BigDecimal.valueOf(10.0), 1);
        Mockito.when(productService.insert(Mockito.any(Product.class)))
                .thenReturn(p1);
        mockMvc.perform( MockMvcRequestBuilders.post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(productService).insert(Mockito.any(Product.class));
    }

    @Test
    public void insertNewProductExpectedBadRequest() throws Exception {
        ProductDTO objDTO = new ProductDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void insertNewProductWithoutNameExpectedBadRequest() throws Exception {
        ProductDTO objDTO = new ProductDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findProductByIdExpectedNotFound() throws Exception {
        Mockito.when(productService.findById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(productService).findById(1);
    }

    @Test
    public void findProductByIdExpectedOk() throws Exception {
        Mockito.when(productService.findById(1)).thenReturn(p1);
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(p1)));
        Mockito.verify(productService).findById(1);
    }

    @Test
    public void findProductByCategoryExpectedOk() throws Exception {

        Mockito.when(productService.findByCategory(Mockito.any(Category.class))).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/category/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(products)));
    }

    @Test
    public void findProductByCategoryExpectedNotFound() throws Exception {

        Mockito.when(productService.findByCategory(Mockito.any(Category.class))).thenThrow(ResourceNotFoundException.class);
        StandardError error = new StandardError(LocalDateTime.now(), 404, "Resource not found", null, "/products/category/2");
        mockMvc.perform(MockMvcRequestBuilders.get("/products/category/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(error)));
    }

    @Test
    public void findAllProductsExpectedNotFound() throws Exception {
        Mockito.when(productService.findAll()).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(productService).findAll();
    }

    @Test
    public void findAllProductsExpectedOk() throws Exception {
        Mockito.when(productService.findAll()).thenReturn(products);
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(products)));
    }

    @Test
    public void editProductExpectedBadRequest() throws Exception {
        Mockito.when(productService.update(Mockito.any(Product.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void editProductExpectedNotFound() throws Exception {
        ProductDTO objDTO = new ProductDTO("Product edited", BigDecimal.valueOf(10.0), 1);
        Mockito.when(productService.update(Mockito.any(Product.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editProductExpectedOk() throws Exception {
        ProductDTO objDTO = new ProductDTO("Product edited", BigDecimal.valueOf(10.0), 1);
        Mockito.when(productService.update(Mockito.any(Product.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(productService).update(Mockito.any(Product.class));
    }

    @Test
    public void deleteProductExpectedResourceNotFound() throws Exception {
        Mockito.when(productService.deleteById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(productService).deleteById(1);
    }

    @Test
    public void deleteProductExpectedResourceNoContent() throws Exception {
        Mockito.when(productService.deleteById(1)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(productService).deleteById(1);
    }

}
