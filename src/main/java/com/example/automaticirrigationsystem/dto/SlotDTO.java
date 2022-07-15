package com.example.automaticirrigationsystem.dto;

import com.example.automaticirrigationsystem.domain.enumeration.Status;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * A DTO for the {@link com.example.automaticirrigationsystem.domain.Slot} entity.
 */
@Data
public class SlotDTO implements Serializable {

  private Long id;

  @NotNull
  private String code;

  private Status status;

}
