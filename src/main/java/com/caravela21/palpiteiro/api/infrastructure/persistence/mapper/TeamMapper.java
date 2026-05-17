package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.controller.dto.TeamDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "externalId", ignore = true)
    TeamEntity toEntity(TeamDTO dto);

    TeamDTO toDTO(TeamEntity entity);
}

