package com.example.automaticirrigationsystem.domain;

import com.example.automaticirrigationsystem.domain.enumeration.CropType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

/**
 * A Plot class represent a unit of land or cultivated area to be irrigated
 */
@Entity
@Table(name = "plot")
@Setter
@Getter
@ToString
@RequiredArgsConstructor
public class Plot implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "code", nullable = false, unique = true)
  private String plotCode;

  @NotNull
  @Column(name = "length")
  private Double plotLength;

  @NotNull
  @Column(name = "width")
  private Double plotWidth;

  @Column(name = "is_irrigated")
  private Boolean isIrrigated;

  @Column(name = "sensor_call_count")
  private Integer sensorCallCount;

  @Column(name = "last_sensor_call_time")
  private String lastSensorCallTime;

  @Column(name = "has_alert")
  private Boolean hasAlert;

  @Column(name = "start_irrigation_time")
  private String startIrrigationTime;

  @Column(name = "last_irrigation_time")
  private String lastIrrigationTime;

  @Column(name = "water_amount")
  private Integer waterAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "crop_type")
  private CropType cropType;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(unique = true, name = "sensor_id", referencedColumnName = "id")
  @JsonIgnoreProperties(value = {"plot"}, allowSetters = true)
  private Sensor plotSensor;

  @OneToMany(mappedBy = "plot", fetch = FetchType.LAZY,
      cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  @JsonIgnoreProperties(value = {"plot"}, allowSetters = true)
  private List<Slot> plotTimerSlots = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Plot plot = (Plot) o;
    return id != null && Objects.equals(id, plot.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
