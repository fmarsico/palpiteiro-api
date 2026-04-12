package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.TeamDTO;
import com.caravela21.palpiteiro.api.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeamService teamService;

    // ============= CREATE TEAM - SUCCESS TESTS =============

    @Test
    @DisplayName("Create team with valid name and flag URL - should return 201")
    void createTeam_ValidNameAndUrl_ReturnsCreated() throws Exception {
        // Arrange
        var request = new TeamDTO(null, "Brazil", "https://example.com/flags/brazil.png");
        var response = new TeamDTO("team-1", "Brazil", "https://example.com/flags/brazil.png");

        when(teamService.createTeam(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("team-1"))
                .andExpect(jsonPath("$.name").value("Brazil"))
                .andExpect(jsonPath("$.flagUrl").value("https://example.com/flags/brazil.png"));
    }

    @Test
    @DisplayName("Create team with valid name only (no flag URL) - should return 201")
    void createTeam_ValidNameNoUrl_ReturnsCreated() throws Exception {
        // Arrange
        var request = new TeamDTO(null, "Brazil", null);
        var response = new TeamDTO("team-1", "Brazil", null);

        when(teamService.createTeam(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Brazil"));
    }

    // ============= CREATE TEAM - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Create team with blank name - should return 400")
    void createTeam_BlankName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO(null, "   ", "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create team with empty name - should return 400")
    void createTeam_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO(null, "", "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create team with null name - should return 400")
    void createTeam_NullName_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "flagUrl": "https://example.com/flags/brazil.png"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create team with name too short (1 character) - should return 400")
    void createTeam_NameTooShort_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO(null, "B", "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create team with name too long (>80 characters) - should return 400")
    void createTeam_NameTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longName = "A".repeat(81);
        var request = new TeamDTO(null, longName, "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create team with invalid flag URL format - should return 400")
    void createTeam_InvalidFlagUrlFormat_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO(null, "Brazil", "not-a-valid-url");

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create team with flag URL too long (>500 characters) - should return 400")
    void createTeam_FlagUrlTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longUrl = "https://example.com/" + "a".repeat(481);
        var request = new TeamDTO(null, "Brazil", longUrl);

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create team with multiple validation errors - should return 400")
    void createTeam_MultipleValidationErrors_ReturnsBadRequest() throws Exception {
        // Arrange
        var longName = "A".repeat(81);
        var request = new TeamDTO(null, longName, "invalid-url");

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ============= UPDATE TEAM - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Update team with blank name - should return 400")
    void updateTeam_BlankName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO("team-1", "   ", "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(put("/team/team-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update team with empty name - should return 400")
    void updateTeam_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO("team-1", "", "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(put("/team/team-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update team with name too short - should return 400")
    void updateTeam_NameTooShort_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO("team-1", "B", "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(put("/team/team-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update team with name too long - should return 400")
    void updateTeam_NameTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longName = "A".repeat(81);
        var request = new TeamDTO("team-1", longName, "https://example.com/flags/brazil.png");

        // Act + Assert
        mockMvc.perform(put("/team/team-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update team with invalid flag URL - should return 400")
    void updateTeam_InvalidFlagUrl_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new TeamDTO("team-1", "Brazil", "not-a-url");

        // Act + Assert
        mockMvc.perform(put("/team/team-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update team with flag URL too long - should return 400")
    void updateTeam_FlagUrlTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longUrl = "https://example.com/" + "a".repeat(481);
        var request = new TeamDTO("team-1", "Brazil", longUrl);

        // Act + Assert
        mockMvc.perform(put("/team/team-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update team with null name - should return 400")
    void updateTeam_NullName_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "flagUrl": "https://example.com/flags/brazil.png"
                }
                """;

        // Act + Assert
        mockMvc.perform(put("/team/team-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    // ============= GET TEAM - SUCCESS TESTS =============

    @Test
    @DisplayName("Get team with valid ID - should return 200")
    void getTeam_ValidId_ReturnsOk() throws Exception {
        // Arrange
        var response = new TeamDTO("team-1", "Brazil", "https://example.com/flags/brazil.png");

        when(teamService.findById(anyString())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/team/team-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("team-1"))
                .andExpect(jsonPath("$.name").value("Brazil"));
    }

    // ============= GET ALL TEAMS - SUCCESS TESTS =============

    @Test
    @DisplayName("Get all teams - should return 200 with list")
    void getAllTeams_ReturnsOk() throws Exception {
        // Arrange
        var teams = List.of(
                new TeamDTO("team-1", "Brazil", "https://example.com/flags/brazil.png"),
                new TeamDTO("team-2", "Germany", "https://example.com/flags/germany.png"),
                new TeamDTO("team-3", "France", "https://example.com/flags/france.png")
        );

        when(teamService.findAll()).thenReturn(teams);

        // Act + Assert
        mockMvc.perform(get("/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Brazil"))
                .andExpect(jsonPath("$[1].name").value("Germany"))
                .andExpect(jsonPath("$[2].name").value("France"));
    }

    // ============= BOUNDARY TESTS =============

    @Test
    @DisplayName("Create team with name exactly 2 characters - should return 201 (valid)")
    void createTeam_NameExactlyTwoCharacters_ReturnsCreated() throws Exception {
        // Arrange
        var request = new TeamDTO(null, "BR", "https://example.com/flags/brazil.png");
        var response = new TeamDTO("team-1", "BR", "https://example.com/flags/brazil.png");

        when(teamService.createTeam(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("BR"));
    }

    @Test
    @DisplayName("Create team with name exactly 80 characters - should return 201 (valid)")
    void createTeam_NameExactlyEightyCharacters_ReturnsCreated() throws Exception {
        // Arrange
        var name = "A".repeat(80);
        var request = new TeamDTO(null, name, "https://example.com/flags/test.png");
        var response = new TeamDTO("team-1", name, "https://example.com/flags/test.png");

        when(teamService.createTeam(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("Create team with flag URL exactly 500 characters - should return 201 (valid)")
    void createTeam_FlagUrlExactlyFiveHundredCharacters_ReturnsCreated() throws Exception {
        // Arrange
        var url = "https://example.com/" + "a".repeat(480);
        var request = new TeamDTO(null, "Brazil", url);
        var response = new TeamDTO("team-1", "Brazil", url);

        when(teamService.createTeam(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flagUrl").value(url));
    }
}

