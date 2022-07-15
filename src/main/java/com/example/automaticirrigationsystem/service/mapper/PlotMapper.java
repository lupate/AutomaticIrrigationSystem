package com.example.automaticirrigationsystem.service.mapper;

import com.example.automaticirrigationsystem.domain.Plot;
import com.example.automaticirrigationsystem.dto.PlotDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Plot} and its DTO {@link PlotDTO}.
 */
@Mapper(componentModel = "spring", uses = {SensorMapper.class})
public interface PlotMapper extends EntityMapper<PlotDTO, Plot> {

}
