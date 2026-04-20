package com.smartcampus;

import com.smartcampus.repository.EventRepository;
import com.smartcampus.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class SmartCampusApplicationTests {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void filteredEventsAndStatisticsLoadFromContext() {
        var events = eventService.getFilteredEvents("Computer Science", "Workshop", null);
        var statistics = eventService.getEventStatistics(events);

        assertThat(events).isNotEmpty();
        assertThat(statistics.totalEvents()).isGreaterThan(0);
        assertThat(statistics.totalRegistrations()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void homePageRendersUpcomingEvents() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Student Event Feed")));
    }

    @Test
    void eventDetailsPageRendersForSeededEvent() throws Exception {
        Long eventId = eventRepository.findAll().stream()
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(view().name("event-details"));
    }

    @Test
    void restApiReturnsUpcomingEventsAndStatistics() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/api/events/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalRegistrations").value(greaterThanOrEqualTo(0)));
    }

    @Test
    void adminDashboardRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
