package com.netcracker.controller;


import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.service.request.RequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RequestControllerHistoryTest {

    private final long requestId = 2L;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    @Before
    public void setUp() throws Exception {
        ChangeGroup changeGroup = new ChangeGroup();
        this.mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
        MockitoAnnotations.initMocks(this);
        Set<ChangeGroup> dayChangeGroup = new HashSet<>();
        dayChangeGroup.add(new ChangeGroup(1L));

        Set<ChangeGroup> monthChangeGroup = new HashSet<>();

        monthChangeGroup.add(new ChangeGroup(3L));
        monthChangeGroup.add(new ChangeGroup(4L));
        monthChangeGroup.add(new ChangeGroup(1L));

        Set<ChangeGroup> allChangeGroup = new HashSet<>();
        when(requestService.getRequestHistory(requestId, "day")).thenReturn(dayChangeGroup);
        when(requestService.getRequestHistory(requestId, "month")).thenReturn(monthChangeGroup);
        when(requestService.getRequestHistory(requestId, "all")).thenReturn(allChangeGroup);
    }

    @Test
    public void fetchDayHistoryTest() throws Exception {
        mockMvc.perform(get("/api/request/history/{requestId}/",requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void fetchMonthHistoryTest() throws Exception {
        mockMvc.perform(get("/api/request/history/{requestId}?period=month",requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[2].id", is(4)));
    }
}
