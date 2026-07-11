package com.eljaguar.mvnlaslo.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchResultTest {

    @Test
    void testBuilder() {
        StemLoopModel match = StemLoopModel.builder()
                .sequence("ATCG")
                .stemStart(0)
                .stemEnd(2)
                .loopStart(2)
                .loopEnd(4)
                .stemLength(2)
                .loopLength(2)
                .complementarityRatio(0.9)
                .matchType(StemLoopModel.MatchType.FULL_MATCH)
                .build();

        MatchResult result = MatchResult.builder("TestStrategy")
                .addMatch(match)
                .totalSequencesScanned(10)
                .totalMatchesFound(1)
                .executionTimeMs(100)
                .success(true)
                .build();

        assertEquals("TestStrategy", result.getStrategyName());
        assertEquals(1, result.getTotalMatchesFound());
        assertEquals(10, result.getTotalSequencesScanned());
        assertEquals(100, result.getExecutionTimeMs());
        assertTrue(result.isSuccess());
        assertTrue(result.hasMatches());
        assertEquals(1, result.getMatches().size());
    }

    @Test
    void testEmptyResult() {
        MatchResult result = MatchResult.empty("TestStrategy");
        
        assertFalse(result.hasMatches());
        assertEquals(0, result.getTotalMatchesFound());
        assertTrue(result.isSuccess());
    }

    @Test
    void testFailureResult() {
        MatchResult result = MatchResult.failure("TestStrategy", "Test error");
        
        assertFalse(result.isSuccess());
        assertEquals("Test error", result.getErrorMessage());
        assertFalse(result.hasMatches());
    }

    @Test
    void testToString() {
        MatchResult result = MatchResult.builder("TestStrategy")
                .totalSequencesScanned(10)
                .totalMatchesFound(5)
                .executionTimeMs(100)
                .success(true)
                .build();

        String str = result.toString();
        assertTrue(str.contains("TestStrategy"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("10"));
    }
}
