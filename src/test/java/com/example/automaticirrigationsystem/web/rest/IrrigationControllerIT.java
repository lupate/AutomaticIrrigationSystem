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
 * Integration tests for the {@link IrrigationController} REST controller.
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class IrrigationControllerIT {

  private static final String ENTITY_API_URL_START = "/api/irrigate/start";
  private static final String ENTITY_API_URL_START_ID = ENTITY_API_URL_START + "/{id}";

  private static final String ENTITY_API_URL_END = "/api/irrigate/end";
  private static final String ENTITY_API_URL_END_ID = ENTITY_API_URL_END + "/{id}";

  private static final String ENTITY_API_URL_PLOT = "/api/plots";
  private static final String ENTITY_API_URL_PLOT_ID = ENTITY_API_URL_PLOT + "/{id}";

  @Autowired
  private MockMvc restIrrigateMockMvc;

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
  @DisplayName("Successfully irrigate a plot while sensor is up")
  void startPlotIrrigation_sensorUP() throws Exception {

    restIrrigateMockMvc
        .perform(get(ENTITY_API_URL_START_ID, 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.isIrrigated").value("true"));
  }

  @Test
  @Transactional
  @DisplayName("throw NotFoundException when irrigate invalid plot")
  void startPlotIrrigation_InvalidPlot() throws Exception {

    restIrrigateMockMvc
        .perform(get(ENTITY_API_URL_START_ID, 10L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print())
        .andExpect(content().contentType("text/plain;charset=UTF-8"))
        .andExpect(content().string("plot doesn't exist!"));
  }

  @Test
  @Transactional
  @DisplayName("fail irrigate a plot while sensor is down")
  void startPlotIrrigation_sensorDOWN() throws Exception {

    restIrrigateMockMvc
        .perform(get(ENTITY_API_URL_START_ID, 2L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andDo(print())
        .andExpect(content().contentType("text/plain;charset=UTF-8"))
        .andExpect(
            content().string("Sensor is DOWN, a time schedule is arranged to re-call the sensor"));
  }

  @Test
  @Transactional
  @DisplayName("Successfully stop irrigate a plot")
  void endPlotIrrigation_sensorUP() throws Exception {

    restIrrigateMockMvc
        .perform(get(ENTITY_API_URL_START_ID, 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.isIrrigated").value("true"));

    restIrrigateMockMvc
        .perform(get(ENTITY_API_URL_END_ID, 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.isIrrigated").value("false"));
  }

  @Test
  @Transactional
  @DisplayName("throw NotFoundException when end irrigate invalid plot")
  void endPlotIrrigation_InvalidPlot() throws Exception {

    restIrrigateMockMvc
        .perform(get(ENTITY_API_URL_END_ID, 10L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print())
        .andExpect(content().contentType("text/plain;charset=UTF-8"))
        .andExpect(content().string("plot doesn't exist!"));
  }
}
