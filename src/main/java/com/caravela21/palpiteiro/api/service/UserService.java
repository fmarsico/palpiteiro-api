package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.UserDTO;
import com.caravela21.palpiteiro.api.exceptions.MissingUserIdException;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.UserMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

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

        // Atualiza apenas campos permitidos
        existing.setName(userDTO.name());
        existing.setLastname(userDTO.lastname());
        existing.setEmail(userDTO.email());
        existing.setPhotoUrl(userDTO.photoUrl());

        UserEntity saved = userRepository.save(existing);

        return userMapper.toDTO(saved);
    }


}
