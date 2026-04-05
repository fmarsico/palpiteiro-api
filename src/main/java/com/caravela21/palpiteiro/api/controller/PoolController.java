package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.ApproveMembershipDTO;
import com.caravela21.palpiteiro.api.controller.dto.JoinPoolDTO;
import com.caravela21.palpiteiro.api.controller.dto.PoolDTO;
import com.caravela21.palpiteiro.api.controller.dto.PoolMembershipDTO;
import com.caravela21.palpiteiro.api.service.PoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pool")
@RequiredArgsConstructor
public class PoolController {

    private final PoolService poolService;

    @Operation(
            summary = "Create a new pool",
            description = "Creates a new pool with the specified owner."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pool successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or owner not found"),
    })
    @PostMapping
    public ResponseEntity<PoolDTO> createPool(@RequestBody @Valid PoolDTO poolDTO) {
        PoolDTO created = poolService.createPool(poolDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Find pool by invite code",
            description = "Returns pool details for a given invite code. Used by users to find a pool before requesting access."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pool found"),
            @ApiResponse(responseCode = "400", description = "No pool found for this invite code")
    })
    @GetMapping("/invite/{inviteCode}")
    public ResponseEntity<PoolDTO> findByInviteCode(@PathVariable String inviteCode) {
        PoolDTO pool = poolService.findByInviteCode(inviteCode);
        return ResponseEntity.ok().body(pool);
    }

    @Operation(
            summary = "Request access to a pool",
            description = "User requests to join an existing pool. The pool owner must approve the request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Access request successfully created (PENDING)"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or user already requested"),
            @ApiResponse(responseCode = "404", description = "Pool or user not found")
    })
    @PostMapping("/{poolId}/request-access")
    public ResponseEntity<PoolMembershipDTO> requestPoolAccess(
            @PathVariable String poolId,
            @RequestBody @Valid JoinPoolDTO joinPoolDTO) {
        PoolMembershipDTO membership = poolService.requestPoolAccess(poolId, joinPoolDTO.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(membership);
    }

    @Operation(
            summary = "Get all memberships of a pool",
            description = "Returns all memberships (PENDING, APPROVED, REJECTED) of a pool. Only the pool owner can view this."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all memberships"),
            @ApiResponse(responseCode = "403", description = "Only pool owner can view all memberships"),
            @ApiResponse(responseCode = "404", description = "Pool not found")
    })
    @GetMapping("/{poolId}/memberships")
    public ResponseEntity<List<PoolMembershipDTO>> getAllMemberships(
            @PathVariable String poolId,
            @RequestParam String ownerId) {
        List<PoolMembershipDTO> memberships = poolService.getAllMemberships(poolId, ownerId);
        return ResponseEntity.ok().body(memberships);
    }

    @Operation(
            summary = "Get all pools owned by a user",
            description = "Returns all pools where the user is the owner."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of pools owned by the user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/owned-by/{ownerId}")
    public ResponseEntity<List<PoolDTO>> getPoolsByOwner(@PathVariable String ownerId) {
        List<PoolDTO> pools = poolService.getPoolsByOwner(ownerId);
        return ResponseEntity.ok().body(pools);
    }

    @Operation(
            summary = "Get pending membership requests",
            description = "Get all pending membership requests for a pool. Only the pool owner can view this."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of pending membership requests"),
            @ApiResponse(responseCode = "403", description = "Only pool owner can view this"),
            @ApiResponse(responseCode = "404", description = "Pool not found")
    })
    @GetMapping("/{poolId}/pending-requests")
    public ResponseEntity<List<PoolMembershipDTO>> getPendingRequests(
            @PathVariable String poolId,
            @RequestParam String ownerId) {
        List<PoolMembershipDTO> requests = poolService.getPendingMembershipRequests(poolId, ownerId);
        return ResponseEntity.ok().body(requests);
    }

    @Operation(
            summary = "Approve membership request",
            description = "Owner approves a user's membership request for the pool."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership request successfully approved"),
            @ApiResponse(responseCode = "400", description = "Invalid request or membership not pending"),
            @ApiResponse(responseCode = "403", description = "Only pool owner can approve requests"),
            @ApiResponse(responseCode = "404", description = "Pool or membership not found")
    })
    @PostMapping("/{poolId}/approve-member")
    public ResponseEntity<PoolMembershipDTO> approveMembership(
            @PathVariable String poolId,
            @RequestBody @Valid ApproveMembershipDTO approveMembershipDTO,
            @RequestParam String ownerId) {
        PoolMembershipDTO membership = poolService.approveMembershipRequest(poolId, approveMembershipDTO.userId(), ownerId);
        return ResponseEntity.ok().body(membership);
    }

    @Operation(
            summary = "Reject membership request",
            description = "Owner rejects a user's membership request for the pool."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership request successfully rejected"),
            @ApiResponse(responseCode = "400", description = "Invalid request or membership not pending"),
            @ApiResponse(responseCode = "403", description = "Only pool owner can reject requests"),
            @ApiResponse(responseCode = "404", description = "Pool or membership not found")
    })
    @PostMapping("/{poolId}/reject-member")
    public ResponseEntity<PoolMembershipDTO> rejectMembership(
            @PathVariable String poolId,
            @RequestBody @Valid ApproveMembershipDTO approveMembershipDTO,
            @RequestParam String ownerId) {
        PoolMembershipDTO membership = poolService.rejectMembershipRequest(poolId, approveMembershipDTO.userId(), ownerId);
        return ResponseEntity.ok().body(membership);
    }

    @Operation(
            summary = "Get pool members",
            description = "Get all approved members of a pool."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of approved members"),
            @ApiResponse(responseCode = "404", description = "Pool not found")
    })
    @GetMapping("/{poolId}/members")
    public ResponseEntity<List<PoolMembershipDTO>> getPoolMembers(@PathVariable String poolId) {
        List<PoolMembershipDTO> members = poolService.getPoolMembers(poolId);
        return ResponseEntity.ok().body(members);
    }

    @Operation(
            summary = "Remove a member from the pool",
            description = "Owner removes an approved member from the pool. Cannot remove self."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member successfully removed"),
            @ApiResponse(responseCode = "400", description = "Invalid request or cannot remove owner"),
            @ApiResponse(responseCode = "403", description = "Only pool owner can remove members"),
            @ApiResponse(responseCode = "404", description = "Pool or member not found")
    })
    @DeleteMapping("/{poolId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String poolId,
            @PathVariable String userId,
            @RequestParam String ownerId) {
        poolService.removeMember(poolId, userId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get pool details",
            description = "Retrieves the details of a specific pool."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pool found"),
            @ApiResponse(responseCode = "404", description = "Pool not found")
    })
    @GetMapping("/{poolId}")
    public ResponseEntity<PoolDTO> getPool(@PathVariable String poolId) {
        PoolDTO pool = poolService.getPoolById(poolId);
        return ResponseEntity.ok().body(pool);
    }
}

