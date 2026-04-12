package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.PoolDTO;
import com.caravela21.palpiteiro.api.controller.dto.PoolMembershipDTO;
import com.caravela21.palpiteiro.api.enums.PoolMembershipStatus;
import com.caravela21.palpiteiro.api.exceptions.UnauthorizedPoolOperationException;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolMembershipEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.PoolMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.PoolMembershipMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolMembershipRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoolService {

    private static final String INVITE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int INVITE_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PoolRepository poolRepository;
    private final UserRepository userRepository;
    private final PoolMembershipRepository poolMembershipRepository;
    private final PoolMapper poolMapper;
    private final PoolMembershipMapper poolMembershipMapper;

    @Transactional
    public PoolDTO createPool(PoolDTO poolDTO) {
        if (poolDTO.ownerId() == null || poolDTO.ownerId().isBlank()) {
            throw new IllegalArgumentException("Owner ID must be provided");
        }

        UserEntity owner = userRepository.findById(poolDTO.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));

        PoolEntity poolEntity = new PoolEntity();
        poolEntity.setName(poolDTO.name());
        poolEntity.setOwner(owner);
        poolEntity.setInviteCode(generateUniqueInviteCode());

        PoolEntity saved = poolRepository.save(poolEntity);
        return poolMapper.toDTO(saved);
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
            for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
                sb.append(INVITE_CODE_CHARS.charAt(RANDOM.nextInt(INVITE_CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (poolRepository.existsByInviteCode(code));
        return code;
    }

    @Transactional(readOnly = true)
    public PoolDTO findByInviteCode(String inviteCode) {
        PoolEntity pool = poolRepository.findByInviteCode(inviteCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Pool not found for invite code: " + inviteCode));
        return poolMapper.toDTO(pool);
    }

    @Transactional
    public PoolMembershipDTO requestPoolAccess(String poolId, String userId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (pool.getOwner() != null && pool.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Pool owner cannot request access to their own pool");
        }

        // Verifica se o usuário já tem requisição pendente ou aprovada
        var existingMembership = poolMembershipRepository.findByPoolIdAndUserId(poolId, userId);
        if (existingMembership.isPresent()) {
            throw new IllegalArgumentException("User already has a membership request or is already a member of this pool");
        }

        PoolMembershipEntity membership = new PoolMembershipEntity();
        membership.setPool(pool);
        membership.setUser(user);
        membership.setStatus(PoolMembershipStatus.PENDING);
        membership.setRequestedAt(OffsetDateTime.now());

        PoolMembershipEntity saved = poolMembershipRepository.save(membership);
        return poolMembershipMapper.toDTO(saved);
    }

    @Transactional
    public PoolMembershipDTO approveMembershipRequest(String poolId, String userId, String ownerId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));

        // Verifica se o usuário é o owner da pool
        if (!pool.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedPoolOperationException("Only the pool owner can approve membership requests");
        }

        PoolMembershipEntity membership = poolMembershipRepository.findByPoolIdAndUserId(poolId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Membership request not found"));

        if (!membership.getStatus().equals(PoolMembershipStatus.PENDING)) {
            throw new IllegalArgumentException("Membership request is not pending");
        }

        membership.setStatus(PoolMembershipStatus.APPROVED);
        membership.setApprovedAt(OffsetDateTime.now());

        PoolMembershipEntity updated = poolMembershipRepository.save(membership);
        return poolMembershipMapper.toDTO(updated);
    }

    @Transactional
    public PoolMembershipDTO rejectMembershipRequest(String poolId, String userId, String ownerId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));

        // Verifica se o usuário é o owner da pool
        if (!pool.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedPoolOperationException("Only the pool owner can reject membership requests");
        }

        PoolMembershipEntity membership = poolMembershipRepository.findByPoolIdAndUserId(poolId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Membership request not found"));

        if (!membership.getStatus().equals(PoolMembershipStatus.PENDING)) {
            throw new IllegalArgumentException("Membership request is not pending");
        }

        membership.setStatus(PoolMembershipStatus.REJECTED);
        PoolMembershipEntity updated = poolMembershipRepository.save(membership);
        return poolMembershipMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public List<PoolMembershipDTO> getPendingMembershipRequests(String poolId, String ownerId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));

        // Verifica se o usuário é o owner da pool
        if (!pool.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedPoolOperationException("Only the pool owner can view membership requests");
        }

        return poolMembershipRepository.findByPoolId(poolId).stream()
                .filter(m -> m.getStatus().equals(PoolMembershipStatus.PENDING))
                .map(poolMembershipMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PoolMembershipDTO> getAllMemberships(String poolId, String ownerId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));

        if (!pool.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedPoolOperationException("Only the pool owner can view all memberships");
        }

        return poolMembershipRepository.findByPoolIdOrderByStatusAscRequestedAtAsc(poolId).stream()
                .map(poolMembershipMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PoolDTO> getPoolsByOwner(String ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return poolRepository.findByOwnerId(ownerId).stream()
                .map(poolMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PoolMembershipDTO> getPoolMembers(String poolId) {
        return poolMembershipRepository.findByPoolId(poolId).stream()
                .filter(m -> m.getStatus().equals(PoolMembershipStatus.APPROVED))
                .map(poolMembershipMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeMember(String poolId, String userId, String ownerId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));

        // Verifica se o usuário é o owner da pool
        if (!pool.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedPoolOperationException("Only the pool owner can remove members");
        }

        // Previne owner de se remover
        if (pool.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Owner cannot remove themselves from the pool");
        }

        PoolMembershipEntity membership = poolMembershipRepository.findByPoolIdAndUserId(poolId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found in this pool"));

        poolMembershipRepository.delete(membership);
    }

    @Transactional(readOnly = true)
    public PoolDTO getPoolById(String poolId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("Pool not found"));
        return poolMapper.toDTO(pool);
    }
}


