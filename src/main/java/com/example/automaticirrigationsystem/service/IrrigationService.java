package com.example.automaticirrigationsystem.service;

import com.example.automaticirrigationsystem.aop.logging.Loggable;
import com.example.automaticirrigationsystem.domain.Plot;
import com.example.automaticirrigationsystem.domain.enumeration.Status;
import com.example.automaticirrigationsystem.dto.PlotDTO;
import com.example.automaticirrigationsystem.exception.BadRequestException;
import com.example.automaticirrigationsystem.exception.PlotHasAlreadyStartedToBeIrrigated;
import com.example.automaticirrigationsystem.exception.ResourceNotFoundException;
import com.example.automaticirrigationsystem.exception.SensorCantBeReachedException;
import com.example.automaticirrigationsystem.repository.PlotRepository;
import com.example.automaticirrigationsystem.service.mapper.PlotMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for manipulating plot irrigation {@link Plot}.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class IrrigationService {

  public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
  private final PlotRepository plotRepository;
  private final PlotMapper plotMapper;
  private final SensorCallingScheduler sensorCallingScheduler;

  @Value("${tries.count}")
  private int triesCount = 10;

  /**
   * start irrigate a plot.
   *
   * @param plotId the plot id to start irrigate.
   * @return the persisted plot.
   */
  @Loggable
  public Optional<PlotDTO> startIrrigate(Long plotId) {
    log.debug("Request to start irrigate a Plot : {}", plotId);
    Optional<Plot> existPlot = plotRepository.findById(plotId);

    existPlot.ifPresentOrElse(plot -> {
      if (plot.getSensorCallCount() > 0 && !plot.getHasAlert()) {
        throw new SensorCantBeReachedException(
            "Be patient!"
                + " sensor is scheduled to be called " + plot.getSensorCallCount() + "/"
                + triesCount + " " + plot.getLastSensorCallTime());
      } else {
        if (plot.getPlotSensor().getStatus() == Status.UP) {
          if (plot.getIsIrrigated()) {
            throw new PlotHasAlreadyStartedToBeIrrigated(
                "Irrigation has already  started by: " + plot.getStartIrrigationTime());
          }
          updatePlotIrrigationSuccess(plot);
        } else {
          if (plot.getHasAlert()) {
            throw new SensorCantBeReachedException(
                "Sensor is DOWN, the Plot has alert ON, please try to fix  the sensor first");
          }
          sensorCallingScheduler.tryToConnectToSensor(plot);
          throw new SensorCantBeReachedException(
              "Sensor is DOWN, a time schedule is arranged to re-call the sensor");
        }
      }
    }, () -> {
      throw new ResourceNotFoundException("plot doesn't exist!");
    });

    return existPlot.map(plotMapper::toDto);
  }

  private void updatePlotIrrigationSuccess(Plot plot) {
    plot.setHasAlert(false);
    plot.setSensorCallCount(0);
    plot.setLastSensorCallTime("");
    plot.setLastIrrigationTime(getFormattedNow());
    plot.setStartIrrigationTime(getFormattedNow());
    plot.setIsIrrigated(true);
    plot.setHasAlert(false);
    plot.getPlotTimerSlots().forEach(plotSlot -> plotSlot.setStatus(Status.UP));
  }

  private String getFormattedNow() {
    return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(LocalDateTime.now());
  }

  @Loggable
  public Optional<PlotDTO> endIrrigate(Long plotId) {
    log.debug("Request to end irrigate a Plot : {}", plotId);
    Optional<Plot> existPlot = plotRepository.findById(plotId);

    return existPlot.map(plot -> {
      if (Boolean.FALSE.equals(plot.getIsIrrigated())) {
        throw new BadRequestException("Please start irrigate first!");
      }
      plot.setIsIrrigated(false);
      plot.setLastIrrigationTime(
          DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(LocalDateTime.now()));
      plot.getPlotTimerSlots().forEach(plotSlot -> plotSlot.setStatus(Status.DOWN));

      return plot;
    }).map(plotMapper::toDto);
  }
}
