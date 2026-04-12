package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.UserDTO;
import com.caravela21.palpiteiro.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // ============= CREATE USER - SUCCESS TESTS =============

    @Test
    @DisplayName("Create user with valid data - should return 200")
    void createUser_ValidData_ReturnsOk() throws Exception {
        // Arrange
        var request = new UserDTO(null, "João", "Silva", "joao@example.com", "https://example.com/photo.jpg");
        var response = new UserDTO("user-123", "João", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        when(userService.createUser(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-123"))
                .andExpect(jsonPath("$.name").value("João"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    @DisplayName("Create user with minimal valid data (no lastname, no photo) - should return 200")
    void createUser_MinimalData_ReturnsOk() throws Exception {
        // Arrange
        var request = new UserDTO(null, "João", null, "joao@example.com", null);
        var response = new UserDTO("user-123", "João", null, "joao@example.com", null);

        when(userService.createUser(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João"));
    }

    // ============= CREATE USER - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Create user with blank name - should return 400")
    void createUser_BlankName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO(null, "   ", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with empty name - should return 400")
    void createUser_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO(null, "", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with null name - should return 400")
    void createUser_NullName_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "lastname": "Silva",
                    "email": "joao@example.com"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with name too short (1 character) - should return 400")
    void createUser_NameTooShort_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO(null, "J", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with name too long (31 characters) - should return 400")
    void createUser_NameTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longName = "A".repeat(31);
        var request = new UserDTO(null, longName, "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with blank email - should return 400")
    void createUser_BlankEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO(null, "João", "Silva", "   ", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with null email - should return 400")
    void createUser_NullEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "name": "João",
                    "lastname": "Silva"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with invalid email format - should return 400")
    void createUser_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO(null, "João", "Silva", "invalid-email", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with email without @domain - should return 400")
    void createUser_EmailWithoutDomain_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO(null, "João", "Silva", "joao@", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with email too long (256 characters) - should return 400")
    void createUser_EmailTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longEmail = "a".repeat(250) + "@example.com";
        var request = new UserDTO(null, "João", "Silva", longEmail, "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with lastname too long (51 characters) - should return 400")
    void createUser_LastnameTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longLastname = "A".repeat(51);
        var request = new UserDTO(null, "João", longLastname, "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with invalid photo URL - should return 400")
    void createUser_InvalidPhotoUrl_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO(null, "João", "Silva", "joao@example.com", "not-a-valid-url");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with photo URL too long (501 characters) - should return 400")
    void createUser_PhotoUrlTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longUrl = "https://example.com/" + "a".repeat(481);
        var request = new UserDTO(null, "João", "Silva", "joao@example.com", longUrl);

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create user with multiple validation errors - should return 400")
    void createUser_MultipleValidationErrors_ReturnsBadRequest() throws Exception {
        // Arrange
        var longName = "A".repeat(31);
        var request = new UserDTO(null, longName, "A".repeat(51), "invalid-email", "not-a-url");

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ============= UPDATE USER - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Update user with blank name - should return 400")
    void updateUser_BlankName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO("user-123", "   ", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user with empty name - should return 400")
    void updateUser_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO("user-123", "", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user with name too short - should return 400")
    void updateUser_NameTooShort_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO("user-123", "J", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user with name too long - should return 400")
    void updateUser_NameTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longName = "A".repeat(31);
        var request = new UserDTO("user-123", longName, "Silva", "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user with null email - should return 400")
    void updateUser_NullEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "id": "user-123",
                    "name": "João",
                    "lastname": "Silva"
                }
                """;

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user with invalid email - should return 400")
    void updateUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO("user-123", "João", "Silva", "not-an-email", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user with invalid photo URL - should return 400")
    void updateUser_InvalidPhotoUrl_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new UserDTO("user-123", "João", "Silva", "joao@example.com", "invalid-url");

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user with lastname too long - should return 400")
    void updateUser_LastnameTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longLastname = "A".repeat(51);
        var request = new UserDTO("user-123", "João", longLastname, "joao@example.com", "https://example.com/photo.jpg");

        // Act + Assert
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ============= BOUNDARY TESTS =============

    @Test
    @DisplayName("Create user with name exactly 2 characters - should return 200 (valid)")
    void createUser_NameExactlyTwoCharacters_ReturnsOk() throws Exception {
        // Arrange
        var request = new UserDTO(null, "Jo", "Silva", "joao@example.com", "https://example.com/photo.jpg");
        var response = new UserDTO("user-123", "Jo", "Silva", "joao@example.com", "https://example.com/photo.jpg");

        when(userService.createUser(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jo"));
    }

    @Test
    @DisplayName("Create user with name exactly 30 characters - should return 200 (valid)")
    void createUser_NameExactlyThirtyCharacters_ReturnsOk() throws Exception {
        // Arrange
        var name = "A".repeat(30);
        var request = new UserDTO(null, name, "Silva", "joao@example.com", "https://example.com/photo.jpg");
        var response = new UserDTO("user-123", name, "Silva", "joao@example.com", "https://example.com/photo.jpg");

        when(userService.createUser(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("Create user with lastname exactly 50 characters - should return 200 (valid)")
    void createUser_LastnameExactlyFiftyCharacters_ReturnsOk() throws Exception {
        // Arrange
        var lastname = "A".repeat(50);
        var request = new UserDTO(null, "João", lastname, "joao@example.com", "https://example.com/photo.jpg");
        var response = new UserDTO("user-123", "João", lastname, "joao@example.com", "https://example.com/photo.jpg");

        when(userService.createUser(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastname").value(lastname));
    }

    @Test
    @DisplayName("Create user with photo URL exactly 500 characters - should return 200 (valid)")
    void createUser_PhotoUrlExactlyFiveHundredCharacters_ReturnsOk() throws Exception {
        // Arrange
        var url = "https://example.com/" + "a".repeat(480);
        var request = new UserDTO(null, "João", "Silva", "joao@example.com", url);
        var response = new UserDTO("user-123", "João", "Silva", "joao@example.com", url);

        when(userService.createUser(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value(url));
    }
}



