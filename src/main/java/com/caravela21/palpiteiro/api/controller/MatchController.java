package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.MatchDTO;
import com.caravela21.palpiteiro.api.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Operation(
            summary = "Create a new match",
            description = "Creates a new match using homeTeamId, awayTeamId, date in ISO-8601 with offset and phase (GROUP_STAGE, SECOND_ROUND, ROUND_OF_16, QUARTER_FINAL, SEMI_FINAL, FINAL)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Match successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<MatchDTO> createMatch(@RequestBody @Valid MatchDTO matchDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createMatch(matchDTO));
    }

    @Operation(
            summary = "Update an existing match",
            description = "Updates match data and/or result based on the provided ID and team IDs. Date must use ISO-8601 with offset and phase is required."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Match not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MatchDTO> updateMatch(@PathVariable String id, @RequestBody @Valid MatchDTO matchDTO) {
        return ResponseEntity.ok(matchService.updateMatch(id, matchDTO));
    }

    @Operation(
            summary = "Get a match by ID",
            description = "Returns a match by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match found"),
            @ApiResponse(responseCode = "404", description = "Match not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable String id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    @Operation(
            summary = "List all matches",
            description = "Returns a list of all registered matches."
    )
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    @GetMapping
    public ResponseEntity<List<MatchDTO>> getAllMatches() {
        return ResponseEntity.ok(matchService.findAll());
    }
}
