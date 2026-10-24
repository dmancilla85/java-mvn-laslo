package com.eljaguar.mvnlaslo.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StemLoopModelTest {

    @Test
    void testBuilder() {
        StemLoopModel model = StemLoopModel.builder()
                .id("test-id")
                .sequence("ATCGATCG")
                .reversed("GCTAGCTA")
                .complement("TAGCTAGC")
                .stemStart(0)
                .stemEnd(4)
                .loopStart(4)
                .loopEnd(8)
                .stemLength(4)
                .loopLength(4)
                .complementarityRatio(0.95)
                .species("Homo sapiens")
                .geneName("TEST")
                .transcriptId("ENST000001")
                .header(">test")
                .matchType(StemLoopModel.MatchType.FULL_MATCH)
                .build();

        assertEquals("test-id", model.getId());
        assertEquals("ATCGATCG", model.getSequence());
        assertEquals(0, model.getStemStart());
        assertEquals(4, model.getStemEnd());
        assertEquals(4, model.getLoopStart());
        assertEquals(8, model.getLoopEnd());
        assertEquals(4, model.getLoopLength());
        assertEquals(4, model.getStemLength());
        assertEquals(0.95, model.getComplementarityRatio(), 0.001);
        assertEquals("Homo sapiens", model.getSpecies());
        assertEquals("TEST", model.getGeneName());
        assertEquals(StemLoopModel.MatchType.FULL_MATCH, model.getMatchType());
    }

    @Test
    void testGetFullSequence() {
        StemLoopModel model = StemLoopModel.builder()
                .sequence("ATCGATCG")
                .stemStart(0)
                .stemEnd(4)
                .loopStart(4)
                .loopEnd(8)
                .build();

        assertEquals("ATCGATCG", model.getFullSequence());
    }

    @Test
    void testGetStemSequence() {
        StemLoopModel model = StemLoopModel.builder()
                .sequence("ATCGATCG")
                .stemStart(0)
                .stemEnd(4)
                .loopStart(4)
                .loopEnd(8)
                .build();

        assertEquals("ATCG", model.getStemSequence());
    }

    @Test
    void testGetLoopSequence() {
        StemLoopModel model = StemLoopModel.builder()
                .sequence("ATCGATCG")
                .stemStart(0)
                .stemEnd(4)
                .loopStart(4)
                .loopEnd(8)
                .build();

        assertEquals("ATCG", model.getLoopSequence());
    }

    @Test
    void testEquals() {
        StemLoopModel model1 = StemLoopModel.builder()
                .sequence("ATCG")
                .stemStart(0)
                .stemEnd(2)
                .loopStart(2)
                .loopEnd(4)
                .build();

        StemLoopModel model2 = StemLoopModel.builder()
                .sequence("ATCG")
                .stemStart(0)
                .stemEnd(2)
                .loopStart(2)
                .loopEnd(4)
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testNotEquals() {
        StemLoopModel model1 = StemLoopModel.builder()
                .sequence("ATCG")
                .stemStart(0)
                .stemEnd(2)
                .loopStart(2)
                .loopEnd(4)
                .build();

        StemLoopModel model2 = StemLoopModel.builder()
                .sequence("ATCG")
                .stemStart(0)
                .stemEnd(3)
                .loopStart(3)
                .loopEnd(4)
                .build();

        assertNotEquals(model1, model2);
    }

    @Test
    void testToString() {
        StemLoopModel model = StemLoopModel.builder()
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

        String str = model.toString();
        assertTrue(str.contains("StemLoopModel"));
        assertTrue(str.contains("0-2"));
        assertTrue(str.contains("2-4"));
        assertTrue(str.contains("FULL_MATCH"));
    }
}
