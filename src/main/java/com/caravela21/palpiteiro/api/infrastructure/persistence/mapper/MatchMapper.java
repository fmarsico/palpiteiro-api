package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.controller.dto.MatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.MatchResultDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchResultEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    MatchEntity toEntity(MatchDTO dto);

    MatchDTO toDTO(MatchEntity entity);

    MatchResultEmbeddable toEmbeddable(MatchResultDTO dto);

    MatchResultDTO toResultDTO(MatchResultEmbeddable embeddable);
}

