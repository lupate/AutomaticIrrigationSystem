package com.example.automaticirrigationsystem.repository;

import com.example.automaticirrigationsystem.domain.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Slot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

}
