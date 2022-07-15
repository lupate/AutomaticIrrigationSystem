package com.example.automaticirrigationsystem.domain;


import com.example.automaticirrigationsystem.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Timing slot represents the slot to be used to irrigate the plot, based on the crop type: rice, 1
 * slot every 1m2/length. beans, 1 slot every 2m2/length
 */
@Entity
@Table(name = "slot")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Slot implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "plot_id", nullable = false)
  @JsonIgnoreProperties(value = {"plotSensor", "plotTimerSlots"}, allowSetters = true)
  private Plot plot;

}
