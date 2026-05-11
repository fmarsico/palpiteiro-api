package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.ApproveMembershipDTO;
import com.caravela21.palpiteiro.api.controller.dto.JoinPoolDTO;
import com.caravela21.palpiteiro.api.controller.dto.PoolDTO;
import com.caravela21.palpiteiro.api.controller.dto.PoolMembershipDTO;
import com.caravela21.palpiteiro.api.enums.PoolMembershipStatus;
import com.caravela21.palpiteiro.api.service.PoolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoolController.class)
class PoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PoolService poolService;

    // ============= CREATE POOL - SUCCESS TEST =============

    @Test
    @DisplayName("Get members from pools where user is member but not owner - should return 200")
    void getMembersFromPoolsWhereUserIsMemberButNotOwner_ReturnsOk() throws Exception {
        // Arrange
        var response = new PoolMembershipDTO(
                "membership-1",
                "pool-1",
                "user-1",
                "User One",
                PoolMembershipStatus.APPROVED,
                null,
                null
        );
        when(poolService.getMembersFromPoolsWhereUserIsMemberButNotOwner("user-123")).thenReturn(java.util.List.of(response));

        // Act + Assert
        mockMvc.perform(get("/pool/member-of/user-123/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].poolId").value("pool-1"))
                .andExpect(jsonPath("$[0].userId").value("user-1"));
    }

    @Test
    @DisplayName("Create pool with valid data - should return 201")
    void createPool_ValidData_ReturnsCreated() throws Exception {
        // Arrange
        var request = new PoolDTO(null, "Copa do Mundo 2026", null, "owner-123");
        var response = new PoolDTO("pool-1", "Copa do Mundo 2026", "ABC12345", "owner-123");

        when(poolService.createPool(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/pool")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("pool-1"))
                .andExpect(jsonPath("$.name").value("Copa do Mundo 2026"));
    }

    // ============= CREATE POOL - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Create pool with blank name - should return 400")
    void createPool_BlankName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PoolDTO(null, "   ", null, "owner-123");

        // Act + Assert
        mockMvc.perform(post("/pool")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create pool with empty name - should return 400")
    void createPool_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PoolDTO(null, "", null, "owner-123");

        // Act + Assert
        mockMvc.perform(post("/pool")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create pool with name too short - should return 400")
    void createPool_NameTooShort_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PoolDTO(null, "AB", null, "owner-123");

        // Act + Assert
        mockMvc.perform(post("/pool")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create pool with name too long - should return 400")
    void createPool_NameTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        var longName = "A".repeat(101);
        var request = new PoolDTO(null, longName, null, "owner-123");

        // Act + Assert
        mockMvc.perform(post("/pool")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create pool with null name - should return 400")
    void createPool_NullName_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "ownerId": "owner-123"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/pool")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    // ============= REQUEST POOL ACCESS - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Request pool access with blank pool ID - should return 400")
    void requestPoolAccess_BlankPoolId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new JoinPoolDTO("   ", "user-123");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request pool access with blank user ID - should return 400")
    void requestPoolAccess_BlankUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new JoinPoolDTO("pool-1", "   ");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request pool access with empty pool ID - should return 400")
    void requestPoolAccess_EmptyPoolId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new JoinPoolDTO("", "user-123");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request pool access with empty user ID - should return 400")
    void requestPoolAccess_EmptyUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new JoinPoolDTO("pool-1", "");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request pool access with null pool ID - should return 400")
    void requestPoolAccess_NullPoolId_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "userId": "user-123"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request pool access with null user ID - should return 400")
    void requestPoolAccess_NullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "poolId": "pool-1"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    // ============= APPROVE MEMBERSHIP - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Approve membership with blank user ID - should return 400")
    void approveMembership_BlankUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new ApproveMembershipDTO("   ");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/approve-member?ownerId=owner-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Approve membership with empty user ID - should return 400")
    void approveMembership_EmptyUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new ApproveMembershipDTO("");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/approve-member?ownerId=owner-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Approve membership with null user ID - should return 400")
    void approveMembership_NullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {}
                """;

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/approve-member?ownerId=owner-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    // ============= REJECT MEMBERSHIP - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Reject membership with blank user ID - should return 400")
    void rejectMembership_BlankUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new ApproveMembershipDTO("   ");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/reject-member?ownerId=owner-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Reject membership with empty user ID - should return 400")
    void rejectMembership_EmptyUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new ApproveMembershipDTO("");

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/reject-member?ownerId=owner-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Reject membership with null user ID - should return 400")
    void rejectMembership_NullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {}
                """;

        // Act + Assert
        mockMvc.perform(post("/pool/pool-1/reject-member?ownerId=owner-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }
}

