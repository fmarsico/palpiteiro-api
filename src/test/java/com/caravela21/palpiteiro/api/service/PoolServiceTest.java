package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.PoolMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.PoolMembershipMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolMembershipRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PoolServiceTest {

    @Mock
    private PoolRepository poolRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PoolMembershipRepository poolMembershipRepository;
    @Mock
    private PoolMapper poolMapper;
    @Mock
    private PoolMembershipMapper poolMembershipMapper;

    @InjectMocks
    private PoolService poolService;

    @Test
    @DisplayName("Request pool access as owner - should throw IllegalArgumentException")
    void requestPoolAccess_UserIsOwner_ThrowsIllegalArgumentException() {
        // Arrange
        String poolId = "pool-123";
        String ownerId = "owner-123";

        UserEntity owner = new UserEntity();
        owner.setId(ownerId);

        PoolEntity pool = new PoolEntity();
        pool.setId(poolId);
        pool.setOwner(owner);

        when(poolRepository.findById(poolId)).thenReturn(Optional.of(pool));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        // Act + Assert
        assertThatThrownBy(() -> poolService.requestPoolAccess(poolId, ownerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pool owner cannot request access to their own pool");

        verify(poolMembershipRepository, never()).findByPoolIdAndUserId(any(), any());
        verify(poolMembershipRepository, never()).save(any());
    }
}

