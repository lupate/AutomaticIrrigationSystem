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
import com.example.automaticirrigationsystem.domain.Slot;
import com.example.automaticirrigationsystem.domain.enumeration.CropType;
import com.example.automaticirrigationsystem.domain.enumeration.Status;
import com.example.automaticirrigationsystem.dto.PlotConfigDTO;
import com.example.automaticirrigationsystem.dto.PlotDTO;
import com.example.automaticirrigationsystem.repository.PlotRepository;
import com.example.automaticirrigationsystem.service.mapper.PlotMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PlotResourceController} REST controller.
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class PlotResourceControllerIT {

    public static final int SLOTS_COUNT = 10;
    public static final CropType CROP_TYPE = CropType.RICE;
    public static final int WATER_AMOUNT = 100;
    private static final String DEFAULT_CODE = "plot-10000000";
    private static final String UPDATED_CODE = "plot-2000000";
    private static final Double DEFAULT_LENGTH = 10D;
    private static final Double UPDATED_LENGTH = 20D;
    private static final Double DEFAULT_WIDTH = 10D;
    private static final Double UPDATED_WIDTH = 15D;
    private static final String ENTITY_API_URL = "/api/plots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_CONFIG_URL = "/api/plots/config/{id}";
    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt());

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PlotMapper plotMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlotMockMvc;

    private Plot plot;


    @BeforeEach
    public void initTest() {
        log.info("Integration Tests Started");
        plot = new Plot();
        plot.setPlotCode(DEFAULT_CODE);
        plot.setPlotLength(DEFAULT_LENGTH);
        plot.setPlotWidth(DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void createPlot() throws Exception {
        int databaseSizeBeforeCreate = plotRepository.findAll().size();
        // Create the Plot
        PlotDTO plotDTO = plotMapper.toDto(plot);
        restPlotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(plotDTO)))
            .andExpect(status().isCreated());

        // Validate the Plot in the database
        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeCreate + 1);
        Plot testPlot = plotList.get(plotList.size() - 1);
        assertThat(testPlot.getPlotCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPlot.getPlotLength()).isEqualTo(DEFAULT_LENGTH);
        assertThat(testPlot.getPlotWidth()).isEqualTo(DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void createPlotWillIgnoreTheProvidedId() throws Exception {
        // Create the Plot with an existing ID
        plot.setId(1L);
        PlotDTO plotDTO = plotMapper.toDto(plot);

        int databaseSizeBeforeCreate = plotRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(plotDTO)))
            .andExpect(status().isCreated());

        // Validate the Plot in the database
        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeCreate + 1);
        Plot testPlot = plotList.get(plotList.size() - 1);
        assertThat(testPlot.getId()).isNotEqualTo(1L);
        assertThat(testPlot.getPlotCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPlot.getPlotLength()).isEqualTo(DEFAULT_LENGTH);
        assertThat(testPlot.getPlotWidth()).isEqualTo(DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = plotRepository.findAll().size();
        // set the field null
        plot.setPlotCode(null);

        // Create the Plot, which fails.
        PlotDTO plotDTO = plotMapper.toDto(plot);

        restPlotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(plotDTO)))
            .andExpect(status().isBadRequest());

        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlots() throws Exception {
        // Initialize the database
        plotRepository.saveAndFlush(plot);

        // Get all the plotList
        restPlotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plot.getId().intValue())))
            .andExpect(jsonPath("$.[*].plotCode").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].plotLength").value(hasItem(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.[*].plotWidth").value(hasItem(DEFAULT_WIDTH)));
    }

    @Test
    @Transactional
    void getPlot() throws Exception {
        // Initialize the database
        plotRepository.saveAndFlush(plot);

        // Get the plot
        restPlotMockMvc
            .perform(get(ENTITY_API_URL_ID, plot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plot.getId().intValue()))
            .andExpect(jsonPath("$.plotCode").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.plotLength").value(DEFAULT_LENGTH))
            .andExpect(jsonPath("$.plotWidth").value(DEFAULT_WIDTH));
    }

    @Test
    @Transactional
    void getNonExistingPlot() throws Exception {
        // Get the plot
        restPlotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void configurePlot() throws Exception {
        // Initialize the database
        plotRepository.saveAndFlush(plot);

        PlotConfigDTO plotConfigDTO = new PlotConfigDTO();
        plotConfigDTO.setCropType(CROP_TYPE);
        plotConfigDTO.setSlotsCount(SLOTS_COUNT);
        plotConfigDTO.setWaterAmount(WATER_AMOUNT);

        restPlotMockMvc
            .perform(
                put(ENTITY_API_CONFIG_URL, plot.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plotConfigDTO))
            )
            .andExpect(status().isOk());

        // Validate the Plot in the database
        List<Plot> plotList = plotRepository.findAll();
        Plot testPlot = plotList.get(plotList.size() - 1);
        assertThat(testPlot.getPlotCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPlot.getPlotLength()).isEqualTo(DEFAULT_LENGTH);
        assertThat(testPlot.getPlotWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testPlot.getWaterAmount()).isEqualTo(WATER_AMOUNT);
        assertThat(testPlot.getCropType()).isEqualTo(CROP_TYPE);
        assertThat(testPlot.getPlotTimerSlots()).hasSize(SLOTS_COUNT);
    }


    @Test
    @Transactional
    void updatePlot() throws Exception {
        // Initialize the database
        plotRepository.saveAndFlush(plot);

        int databaseSizeBeforeUpdate = plotRepository.findAll().size();

        // Update the plot
        Plot updatedPlot = plotRepository.findById(plot.getId()).get();
        // Disconnect from session so that the updates on updatedPlot are not directly saved in db
        em.detach(updatedPlot);
        updatedPlot.setPlotCode(UPDATED_CODE);
        updatedPlot.setPlotLength(UPDATED_LENGTH);
        updatedPlot.setPlotWidth(UPDATED_WIDTH);

        PlotDTO plotDTO = plotMapper.toDto(updatedPlot);

        restPlotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, plotDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plotDTO))
            )
            .andExpect(status().isOk());

        // Validate the Plot in the database
        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeUpdate);
        Plot testPlot = plotList.get(plotList.size() - 1);
        assertThat(testPlot.getPlotCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPlot.getPlotLength()).isEqualTo(UPDATED_LENGTH);
        assertThat(testPlot.getPlotWidth()).isEqualTo(UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void updatePlotWillNotAffectTheConfige() throws Exception {
        // Initialize the database
        plot.setCropType(CROP_TYPE);
        plot.setWaterAmount(WATER_AMOUNT);
        Slot slot = new Slot();
        slot.setStatus(Status.UP);
        plotRepository.saveAndFlush(plot);

        int databaseSizeBeforeUpdate = plotRepository.findAll().size();

        // Update the plot
        Plot updatedPlot = new Plot();
        updatedPlot.setId(plot.getId());
        updatedPlot.setPlotCode(UPDATED_CODE);
        updatedPlot.setPlotLength(UPDATED_LENGTH);
        updatedPlot.setPlotWidth(UPDATED_WIDTH);

        PlotDTO plotDTO = plotMapper.toDto(updatedPlot);

        restPlotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, plotDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plotDTO))
            )
            .andExpect(status().isOk());

        // Validate the Plot in the database
        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeUpdate);
        Plot testPlot = plotList.get(plotList.size() - 1);
        assertThat(testPlot.getPlotCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPlot.getPlotLength()).isEqualTo(UPDATED_LENGTH);
        assertThat(testPlot.getPlotWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testPlot.getWaterAmount()).isEqualTo(WATER_AMOUNT);
        assertThat(testPlot.getCropType()).isEqualTo(CROP_TYPE);

    }

    @Test
    @Transactional
    void updateNonExistingPlot() throws Exception {
        int databaseSizeBeforeUpdate = plotRepository.findAll().size();
        plot.setId(count.incrementAndGet());

        // Create the Plot
        PlotDTO plotDTO = plotMapper.toDto(plot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, plotDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plotDTO))
            )
            .andExpect(status().isNotFound());

        // Validate the Plot in the database
        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeUpdate);
    }



    @Test
    @Transactional
    void putWithMissingIdPathParamPlot() throws Exception {
        int databaseSizeBeforeUpdate = plotRepository.findAll().size();
        plot.setId(count.incrementAndGet());

        // Create the Plot
        PlotDTO plotDTO = plotMapper.toDto(plot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlotMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(plotDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plot in the database
        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlot() throws Exception {
        // Initialize the database
        plotRepository.saveAndFlush(plot);

        int databaseSizeBeforeDelete = plotRepository.findAll().size();

        // Delete the plot
        restPlotMockMvc
            .perform(delete(ENTITY_API_URL_ID, plot.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Plot> plotList = plotRepository.findAll();
        assertThat(plotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
