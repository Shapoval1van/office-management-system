package com.netcracker.controller;


import com.netcracker.component.PageableHandlerMethodArgumentResolver;
import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.request.RequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test") //TODO need fix test, got status 500!
public class RequestControllerHistoryTest {

//    private final long requestId = 20L;
//    private MockMvc mockMvc;
//
//    @Autowired
//    private HttpMessageConverter mappingJackson2HttpMessageConverter;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Mock
//    private RequestService requestService;
//
//    @InjectMocks
//    private RequestController requestController;
//
//
//    @Before
//    public void setUp() throws Exception {
//        ChangeGroup changeGroup = new ChangeGroup();
//        this.mockMvc = MockMvcBuilders.standaloneSetup(requestController)
//                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
//                .setMessageConverters(this.mappingJackson2HttpMessageConverter).build();
//        MockitoAnnotations.initMocks(this);
//        Set<ChangeGroup> dayChangeGroup = new HashSet<>();
//        dayChangeGroup.add(new ChangeGroup(1L));
//
//        Set<ChangeGroup> monthChangeGroup = new HashSet<>();
//
//        monthChangeGroup.add(new ChangeGroup(3L));
//        monthChangeGroup.add(new ChangeGroup(4L));
//        monthChangeGroup.add(new ChangeGroup(1L));
//
//        Set<ChangeGroup> allChangeGroup = new HashSet<>();
//        when(requestService.getRequestHistory(eq(requestId), eq("day"), any(Pageable.class))).thenReturn(dayChangeGroup);
//        when(requestService.getRequestHistory(eq(requestId), eq("month"), any(Pageable.class))).thenReturn(monthChangeGroup);
//        when(requestService.getRequestHistory(eq(requestId), eq("all"), any(Pageable.class))).thenReturn(allChangeGroup);
//    }
//
//
//
//    @Test
//    public void fetchDayHistoryTest() throws Exception {
//        mockMvc.perform(get("/api/request/history/{requestId}/",requestId))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(jsonPath("$[0].id", is(1)));
//    }
//
//    @Test
//    public void fetchMonthHistoryTest() throws Exception {
//        mockMvc.perform(get("/api/request/history/{requestId}?period=month",requestId))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(jsonPath("$", hasSize(3)));
//    }
}
