package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.models.dto.TableDTO;
import com.pgbezerra.bezerras.models.entity.Table;
import com.pgbezerra.bezerras.security.JWTUtil;
import com.pgbezerra.bezerras.services.TableService;
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
@WebMvcTest(TableResource.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN"})
@Import({BCryptConfiguration.class, JWTUtil.class})
public class TableResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableService tableService;

    @MockBean
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Table t1;
    private Table t2;
    private Table t3;
    private List<Table> tables = new ArrayList<>();

    @Before
    public void config(){
        t1 = new Table(1, "Table 1");
        t2 = new Table(2, "Table 2");
        t3 = new Table(3, "Table 3");
        tables.addAll(Arrays.asList(t1, t2, t3));
    }

    @Test
    public void insertNewTableExpectedCreated() throws Exception {
        TableDTO objDTO = new TableDTO("Table test");
        Mockito.when(tableService.insert(Mockito.any(Table.class)))
                .thenReturn(new Table(1, "Table test"));
        mockMvc.perform( MockMvcRequestBuilders.post("/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(tableService).insert(Mockito.any(Table.class));
    }

    @Test
    public void insertNewTableExpectedBadRequest() throws Exception {
        TableDTO objDTO = new TableDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void insertNewTableWithoutNameExpectedBadRequest() throws Exception {
        TableDTO objDTO = new TableDTO();
        mockMvc.perform( MockMvcRequestBuilders.post("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findTableByIdExpectedNotFound() throws Exception {
        Mockito.when(tableService.findById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(tableService).findById(1);
    }

    @Test
    public void findTableByIdExpectedOk() throws Exception {
        Mockito.when(tableService.findById(1)).thenReturn(t1);
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(t1)));
        Mockito.verify(tableService).findById(1);
    }

    @Test
    public void findAllTablesExpectedNotFound() throws Exception {
        Mockito.when(tableService.findAll()).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/tables"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(tableService).findAll();
    }

    @Test
    public void findAllTablesExpectedOk() throws Exception {
        Mockito.when(tableService.findAll()).thenReturn(tables);
        mockMvc.perform(MockMvcRequestBuilders.get("/tables"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(tables)));
    }

    @Test
    public void editTableExpectedBadRequest() throws Exception {
        Mockito.when(tableService.update(Mockito.any(Table.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/tables/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void editTableExpectedNotFound() throws Exception {
        TableDTO objDTO = new TableDTO("Table edited");
        Mockito.when(tableService.update(Mockito.any(Table.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/tables/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editTableExpectedOk() throws Exception {
        TableDTO objDTO = new TableDTO("Table edited");
        Mockito.when(tableService.update(Mockito.any(Table.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/tables/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(tableService).update(Mockito.any(Table.class));
    }

    @Test
    public void deleteTableExpectedResourceNotFound() throws Exception {
        Mockito.when(tableService.deleteById(1)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/tables/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(tableService).deleteById(1);
    }

    @Test
    public void deleteTableExpectedResourceNoContent() throws Exception {
        Mockito.when(tableService.deleteById(1)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/tables/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(tableService).deleteById(1);
    }

}
