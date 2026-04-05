package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.MatchDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.MatchMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchMapper matchMapper;

    @Transactional
    public MatchDTO createMatch(MatchDTO matchDTO) {
        MatchEntity entity = matchMapper.toEntity(matchDTO);
        MatchEntity saved = matchRepository.save(entity);
        return matchMapper.toDTO(saved);
    }

    @Transactional
    public MatchDTO updateMatch(String id, MatchDTO matchDTO) {
        MatchEntity existing = matchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + id));

        existing.setHomeTeam(matchDTO.homeTeam());
        existing.setAwayTeam(matchDTO.awayTeam());
        existing.setDate(matchDTO.date());

        if (matchDTO.result() != null) {
            existing.setResult(matchMapper.toEmbeddable(matchDTO.result()));
        }

        MatchEntity saved = matchRepository.save(existing);
        return matchMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public MatchDTO findById(String id) {
        MatchEntity entity = matchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + id));
        return matchMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<MatchDTO> findAll() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toDTO)
                .toList();
    }
}

