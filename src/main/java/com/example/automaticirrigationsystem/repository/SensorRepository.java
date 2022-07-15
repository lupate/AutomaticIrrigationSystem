package com.example.automaticirrigationsystem.repository;

import com.example.automaticirrigationsystem.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Sensor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

}
