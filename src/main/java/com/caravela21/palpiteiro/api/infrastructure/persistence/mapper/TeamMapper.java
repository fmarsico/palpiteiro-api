package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.controller.dto.TeamDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    TeamEntity toEntity(TeamDTO dto);

    TeamDTO toDTO(TeamEntity entity);
}

