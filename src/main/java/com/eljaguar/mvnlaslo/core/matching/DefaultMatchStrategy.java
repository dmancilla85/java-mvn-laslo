package com.eljaguar.mvnlaslo.core.matching;

import com.eljaguar.mvnlaslo.core.analysis.ComplementarityChecker;
import com.eljaguar.mvnlaslo.core.analysis.HairpinValidator;
import com.eljaguar.mvnlaslo.core.analysis.SequenceTransformer;
import com.eljaguar.mvnlaslo.core.model.MatchResult;
import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import com.eljaguar.mvnlaslo.core.model.StemLoopModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Default matching strategy that performs full hairpin matching.
 * Implements the Template Method pattern — defines the skeleton of the matching algorithm
 * while allowing subclasses to override specific steps.
 */
public class DefaultMatchStrategy implements MatchStrategy {

    protected final HairpinValidator validator;
    protected final ComplementarityChecker complementarityChecker;

    public DefaultMatchStrategy(HairpinValidator validator, ComplementarityChecker complementarityChecker) {
        this.validator = validator;
        this.complementarityChecker = complementarityChecker;
    }

    public DefaultMatchStrategy(MatchConfig config) {
        this(new HairpinValidator(config.minStemLength(), config.maxLoopLength(), config.minComplementarity()),
             new ComplementarityChecker(config.minComplementarity()));
    }

    @Override
    public String getName() {
        return "DefaultMatch";
    }

    @Override
    public MatchResult match(SequenceInfo query, Iterable<SequenceInfo> targetSequences, MatchConfig config) {
        long startTime = System.currentTimeMillis();
        List<StemLoopModel> allMatches = new ArrayList<>();
        int scanned = 0;

        try {
            for (SequenceInfo target : targetSequences) {
                scanned++;
                List<StemLoopModel> matches = findMatchesInTarget(query, target, config);
                allMatches.addAll(matches);
            }

            long elapsed = System.currentTimeMillis() - startTime;
            return MatchResult.builder(getName())
                    .addAllMatches(allMatches)
                    .totalSequencesScanned(scanned)
                    .totalMatchesFound(allMatches.size())
                    .executionTimeMs(elapsed)
                    .success(true)
                    .build();

        } catch (RuntimeException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return MatchResult.builder(getName())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .totalSequencesScanned(scanned)
                    .executionTimeMs(elapsed)
                    .build();
        }
    }

    /**
     * Finds matches between a query and a single target sequence.
     * Override in subclasses to customize matching behavior.
     */
    protected List<StemLoopModel> findMatchesInTarget(SequenceInfo query, SequenceInfo target, MatchConfig config) {
        List<StemLoopModel> matches = new ArrayList<>();
        String querySeq = query.getSequence();
        String targetSeq = target.getSequence();

        for (int i = 0; i <= querySeq.length() - config.minStemLength() * 2 - 3; i++) {
            for (int stemLen = config.minStemLength(); stemLen <= querySeq.length() / 2; stemLen++) {
                for (int loopLen = 3; loopLen <= config.maxLoopLength(); loopLen++) {
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
                                    .reversed(SequenceTransformer.reverse(stem2))
                                    .complement(SequenceTransformer.complement(stem2))
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
                                    .matchType(StemLoopModel.MatchType.FULL_MATCH)
                                    .build();
                            matches.add(model);
                        }
                    }
                }
            }
        }
        return matches;
    }
}
