package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.dto.PredictionDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PredictionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PredictionMapper {
    PredictionDTO toDTO(PredictionEntity entity);
    PredictionEntity toEntity(PredictionDTO dto);
}


