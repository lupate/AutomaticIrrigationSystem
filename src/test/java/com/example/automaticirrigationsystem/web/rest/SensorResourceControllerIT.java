package com.example.automaticirrigationsystem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.automaticirrigationsystem.domain.Plot;
import com.example.automaticirrigationsystem.domain.Sensor;
import com.example.automaticirrigationsystem.domain.enumeration.Status;
import com.example.automaticirrigationsystem.dto.SensorDTO;
import com.example.automaticirrigationsystem.repository.PlotRepository;
import com.example.automaticirrigationsystem.repository.SensorRepository;
import com.example.automaticirrigationsystem.service.mapper.SensorMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SensorResourceController} REST controller.
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class SensorResourceControllerIT {

  private static final String DEFAULT_CODE = "sensor-1999999999";
  private static final String UPDATED_CODE = "sensor-10000000000000";

  private static final Status DEFAULT_STATUS = Status.UP;
  private static final Status UPDATED_STATUS = Status.DOWN;

  private static final String ENTITY_API_URL = "/api/sensors";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static final Random random = new Random();
  private static final AtomicLong count = new AtomicLong(random.nextInt());

  @Autowired
  private SensorRepository sensorRepository;

  @Autowired
  private PlotRepository plotRepository;

  @Autowired
  private SensorMapper sensorMapper;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restSensorMockMvc;

  private Sensor sensor;

  @AfterAll
  static void done() {
    log.info("Integration Tests Executed");
  }

  @BeforeEach
  public void initTest() {
    log.info("Integration Tests Started");
    sensor = new Sensor();
    sensor.setSensorCode(DEFAULT_CODE);
    sensor.setStatus(DEFAULT_STATUS);
  }

  @Test
  @Transactional
  void createSensor() throws Exception {
    Plot plot = new Plot();
    plot.setPlotWidth(100D);
    plot.setPlotLength(100D);
    plot.setPlotCode("dlsl");
    plotRepository.saveAndFlush(plot);

    int databaseSizeBeforeCreate = sensorRepository.findAll().size();
    // Create the Sensor
    SensorDTO sensorDTO = sensorMapper.toDto(sensor);
    restSensorMockMvc
        .perform(post(ENTITY_API_URL_ID, plot.getId()).contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sensorDTO)))
        .andExpect(status().isCreated());

    // Validate the Sensor in the database
    List<Sensor> sensorList = sensorRepository.findAll();
    assertThat(sensorList).hasSize(databaseSizeBeforeCreate + 1);
    Sensor testSensor = sensorList.get(sensorList.size() - 1);
    assertThat(testSensor.getSensorCode()).isEqualTo(DEFAULT_CODE);
    assertThat(testSensor.getStatus()).isEqualTo(DEFAULT_STATUS);
  }


  @Test
  @Transactional
  void getAllSensors() throws Exception {
    // Initialize the database
    sensorRepository.saveAndFlush(sensor);

    // Get all the sensorList
    restSensorMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(sensor.getId().intValue())))
        .andExpect(jsonPath("$.[*].sensorCode").value(hasItem(DEFAULT_CODE)))
        .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
  }

  @Test
  @Transactional
  void getSensor() throws Exception {
    // Initialize the database
    sensorRepository.saveAndFlush(sensor);

    // Get the sensor
    restSensorMockMvc
        .perform(get(ENTITY_API_URL_ID, sensor.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(sensor.getId().intValue()))
        .andExpect(jsonPath("$.sensorCode").value(DEFAULT_CODE))
        .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
  }

  @Test
  @Transactional
  void getNonExistingSensor() throws Exception {
    // Get the sensor
    restSensorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void updateSensor() throws Exception {
    // Initialize the database
    sensorRepository.saveAndFlush(sensor);

    int databaseSizeBeforeUpdate = sensorRepository.findAll().size();

    // Update the sensor
    Sensor updatedSensor = sensorRepository.findById(sensor.getId()).get();
    // Disconnect from session so that the updates on updatedSensor are not directly saved in db
    em.detach(updatedSensor);
    updatedSensor.setSensorCode(UPDATED_CODE);
    updatedSensor.setStatus(UPDATED_STATUS);
    SensorDTO sensorDTO = sensorMapper.toDto(updatedSensor);

    restSensorMockMvc
        .perform(
            put(ENTITY_API_URL_ID, sensorDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(sensorDTO))
        )
        .andExpect(status().isOk());

    // Validate the Sensor in the database
    List<Sensor> sensorList = sensorRepository.findAll();
    assertThat(sensorList).hasSize(databaseSizeBeforeUpdate);
    Sensor testSensor = sensorList.get(sensorList.size() - 1);
    assertThat(testSensor.getSensorCode()).isEqualTo(UPDATED_CODE);
    assertThat(testSensor.getStatus()).isEqualTo(UPDATED_STATUS);
  }

  @Test
  @Transactional
  void updateNonExistingSensor() throws Exception {
    int databaseSizeBeforeUpdate = sensorRepository.findAll().size();
    sensor.setId(count.incrementAndGet());

    // Create the Sensor
    SensorDTO sensorDTO = sensorMapper.toDto(sensor);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restSensorMockMvc
        .perform(
            put(ENTITY_API_URL_ID, sensorDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(sensorDTO))
        )
        .andExpect(status().isNotFound());

    // Validate the Sensor in the database
    List<Sensor> sensorList = sensorRepository.findAll();
    assertThat(sensorList).hasSize(databaseSizeBeforeUpdate);
  }


  @Test
  @Transactional
  void updateWithMissingIdPathParamSensor() throws Exception {
    int databaseSizeBeforeUpdate = sensorRepository.findAll().size();
    sensor.setId(count.incrementAndGet());

    // Create the Sensor
    SensorDTO sensorDTO = sensorMapper.toDto(sensor);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSensorMockMvc
        .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sensorDTO)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Sensor in the database
    List<Sensor> sensorList = sensorRepository.findAll();
    assertThat(sensorList).hasSize(databaseSizeBeforeUpdate);
  }


  @Test
  @Transactional
  void deleteSensor() throws Exception {
    // Initialize the database
    sensorRepository.saveAndFlush(sensor);

    int databaseSizeBeforeDelete = sensorRepository.findAll().size();

    // Delete the sensor
    restSensorMockMvc
        .perform(delete(ENTITY_API_URL_ID, sensor.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Sensor> sensorList = sensorRepository.findAll();
    assertThat(sensorList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
