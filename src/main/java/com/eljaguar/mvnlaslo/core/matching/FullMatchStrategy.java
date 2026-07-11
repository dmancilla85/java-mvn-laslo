package com.eljaguar.mvnlaslo.core.matching;

import com.eljaguar.mvnlaslo.core.analysis.ComplementarityChecker;
import com.eljaguar.mvnlaslo.core.analysis.HairpinValidator;
import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import com.eljaguar.mvnlaslo.core.model.StemLoopModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Full matching strategy that searches both query and target for complementary regions.
 * Extends DefaultMatchStrategy with additional target-scanning logic.
 */
public class FullMatchStrategy extends DefaultMatchStrategy {

    public FullMatchStrategy(HairpinValidator validator, ComplementarityChecker complementarityChecker) {
        super(validator, complementarityChecker);
    }

    public FullMatchStrategy(MatchConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "FullMatch";
    }

    @Override
    protected List<StemLoopModel> findMatchesInTarget(SequenceInfo query, SequenceInfo target, MatchConfig config) {
        List<StemLoopModel> matches = new ArrayList<>();

        // First, find matches in the query sequence
        matches.addAll(super.findMatchesInTarget(query, target, config));

        // Then, find matches in the target sequence against the query
        if (!query.getSequence().equals(target.getSequence())) {
            matches.addAll(findCrossMatches(query, target, config));
        }

        return matches;
    }

    private List<StemLoopModel> findCrossMatches(SequenceInfo query, SequenceInfo target, MatchConfig config) {
        List<StemLoopModel> matches = new ArrayList<>();
        String querySeq = query.getSequence();
        String targetSeq = target.getSequence();

        // Search for complementary regions between query and target
        for (int qStart = 0; qStart <= querySeq.length() - config.minStemLength(); qStart++) {
            for (int tStart = 0; tStart <= targetSeq.length() - config.minStemLength(); tStart++) {
                for (int len = config.minStemLength(); len <= Math.min(querySeq.length() - qStart, targetSeq.length() - tStart); len++) {
                    String qRegion = querySeq.substring(qStart, qStart + len);
                    String tRegion = targetSeq.substring(tStart, tStart + len);

                    double ratio = complementarityChecker.calculateRatio(qRegion, tRegion);
                    if (ratio >= config.minComplementarity()) {
                        StemLoopModel model = StemLoopModel.builder()
                                .sequence(querySeq)
                                .reversed(tRegion)
                                .complement(ratio > 0.5 ? tRegion : "")
                                .stemStart(qStart)
                                .stemEnd(qStart + len)
                                .loopStart(qStart + len)
                                .loopEnd(qStart + len)
                                .loopLength(0)
                                .stemLength(len)
                                .complementarityRatio(ratio)
                                .species(target.getSpecies())
                                .geneName(target.getGeneName())
                                .transcriptId(target.getTranscriptId())
                                .header(target.getHeader())
                                .matchType(StemLoopModel.MatchType.PARTIAL_MATCH)
                                .build();
                        matches.add(model);
                    }
                }
            }
        }
        return matches;
    }
}
