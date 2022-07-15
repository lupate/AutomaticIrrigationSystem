package com.example.automaticirrigationsystem.web.rest;


import com.example.automaticirrigationsystem.aop.logging.Loggable;
import com.example.automaticirrigationsystem.dto.PlotDTO;
import com.example.automaticirrigationsystem.exception.ResourceNotFoundException;
import com.example.automaticirrigationsystem.service.IrrigationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for manipulating irrigation business.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class IrrigationController {

  private final IrrigationService irrigationService;

  /**
   * {@code GET  /irrigate/start/{id}} : start irrigate a plot.
   *
   * @param plotId plot id to start irrigation.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   * sensorDTO, or with status {@code 400 (Not Found)} if the plot does not exist.
   */
  @GetMapping("/irrigate/start/{id}")
  @Loggable
  public ResponseEntity<PlotDTO> startPlotIrrigation(
      @PathVariable(value = "id") final Long plotId) {
    log.debug("REST request to start irrigation : {}", plotId);
    Optional<PlotDTO> result = irrigationService.startIrrigate(plotId);
    return result.map(plot -> ResponseEntity.ok().body(plot))
        .orElseThrow(() -> {
          throw new ResourceNotFoundException("plot doesn't exist!");
        });
  }

  /**
   * {@code GET  /irrigate/end/{id}} : end irrigate a plot.
   *
   * @param plotId plot id to start irrigation.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   * sensorDTO, or with status {@code 400 (Not Found)} if the plot does not exist.
   */
  @GetMapping("/irrigate/end/{id}")
  @Loggable
  public ResponseEntity<PlotDTO> endPlotIrrigation(
      @PathVariable(value = "id") final Long plotId) {
    log.debug("REST request to start irrigation : {}", plotId);
    var result = irrigationService.endIrrigate(plotId);
    return result.map(plot -> ResponseEntity.ok().body(plot))
        .orElseThrow(() -> {
          throw new ResourceNotFoundException("plot doesn't exist!");
        });
  }



}
