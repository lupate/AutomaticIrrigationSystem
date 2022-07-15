package com.example.automaticirrigationsystem.repository;

import com.example.automaticirrigationsystem.domain.Plot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PlotRepository extends JpaRepository<Plot, Long> {

  List<Plot> findAllByHasAlertIsTrue();

  @Modifying
  @Query("update Plot p set p.hasAlert = false where p.id = ?1")
  int fixPlotAlert(Long plotId);
}
