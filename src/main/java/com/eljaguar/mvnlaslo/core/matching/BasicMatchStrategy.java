package com.eljaguar.mvnlaslo.core.matching;

import com.eljaguar.mvnlaslo.core.analysis.ComplementarityChecker;
import com.eljaguar.mvnlaslo.core.analysis.HairpinValidator;
import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import com.eljaguar.mvnlaslo.core.model.StemLoopModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic matching strategy that only checks for simple hairpin structures.
 * Uses relaxed criteria compared to the full match strategy.
 */
public class BasicMatchStrategy extends DefaultMatchStrategy {

    public BasicMatchStrategy(HairpinValidator validator, ComplementarityChecker complementarityChecker) {
        super(validator, complementarityChecker);
    }

    public BasicMatchStrategy(MatchConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "BasicMatch";
    }

    @Override
    protected List<StemLoopModel> findMatchesInTarget(SequenceInfo query, SequenceInfo target, MatchConfig config) {
        List<StemLoopModel> matches = new ArrayList<>();
        String querySeq = query.getSequence();

        // Only search for hairpins in the query sequence (no cross-matching)
        for (int i = 0; i <= querySeq.length() - config.minStemLength() * 2 - 3; i++) {
            int stemLen = config.minStemLength();
            int loopLen = 3; // Fixed small loop for basic matching

            int stem1End = i + stemLen;
            int loopStart = stem1End;
            int loopEnd = loopStart + loopLen;
            int stem2Start = loopEnd;
            int stem2End = stem2Start + stemLen;

            if (stem2End > querySeq.length()) {
                continue;
            }

            if (validator.isValidHairpin(querySeq, i, stem1End, loopStart, loopEnd)) {
                String stem1 = querySeq.substring(i, stem1End);
                String stem2 = querySeq.substring(stem2Start, stem2End);

                double ratio = complementarityChecker.calculateRatio(stem1, stem2);
                if (ratio >= config.minComplementarity()) {
                    StemLoopModel model = StemLoopModel.builder()
                            .sequence(querySeq)
                            .reversed(stem2)
                            .complement(ratio > 0.5 ? stem2 : "")
                            .stemStart(i)
                            .stemEnd(stem2End)
                            .loopStart(loopStart)
                            .loopEnd(loopEnd)
                            .loopLength(loopLen)
                            .stemLength(stemLen)
                            .complementarityRatio(ratio)
                            .species(query.getSpecies())
                            .geneName(query.getGeneName())
                            .transcriptId(query.getTranscriptId())
                            .header(query.getHeader())
                            .matchType(StemLoopModel.MatchType.BASIC_MATCH)
                            .build();
                    matches.add(model);
                }
            }
        }
        return matches;
    }
}
