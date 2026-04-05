package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.PredictionDTO;
import com.caravela21.palpiteiro.api.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prediction")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @Operation(
            summary = "Create or update a prediction",
            description = "Creates a new prediction or updates the existing one for the same user, pool and match."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prediction saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or prediction deadline expired"),
            @ApiResponse(responseCode = "404", description = "User, pool, or match not found")
    })
    @PostMapping
    public ResponseEntity<PredictionDTO> upsertPrediction(@RequestBody @Valid PredictionDTO predictionDTO) {
        return ResponseEntity.ok(predictionService.upsertPrediction(predictionDTO));
    }
}

