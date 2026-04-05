package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.controller.dto.PoolDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PoolMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    PoolDTO toDTO(PoolEntity entity);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(target = "inviteCode", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    PoolEntity toEntity(PoolDTO dto);
}

