package com.caravela21.palpiteiro.api.infrastructure.persistence.mapper;

import com.caravela21.palpiteiro.api.controller.dto.PoolMembershipDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolMembershipEntity;
import org.springframework.stereotype.Component;

@Component
public class PoolMembershipMapper {

    public PoolMembershipDTO toDTO(PoolMembershipEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PoolMembershipDTO(
                entity.getId(),
                entity.getPool().getId(),
                entity.getUser().getId(),
                entity.getUser().getName(),
                entity.getStatus(),
                entity.getRequestedAt(),
                entity.getApprovedAt()
        );
    }
}

