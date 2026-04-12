package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.PredictionBatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.PredictionItemDTO;
import com.caravela21.palpiteiro.api.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @Operation(
            summary = "Create predictions",
            description = "Creates one or more predictions for the same user and pool in a single request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Predictions created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data, prediction already exists, or deadline expired"),
            @ApiResponse(responseCode = "404", description = "User, pool, or match not found")
    })
    @PostMapping
    public ResponseEntity<List<PredictionItemDTO>> createPredictions(@RequestBody @Valid PredictionBatchDTO batchDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(predictionService.createPredictions(batchDTO));
    }

    @Operation(
            summary = "Update predictions",
            description = "Updates one or more existing predictions for the same user and pool in a single request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Predictions updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or prediction deadline expired"),
            @ApiResponse(responseCode = "404", description = "User, pool, or match not found")
    })
    @PutMapping
    public ResponseEntity<List<PredictionItemDTO>> updatePredictions(@RequestBody @Valid PredictionBatchDTO batchDTO) {
        return ResponseEntity.ok(predictionService.updatePredictions(batchDTO));
    }
}


