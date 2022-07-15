package com.example.automaticirrigationsystem.service;

import com.example.automaticirrigationsystem.aop.logging.Loggable;
import com.example.automaticirrigationsystem.domain.Plot;
import com.example.automaticirrigationsystem.domain.enumeration.Status;
import com.example.automaticirrigationsystem.repository.PlotRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class SensorCallingScheduler {

  public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
  private static final int TIME_SLEEP = 1000 * 30;
  private final PlotRepository plotRepository;
  @Value("${tries.count}")
  private int triesCount = 10;

  @Async
  @Loggable
  public void tryToConnectToSensor(Plot plot) {
    log.debug("try to connect to sensor  '{}'", plot);

    while (triesCount > 0) {
      log.debug("try to connect sensor tries remain: {}", triesCount);
      triesCount--;
      plot = plotRepository.findById(plot.getId()).stream().findFirst().orElse(new Plot());

      log.debug("current plot status {}", plot);

      if (plot.getPlotSensor().getStatus() == Status.UP) {
        if (plot.getSensorCallCount() != 0) {
          log.debug("sensor is up now after {} tries", triesCount);
        }
        UpdatePlotIrrigationSuccess(plot);
        break;
      }

      plot.setSensorCallCount(plot.getSensorCallCount() + 1);
      plot.setLastSensorCallTime(getFormattedNow());
      plotRepository.save(plot);
      if (triesCount != 0) {
        sleepTillNextTry();
      }
    }

    if (triesCount == 0) {
      plot.setHasAlert(true);
    }

    plotRepository.save(plot);
  }

  private void UpdatePlotIrrigationSuccess(Plot plot) {
    plot.setHasAlert(false);
    plot.setSensorCallCount(0);
    plot.setLastSensorCallTime("");
    plot.setLastIrrigationTime(getFormattedNow());
    plot.setStartIrrigationTime(getFormattedNow());
    plot.setIsIrrigated(true);
    plot.getPlotTimerSlots().forEach(plotSlot -> plotSlot.setStatus(Status.UP));
  }

  private String getFormattedNow() {
    return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(LocalDateTime.now());
  }

  private void sleepTillNextTry() {
    try {
      Thread.sleep(TIME_SLEEP);
    } catch (InterruptedException e) {
      log.error("interrupted '{}'", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }
}
