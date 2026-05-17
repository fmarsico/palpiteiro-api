package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.controller.dto.MatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.MatchResultDTO;
import com.caravela21.palpiteiro.api.controller.dto.TeamDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchResultEmbeddable;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(target = "homeTeam", ignore = true)
    @Mapping(target = "awayTeam", ignore = true)
    @Mapping(target = "externalFixtureId", ignore = true)
    @Mapping(target = "roundName", ignore = true)
    @Mapping(target = "status", ignore = true)
    MatchEntity toEntity(MatchDTO dto);

    @Mapping(target = "homeTeamId", source = "homeTeam.id")
    @Mapping(target = "awayTeamId", source = "awayTeam.id")
    MatchDTO toDTO(MatchEntity entity);

    MatchResultEmbeddable toEmbeddable(MatchResultDTO dto);

    MatchResultDTO toResultDTO(MatchResultEmbeddable embeddable);

    TeamDTO toTeamDTO(TeamEntity entity);
}
