package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.UserDTO;
import com.caravela21.palpiteiro.api.exceptions.MissingUserIdException;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.UserMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {

        if (userDTO.id() == null || userDTO.id().isBlank()) {
            throw new MissingUserIdException("User ID must be provided (Firebase UID)");
        }

        if (userRepository.existsById(userDTO.id())) {
            throw new IllegalArgumentException("User already exists");
        }

        UserEntity entity = userMapper.toEntity(userDTO);
        UserEntity saved = userRepository.save(entity);

        return userMapper.toDTO(saved);
    }

    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {

        if (userDTO.id() == null || userDTO.id().isBlank()) {
            throw new MissingUserIdException("User ID must be provided for update");
        }

        UserEntity existing = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        applyUpdatableFields(existing, userDTO);

        UserEntity saved = userRepository.save(existing);

        return userMapper.toDTO(saved);
    }

    @Transactional
    public UserDTO updateAuthenticatedUser(String subject, UserDTO userDTO) {
        UserEntity existing = findBySubject(subject)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        if (userDTO.id() != null && !userDTO.id().isBlank() && !userDTO.id().equals(existing.getId())) {
            throw new IllegalArgumentException("User ID in body must match authenticated user");
        }

        applyUpdatableFields(existing, userDTO);

        UserEntity saved = userRepository.save(existing);

        return userMapper.toDTO(saved);
    }

    private Optional<UserEntity> findBySubject(String subject) {
        Optional<UserEntity> byId = userRepository.findById(subject);
        if (byId.isPresent()) {
            return byId;
        }

        return userRepository.findByEmail(subject);
    }

    private void applyUpdatableFields(UserEntity existing, UserDTO userDTO) {

        // Atualiza apenas campos permitidos
        existing.setName(userDTO.name());
        existing.setLastname(userDTO.lastname());
        existing.setEmail(userDTO.email());
        existing.setPhotoUrl(userDTO.photoUrl());
    }


}
