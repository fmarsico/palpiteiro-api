package com.caravela21.palpiteiro.api.service;

import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    public int calculatePoints(int predictedHomeScore, int predictedAwayScore, int actualHomeScore, int actualAwayScore) {
        boolean exactScore = predictedHomeScore == actualHomeScore && predictedAwayScore == actualAwayScore;
        if (exactScore) {
            return 4;
        }

        int predictedResult = Integer.compare(predictedHomeScore, predictedAwayScore);
        int actualResult = Integer.compare(actualHomeScore, actualAwayScore);
        return predictedResult == actualResult ? 1 : 0;
    }

    public boolean isExactScore(int predictedHomeScore, int predictedAwayScore, int actualHomeScore, int actualAwayScore) {
        return predictedHomeScore == actualHomeScore && predictedAwayScore == actualAwayScore;
    }
}

