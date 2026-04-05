package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.TeamDTO;
import com.caravela21.palpiteiro.api.service.TeamService;
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
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Operation(summary = "Create a new team", description = "Creates a new team with name and optional flag URL")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Team successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Team already exists")
    })
    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@RequestBody @Valid TeamDTO teamDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(teamDTO));
    }

    @Operation(summary = "Update an existing team", description = "Updates team name and flag URL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable String id, @RequestBody @Valid TeamDTO teamDTO) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamDTO));
    }

    @Operation(summary = "Get a team by ID", description = "Returns a team by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team found"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable String id) {
        return ResponseEntity.ok(teamService.findById(id));
    }

    @Operation(summary = "List all teams", description = "Returns a list of all registered teams")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.findAll());
    }
}

