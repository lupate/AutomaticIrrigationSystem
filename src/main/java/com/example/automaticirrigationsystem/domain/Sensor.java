package com.example.automaticirrigationsystem.domain;


import com.example.automaticirrigationsystem.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

/**
 * Sensor, is the hardware that receives the start signal and calls the timing slots to start
 * irrigate *** if it is not reached in 3 times 5 min- step, an alert is declared and the plot
 * flagged as has a defect.
 */

@Entity
@Table(name = "sensor")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Sensor implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "code", nullable = false, unique = true)
  private String sensorCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;

  @ToString.Exclude
  @JsonIgnoreProperties(value = {"plotSensor", "plotTimerSlots"}, allowSetters = true)
  @OneToOne(mappedBy = "plotSensor")
  private Plot plot;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Sensor sensor = (Sensor) o;
    return id != null && Objects.equals(id, sensor.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
