package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EvaluationTest {

    @Test
    void getTotalScore_withAllScores_returnsAverage() {
        Evaluation e = new Evaluation();
        e.setBackendQuality(10);
        e.setDatabaseScore(8);
        e.setFrontendQuality(9);
        e.setFunctionalityScore(7);
        e.setUsabilityScore(6);
        e.setMustHaveCompleteness(8);
        assertEquals(8.0, e.getTotalScore(), 0.001);
    }

    @Test
    void getTotalScore_withPartialScores_returnsAverageOfNonNull() {
        Evaluation e = new Evaluation();
        e.setBackendQuality(10);
        e.setDatabaseScore(6);
        e.setFrontendQuality(8);
        assertEquals(8.0, e.getTotalScore(), 0.001);
    }

    @Test
    void getTotalScore_withSingleScore_returnsThatScore() {
        Evaluation e = new Evaluation();
        e.setBackendQuality(7);
        assertEquals(7.0, e.getTotalScore(), 0.001);
    }

    @Test
    void getTotalScore_withAllNull_returnsZero() {
        Evaluation e = new Evaluation();
        assertEquals(0.0, e.getTotalScore(), 0.001);
    }

    @Test
    void getTotalScore_withNullMixedWithValues() {
        Evaluation e = new Evaluation();
        e.setBackendQuality(5);
        e.setDatabaseScore(10);
        e.setFrontendQuality(null);
        e.setFunctionalityScore(null);
        e.setUsabilityScore(null);
        e.setMustHaveCompleteness(null);
        assertEquals(7.5, e.getTotalScore(), 0.001);
    }

    @Test
    void getTotalScore_roundToTwoDecimals() {
        Evaluation e = new Evaluation();
        e.setBackendQuality(7);
        e.setDatabaseScore(8);
        assertEquals(7.5, e.getTotalScore(), 0.001);
    }
}
