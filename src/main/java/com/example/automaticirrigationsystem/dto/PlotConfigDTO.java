package com.example.automaticirrigationsystem.dto;

import com.example.automaticirrigationsystem.domain.enumeration.CropType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlotConfigDTO {

  private Long Id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private CropType cropType;

  @NotNull
  private int waterAmount;

  @NotNull
  private int slotsCount;
}
