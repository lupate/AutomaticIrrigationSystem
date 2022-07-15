package com.example.automaticirrigationsystem.dto;

import com.example.automaticirrigationsystem.domain.enumeration.Status;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * A DTO for the {@link com.example.automaticirrigationsystem.domain.Sensor} entity.
 */
@Data
public class SensorDTO implements Serializable {

  private Long id;

  private String sensorCode;

  @NotNull
  private Status status;

}
