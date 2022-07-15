package com.example.automaticirrigationsystem.dto;

import com.example.automaticirrigationsystem.domain.Sensor;
import com.example.automaticirrigationsystem.domain.Slot;
import com.example.automaticirrigationsystem.domain.enumeration.CropType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * A DTO for the {@link com.example.automaticirrigationsystem.domain.Plot} entity.
 */
@Data
public class PlotDTO implements Serializable {

  private Long Id;

  @NotNull
  private String plotCode;

  @NotNull
  private Double plotLength;

  @NotNull
  private Double plotWidth;

  @JsonProperty(access = Access.READ_ONLY)
  private Boolean isIrrigated;

  @JsonProperty(access = Access.READ_ONLY)
  private Integer sensorCallCount;

  @JsonProperty(access = Access.READ_ONLY)
  private String lastSensorCallTime;

  @JsonProperty(access = Access.READ_ONLY)
  private Boolean hasAlert;

  @JsonProperty(access = Access.READ_ONLY)
  private String startIrrigationTime;

  @JsonProperty(access = Access.READ_ONLY)
  private String lastIrrigationTime;

  private Integer waterAmount;

  private CropType cropType;

  @JsonIgnoreProperties(value = {"plot"}, allowSetters = true)
  private Sensor plotSensor;
  @JsonIgnoreProperties(value = {"plot"}, allowSetters = true)
  private List<Slot> plotTimerSlots = new ArrayList<>();


}
