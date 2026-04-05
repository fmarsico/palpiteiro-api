package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.TeamDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.TeamMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMapper teamMapper;

    @Transactional
    public TeamDTO createTeam(TeamDTO teamDTO) {
        if (teamRepository.existsByNameIgnoreCase(teamDTO.name())) {
            throw new IllegalArgumentException("Team already exists with this name");
        }

        TeamEntity teamEntity = teamMapper.toEntity(teamDTO);
        TeamEntity saved = teamRepository.save(teamEntity);
        return teamMapper.toDTO(saved);
    }

    @Transactional
    public TeamDTO updateTeam(String id, TeamDTO teamDTO) {
        TeamEntity existing = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));

        if (!existing.getName().equalsIgnoreCase(teamDTO.name())
                && teamRepository.existsByNameIgnoreCase(teamDTO.name())) {
            throw new IllegalArgumentException("Team already exists with this name");
        }

        existing.setName(teamDTO.name());
        existing.setFlagUrl(teamDTO.flagUrl());

        TeamEntity saved = teamRepository.save(existing);
        return teamMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public TeamDTO findById(String id) {
        TeamEntity teamEntity = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));

        return teamMapper.toDTO(teamEntity);
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> findAll() {
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toDTO)
                .toList();
    }
}

