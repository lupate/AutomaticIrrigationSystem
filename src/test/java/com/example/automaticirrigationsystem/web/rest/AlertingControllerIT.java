package com.example.automaticirrigationsystem.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AlertingController} REST controller.
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AlertingControllerIT {

  private static final String ENTITY_API_URL = "/api/alert";

  private static final String ENTITY_API_URL_OFF = "/api/alert/off";
  private static final String ENTITY_API_URL_OFF_ID = ENTITY_API_URL_OFF + "/{id}";

  @Autowired
  private MockMvc restAlertingMockMvc;

  @AfterAll
  static void done() {
    log.info("Integration Tests Executed");
  }

  @BeforeEach
  public void initTest() {
    log.info("Integration Tests Started");
  }

  @Test
  @Transactional
  @DisplayName("Successfully show up all plots with alert")
  void showUpPlotsWithAlert() throws Exception {

    restAlertingMockMvc
        .perform(get(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].id").value("3"))
        .andExpect(jsonPath("$[0].hasAlert").value("true"))
        .andReturn();
  }

  @Test
  @Transactional
  @DisplayName("Successfully fix a plot alert set to false")
  void fixPlotAlert() throws Exception {

    restAlertingMockMvc
        .perform(get(ENTITY_API_URL_OFF_ID, 3L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.hasAlert").value("false"));
  }

}
