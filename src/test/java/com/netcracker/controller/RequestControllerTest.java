package com.netcracker.controller;

import com.netcracker.model.dto.RequestAssignDTO;
import com.netcracker.model.dto.RequestDTO;
import com.netcracker.repository.data.interfaces.RequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/repository-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RequestControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private RequestDTO requestDTO;

    private Principal principal = Mockito.mock(Principal.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);
        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }


    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        Mockito.when(principal.getName()).thenReturn("test2@test.com");
    }

    @Test
    public void addRequestNameNotPresent() throws Exception {
        requestDTO = new RequestDTO();
        requestDTO.setName(null);
        requestDTO.setDescription(null);
        requestDTO.setManager(null);
        requestDTO.setPriority(null);

        mockMvc.perform(post("/api/request/add/")
                .content(this.json(requestDTO))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR", password = "test2", username = "test2@test.com")
    public void addRequest() throws Exception {
        requestDTO = new RequestDTO();
        requestDTO.setName("New request");
        requestDTO.setDescription("Make me tea");
        requestDTO.setManager(2L);
        requestDTO.setPriority(2);

        int size = requestRepository.findAll().size();

        mockMvc.perform(post("/api/request/add/")
                .principal(principal)
                .content(this.json(requestDTO))
                .contentType(contentType))
                .andExpect(status().isOk());

        assertEquals(requestRepository.findAll().size(), ++size);
    }

    @Test
    public void getRequest() throws Exception {
        mockMvc.perform(get("/api/request/{requestId}/", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Request 2 (closed)")))
                .andExpect(jsonPath("$.description", is("Request 2 description")));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR", password = "test2", username = "test2@test.com")
    public void successRequestAssignToSmb() throws Exception {
        RequestAssignDTO requestAssignDTO = new RequestAssignDTO();
        requestAssignDTO.setRequestId(4L);
        requestAssignDTO.setPersonId(2L);

        mockMvc.perform(post("/api/request/assign/request")
                .principal(principal)
                .content(this.json(requestAssignDTO))
                .contentType(contentType))
                .andExpect(status().isOk());

        assertEquals(2L, (long)requestRepository.findOne(4L).get().getManager().getId());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR", password = "test2", username = "test2@test.com")
    public void successRequestAssignToMe() throws Exception {
        RequestAssignDTO requestAssignDTO = new RequestAssignDTO();
        requestAssignDTO.setRequestId(4L);

        mockMvc.perform(post("/api/request/assign/request/{requestId}", 1)
                .principal(principal)
                .contentType(contentType))
                .andExpect(status().isOk());
    }

}