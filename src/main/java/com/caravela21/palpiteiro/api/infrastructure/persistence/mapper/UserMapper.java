package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.dto.UserDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDTO dto);

    UserDTO toDTO(UserEntity entity);
}
