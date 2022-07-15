package com.example.automaticirrigationsystem.service.mapper;

import com.example.automaticirrigationsystem.domain.Slot;
import com.example.automaticirrigationsystem.dto.SlotDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Slot} and its DTO {@link SlotDTO}.
 */
@Mapper(componentModel = "spring", uses = {PlotMapper.class})
public interface SlotMapper extends EntityMapper<SlotDTO, Slot> {

}
