package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.MatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.MatchResultBatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.MatchResultUpdateItemDTO;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.mapper.MatchMapper;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final MatchMapper matchMapper;

    @Transactional
    public MatchDTO createMatch(MatchDTO matchDTO) {
        validateTeams(matchDTO.homeTeamId(), matchDTO.awayTeamId());

        TeamEntity homeTeam = findTeamById(matchDTO.homeTeamId());
        TeamEntity awayTeam = findTeamById(matchDTO.awayTeamId());

        MatchEntity entity = matchMapper.toEntity(matchDTO);
        entity.setHomeTeam(homeTeam);
        entity.setAwayTeam(awayTeam);

        if (matchDTO.result() != null) {
            entity.setResult(matchMapper.toEmbeddable(matchDTO.result()));
        }

        MatchEntity saved = matchRepository.save(entity);
        return matchMapper.toDTO(saved);
    }

    @Transactional
    public MatchDTO updateMatch(String id, MatchDTO matchDTO) {
        MatchEntity existing = matchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + id));

        validateTeams(matchDTO.homeTeamId(), matchDTO.awayTeamId());

        TeamEntity homeTeam = findTeamById(matchDTO.homeTeamId());
        TeamEntity awayTeam = findTeamById(matchDTO.awayTeamId());

        existing.setHomeTeam(homeTeam);
        existing.setAwayTeam(awayTeam);
        existing.setDate(matchDTO.date());
        existing.setPhase(matchDTO.phase());
        existing.setGroupCode(matchDTO.groupCode());

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

    @Transactional
    public List<MatchDTO> updateMatchResults(MatchResultBatchDTO batchDTO) {
        validateDuplicateMatchIds(batchDTO.results());

        return batchDTO.results().stream()
                .map(this::updateSingleMatchResult)
                .toList();
    }

    private MatchDTO updateSingleMatchResult(MatchResultUpdateItemDTO item) {
        MatchEntity existing = matchRepository.findById(item.matchId())
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + item.matchId()));

        existing.setResult(matchMapper.toEmbeddable(item.result()));

        MatchEntity saved = matchRepository.save(existing);
        return matchMapper.toDTO(saved);
    }

    private void validateDuplicateMatchIds(List<MatchResultUpdateItemDTO> items) {
        Set<String> seenIds = new HashSet<>();
        for (MatchResultUpdateItemDTO item : items) {
            if (!seenIds.add(item.matchId())) {
                throw new IllegalArgumentException("Duplicate match ID in request: " + item.matchId());
            }
        }
    }

    private TeamEntity findTeamById(String id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
    }

    private void validateTeams(String homeTeamId, String awayTeamId) {
        if (homeTeamId.equals(awayTeamId)) {
            throw new IllegalArgumentException("Home team and away team must be different");
        }
    }
}
