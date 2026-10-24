/*
 * Copyright (C) 2018 David A. Mancilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.eljaguar.mvnlaslo.core;

import com.eljaguar.mvnlaslo.io.BioMartFasta;
import com.eljaguar.mvnlaslo.io.EnsemblFasta;
import com.eljaguar.mvnlaslo.io.FlyBaseFasta;
import com.eljaguar.mvnlaslo.io.GenBank;
import com.eljaguar.mvnlaslo.io.Generic;
import com.eljaguar.mvnlaslo.io.InputSequence;
import com.eljaguar.mvnlaslo.io.SourceFile;
import com.eljaguar.mvnlaslo.io.Vienna;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.eljaguar.mvnlaslo.core.SequenceAnalizer.reverseSequence;
import static java.lang.System.out;

/**
 * Main class for stem-loops
 *
 * @author David A. Mancilla
 * @version 1.0
 * @since 2016-12-13
 */
public class StemLoop {

    private static int maxPatternLength;
    private static boolean hasAdditionalSequence;
    private static boolean hasTwoSenses;
    private final List<BaseVariable> patternVariables;
    private SourceFile idFasta;
    private InputSequence mode;
    private String rnaHairpinSequence;
    private String loop;
    private String hairpinStructure;
    private String additional5Seq;
    private String additional3Seq;
    private String loopPattern;
    private String viennaStructure;
    private int sequenceLength;
    private int startsAt;
    private int endsAt;
    private int bulge;
    private int internalLoops;
    private char predecessor2Loop;
    private char predecessorLoop;
    private double percentAg;
    private double percentGu;
    private double percentCg;
    private double percentAu;
    private double percASequence;
    private double percGSequence;
    private double percCSequence;
    private double percUSequence;
    private double relativePos;
    private boolean reversed;
    private double mfe;
    private List<Integer> additionalSeqLocations;

    /**
     *
     * @param mode
     */
    public StemLoop(InputSequence mode) {
        super();
        this.mode = mode;

        switch (mode) {
            case ENSEMBL:
                this.idFasta = new EnsemblFasta();
                break;

            case FLYBASE:
                this.idFasta = new FlyBaseFasta();
                break;

            case BIOMART:
                this.idFasta = new BioMartFasta();
                break;

            case GENERIC:
                this.idFasta = new Generic();
                break;

            case GENBANK:
                this.idFasta = new GenBank();
                break;

            case VIENNA:
                this.idFasta = new Vienna();
                break;

        }

        this.loop = ""; //$NON-NLS-1$
        this.rnaHairpinSequence = null;
        this.sequenceLength = 0;
        this.percentAg = 0;
        this.percentGu = 0;
        this.percentCg = 0;
        this.percentAu = 0;
        this.percASequence = 0;
        this.percCSequence = 0;
        this.percGSequence = 0;
        this.percUSequence = 0;
        this.loopPattern = "";
        this.endsAt = 0;
        this.startsAt = 0;
        this.bulge = 0;
        this.reversed = false;
        this.internalLoops = 0;
        this.relativePos = 0.0;
        this.predecessorLoop = 0;
        this.predecessor2Loop = 0;
        this.mfe = (float) 0.0;
        this.viennaStructure = "";
        this.additional5Seq = "";
        this.additional3Seq = "";
        this.additionalSeqLocations = new ArrayList<>();
        this.patternVariables = new ArrayList<>();
    }

    /**
     *
     * @param mode
     * @return
     */
    public static String getHeader(InputSequence mode) {

        String header = "";
        String nVariablesTable = "";
        String additionalSequenceCol = "";
        String senseColumn = "";

        switch (mode) {
            case ENSEMBL:
                header = EnsemblFasta.getHeader();
                break;

            case FLYBASE:
                header = FlyBaseFasta.getHeader();
                break;

            case BIOMART:
                header = BioMartFasta.getHeader();
                break;

            case GENBANK:
                header = GenBank.getHeader();
                break;

            case GENERIC:
                header = Generic.getHeader();
                break;

            case VIENNA:
                header = "";
        }

        for (int i = 0; i < getMaxPatternLength(); i++) {
            nVariablesTable += "N" + i + SourceFile.ROW_DELIMITER;
        }

        if (isHasAdditionalSequence()) {
            additionalSequenceCol = "AdditionalSeqMatches" + SourceFile.ROW_DELIMITER
                    + "AdditionalSeqPositions" + SourceFile.ROW_DELIMITER;
        }

        if (isHasTwoSenses()) {
            senseColumn = "Sense" + SourceFile.ROW_DELIMITER;
        }

        return header
                + "LoopPattern" + SourceFile.ROW_DELIMITER
                + "TerminalPair" + SourceFile.ROW_DELIMITER
                + "N-2" + SourceFile.ROW_DELIMITER
                + "N-1" + SourceFile.ROW_DELIMITER
                + "Loop" + SourceFile.ROW_DELIMITER
                + "StemLoopSequence" + SourceFile.ROW_DELIMITER
                + "Additional5Seq" + SourceFile.ROW_DELIMITER
                + "Additional3Seq" + SourceFile.ROW_DELIMITER
                + "PredictedStructure" + SourceFile.ROW_DELIMITER
                + "ViennaBracketStr" + SourceFile.ROW_DELIMITER
                + "Pairments" + SourceFile.ROW_DELIMITER
                + "WooblePairs" + SourceFile.ROW_DELIMITER
                + "Bulges" + SourceFile.ROW_DELIMITER
                + "InternalLoops" + SourceFile.ROW_DELIMITER
                + "SequenceLength" + SourceFile.ROW_DELIMITER
                + "StartsAt" + SourceFile.ROW_DELIMITER
                + "EndsAt" + SourceFile.ROW_DELIMITER
                + "A_PercentSequence" + SourceFile.ROW_DELIMITER
                + "C_PercentSequence" + SourceFile.ROW_DELIMITER
                + "G_PercentSequence" + SourceFile.ROW_DELIMITER
                + "U_PercentSequence" + SourceFile.ROW_DELIMITER
                + "AU_PercentPairs" + SourceFile.ROW_DELIMITER
                + "CG_PercentPairs" + SourceFile.ROW_DELIMITER
                + "GU_PercentPairs" + SourceFile.ROW_DELIMITER
                + "PurinePercentPairs" + SourceFile.ROW_DELIMITER
                + "RnaFoldMFE" + SourceFile.ROW_DELIMITER
                + "RelativePosition" + SourceFile.ROW_DELIMITER
                + senseColumn
                + nVariablesTable
                + additionalSequenceCol
                + "fornaVisualization" + SourceFile.ROW_DELIMITER;
    }

    /**
     * @return the maxPatternLength
     */
    public static int getMaxPatternLength() {
        return maxPatternLength;
    }

    /**
     * @param aMaxPatternLength the maxPatternLength to set
     */
    public static void setMaxPatternLength(int aMaxPatternLength) {
        maxPatternLength = aMaxPatternLength;
    }

    /**
     * @return the hasAdditionalSequence
     */
    public static boolean isHasAdditionalSequence() {
        return hasAdditionalSequence;
    }

    /**
     * @param aHasAdditionalSequence the hasAdditionalSequence to set
     */
    public static void setHasAdditionalSequence(boolean aHasAdditionalSequence) {
        hasAdditionalSequence = aHasAdditionalSequence;
    }

    /**
     * @return the hasTwoSenses
     */
    public static boolean isHasTwoSenses() {
        return hasTwoSenses;
    }

    /**
     * @param aHasTwoSenses the hasTwoSenses to set
     */
    public static void setHasTwoSenses(boolean aHasTwoSenses) {
        hasTwoSenses = aHasTwoSenses;
    }

    /**
     *
     * @return
     */
    public String getAdditional5Seq() {
        return additional5Seq;
    }

    /**
     *
     * @param additional5Seq
     */
    public void setAdditional5Seq(String additional5Seq) {
        this.additional5Seq = additional5Seq;
    }

    /**
     *
     * @return
     */
    public String getAdditional3Seq() {
        return additional3Seq;
    }

    /**
     *
     * @param additional3Seq
     */
    public void setAdditional3Seq(String additional3Seq) {
        this.additional3Seq = additional3Seq;
    }

    /**
     *
     * @param number
     * @return
     */
    private String getFormattedNumber(Double number, int digits) {
        NumberFormat numberFormatter
                = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormatter.setMinimumFractionDigits(digits);
        return numberFormatter.format(number);
    }

    private String getFormattedNumber(Double number) {
        return getFormattedNumber(number, 3);
    }

    /**
     *
     * @return
     */
    public int getMismatches() {
        return getBulge();
    }

    /**
     *
     * @param mismatches
     */
    public void setMismatches(int mismatches) {
        this.setBulge(mismatches);
    }

    /**
     *
     * @param invert
     */
    public void setReverse(boolean invert) {
        this.setReversed(invert);
    }

    /**
     *
     * @return
     */
    public boolean getReversed() {
        return isReversed();
    }

    /**
     *
     * @return
     */
    public double getRelativePos() {
        return this.relativePos;
    }

    /**
     *
     * @param relativePos
     */
    public void setRelativePos(double relativePos) {
        this.relativePos = relativePos;
    }

    /**
     *
     * @return
     */
    public int getEndsAt() {
        return endsAt;
    }

    /**
     *
     * @param endsAt
     */
    public void setEndsAt(int endsAt) {
        this.endsAt = endsAt;
    }

    public String isReverse() {
        if (!this.isReversed()) {
            return "+";
        } else {
            return "-";
        }
    }

    /**
     *
     * @return
     */
    public String getGeneID() {
        return this.getIdFasta().getGeneID();
    }

    /**
     *
     * @return
     */
    public String getGeneSymbol() {
        if (this.getIdFasta().getGeneSymbol() != null) {
            return this.getIdFasta().getGeneSymbol();
        } else {
            return "";
        }
    }

    /**
     *
     * @return
     */
    public String getGUPairs() {
        long pairs = Math.round(this.getPercentGu() * this.getPairments());

        return pairs + "";
    }

    /**
     *
     * @return
     */
    public String getHairpinStructure() {
        return hairpinStructure; //drawHairpinStructure();
    }

    /**
     * @param hairpinStructure the hairpinStructure to set
     */
    public void setHairpinStructure(String hairpinStructure) {
        this.hairpinStructure = hairpinStructure;
    }

    /**
     *
     * @return
     */
    public String getLoop() {

        String theLoop = this.loop;

        if (isReversed()) {
            theLoop = reverseSequence(theLoop);
        }

        return theLoop;
    }

    /**
     *
     * @param loop
     */
    public void setLoop(String loop) {
        char aux;
        this.loop = loop;

        for (int i = 0; i < loop.length(); i++) {
            for (BaseVariable baseV : patternVariables) {
                if (baseV.getPosition() == i) {
                    baseV.setValue(loop.charAt(i));
                }
            }
        }

    }

    /**
     *
     * @return
     */
    public String getLoopID() {
        String theLoop, terminal;

        theLoop = this.getLoop();
        terminal = this.getTerminalPair();

        if (isReversed()) {
            theLoop = reverseSequence(theLoop);
            terminal = reverseSequence(terminal);
        }

        return this.getPredecessorLoop() + theLoop
                + "(" + terminal + ")|" + this.getPairments();
    }

    /**
     *
     * @return
     */
    public String getLoopPattern() {

        return this.loopPattern;
    }

    /**
     *
     * @param pattern
     */
    public void setLoopPattern(String pattern) {
        this.loopPattern = pattern;
        char aux;

        this.patternVariables.clear();

        for (int i = 0; i < pattern.length(); i++) {
            aux = pattern.charAt(i);
            if (aux == 'N' || aux == 'K' || aux == 'M' || aux == 'W'
                    || aux == 'B' || aux == 'D' || aux == 'R' || aux == 'H'
                    || aux == 'S' || aux == 'Y' || aux == 'V') {
                this.patternVariables.add(new BaseVariable('N', i));
            }
        }
    }

    /**
     *
     * @return
     */
    public InputSequence getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(InputSequence mode) {
        this.mode = mode;
    }

    /**
     *
     * @return
     */
    public double getPercASequence() {
        return percASequence;
    }

    /**
     *
     * @param percASequence
     */
    public final void setPercASequence(float percASequence) {
        this.percASequence = percASequence;
    }

    /**
     * @param percASequence the percASequence to set
     */
    public void setPercASequence(double percASequence) {
        this.percASequence = percASequence;
    }

    /**
     *
     * @return
     */
    public double getPercCSequence() {
        return percCSequence;
    }

    /**
     *
     * @param percCSequence
     */
    public final void setPercCSequence(float percCSequence) {
        this.percCSequence = percCSequence;
    }

    /**
     * @param percCSequence the percCSequence to set
     */
    public void setPercCSequence(double percCSequence) {
        this.percCSequence = percCSequence;
    }

    /**
     *
     * @return
     */
    public double getPercentAg() {
        return percentAg;
    }

    /**
     * @param percent_AG the percent_AG to set
     */
    public void setPercentAg(double percent_AG) {
        this.percentAg = percent_AG;
    }

    /**
     *
     * @return
     */
    public double getPercentAu() {
        return percentAu;
    }

    /**
     * @param percent_AU the percent_AU to set
     */
    public void setPercentAu(double percent_AU) {
        this.percentAu = percent_AU;
    }

    /**
     *
     * @return
     */
    public double getPercentCg() {
        return percentCg;
    }

    /**
     * @param percent_CG the percent_CG to set
     */
    public void setPercentCg(double percent_CG) {
        this.percentCg = percent_CG;
    }

    /**
     *
     * @return
     */
    public double getPercentGu() {
        return percentGu;
    }

    /**
     *
     * @param wooble
     */
    public void setPercentGu(int wooble) {
        float percentGU;
        int size;
        String aux = getRnaHairpinSequence();
        size = (aux.length() - this.getLoop().length()) / 2;

        aux = aux.substring(0, size);

        percentGU = (float) wooble / aux.length();
        this.setPercentGu(percentGU);
    }

    /**
     * @param percent_GU the percent_GU to set
     */
    public void setPercentGu(double percent_GU) {
        this.percentGu = percent_GU;
    }

    /**
     *
     * @return
     */
    public double getPercGSequence() {
        return percGSequence;
    }

    /**
     *
     * @param percGSequence
     */
    public void setPercGSequence(float percGSequence) {
        this.percGSequence = percGSequence;
    }

    /**
     * @param percGSequence the percGSequence to set
     */
    public void setPercGSequence(double percGSequence) {
        this.percGSequence = percGSequence;
    }

    /**
     *
     * @return
     */
    public double getPercUSequence() {
        return percUSequence;
    }

    /**
     *
     * @param percUSequence
     */
    public void setPercUSequence(float percUSequence) {
        this.percUSequence = percUSequence;
    }

    /**
     * @param percUSequence the percUSequence to set
     */
    public void setPercUSequence(double percUSequence) {
        this.percUSequence = percUSequence;
    }

    /**
     *
     * @return
     */
    public char getPredecessorLoop() {
        return predecessorLoop;
    }

    /**
     * @param predecessorLoop the predecessorLoop to set
     */
    public void setPredecessorLoop(char predecessorLoop) {
        this.predecessorLoop = predecessorLoop;
    }

    /**
     *
     * @return
     */
    public String getAdditionalSequenceCount() {

        Integer count = 0;

        if (this.getAdditionalSeqLocations() != null) {
            count = this.getAdditionalSeqLocations().size();
        }

        return count.toString();
    }

    /**
     *
     * @return
     */
    public String getAdditionalSequenceLocations() {

        String locations = ""; //$NON-NLS-1$

        if (this.getAdditionalSeqLocations() == null) {
            return "";
        }

        Iterator<Integer> itr = this.getAdditionalSeqLocations().iterator();

        while (itr.hasNext()) {

            Integer element = itr.next();

            if (itr.hasNext()) {
                locations = locations.concat(element + ","); //$NON-NLS-1$
            } else {
                locations = locations.concat(Integer.toString(element));
            }
        }

        return locations;
    }

    /**
     *
     * @return
     */
    public String getRnaHairpinSequence() {
        return rnaHairpinSequence;
    }

    /**
     *
     * @param rnaHairpinSequence
     */
    public void setRnaHairpinSequence(String rnaHairpinSequence) {
        this.rnaHairpinSequence = rnaHairpinSequence;
    }

    /**
     *
     * @return
     */
    public int getSequenceLength() {
        return sequenceLength;
    }

    /**
     *
     * @param sequenceLength
     */
    public void setSequenceLength(final int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    /**
     *
     * @return
     */
    public int getStartsAt() {
        return startsAt;
    }

    /**
     *
     * @param startsAt
     */
    public void setStartsAt(int startsAt) {
        this.startsAt = startsAt;
    }

    /**
     *
     * @return
     */
    public int getPairments() {
        int count = StringUtils.countMatches(getViennaStructure(), "(");
        return count;
    }

    /**
     *
     * @return
     */
    public String getTerminalPair() {
        Character a = getRnaHairpinSequence().charAt(getViennaStructure().lastIndexOf("("));
        Character b = getRnaHairpinSequence().charAt(getViennaStructure().indexOf(")"));

        return a.toString() + b;
    }

    /**
     *
     * @return
     */
    public String getTranscriptID() {
        return this.getIdFasta().getTranscriptID();
    }

    public void setLocation(int pos) {
        ((GenBank) getIdFasta()).setLocation(pos);
    }

    /**
     *
     */
    public void checkPairments() {

        String seq = this.getRnaHairpinSequence();
        int woobleCount = 0;
        int CG = 0, AU = 0;
        StringBuilder aux = new StringBuilder(this.getViennaStructure());
        int firstIzq;

        if (this.getLoop().isEmpty() || this.getViennaStructure().isEmpty()) {
            return;
        }

        // Count internal loops and internalLoops
        try {
            firstIzq = this.getViennaStructure().lastIndexOf('(');
            int firstDer = this.getViennaStructure().indexOf(')');

            for (int i = firstIzq; i >= 0 && firstDer
                    < getViennaStructure().length(); i--) {
                if (getViennaStructure().charAt(i) == '(') {
                    while (getViennaStructure().charAt(firstDer) != ')') {
                        firstDer++;
                    }

                    if (getViennaStructure().charAt(firstDer) == ')') {
                        if ((seq.charAt(i) == 'U' && seq.charAt(firstDer) == 'G')
                                        || (seq.charAt(i) == 'G'
                                        && seq.charAt(firstDer) == 'U')) {
                            aux.setCharAt(i, '{');
                            aux.setCharAt(firstDer, '}');
                            woobleCount++;
                        } else {
                            if ((seq.charAt(i) == 'U'
                                    && seq.charAt(firstDer) == 'A')
                                    || (seq.charAt(i) == 'A'
                                    && seq.charAt(firstDer) == 'U')) {
                                AU++;
                            }

                            if ((seq.charAt(i) == 'C' && seq.charAt(firstDer) == 'G')
                                    || (seq.charAt(i) == 'G'
                                    && seq.charAt(firstDer) == 'C')) {
                                CG++;
                            }
                        }
                    }

                    firstDer++;
                }
            }

        } catch (Exception e) {
            out.println("checkPairments-ERROR: " + e.getMessage());
        }
        this.setHairpinStructure(aux.toString());
        this.setPercentAu(AU / (double) (AU + CG + woobleCount));
        this.setPercentCg(CG / (double) (AU + CG + woobleCount));
        this.setPercentGu(woobleCount / (double) (AU + CG + woobleCount));
    }

    /**
     *
     */
    public final void checkInternalLoops() {
        this.setInternalLoops(0);
        this.setBulge(0);
        String auxStruct = this.getViennaStructure();
        int len, aux, auxI;
        auxStruct = auxStruct.replaceAll("\\.", "a");
        auxStruct = auxStruct.replaceAll("([a-z])\\1+", "$1");

        len = auxStruct.length();
        aux = auxStruct.indexOf(")");
        auxI = auxStruct.lastIndexOf("(");

        for (int i = 0; (auxI - i) >= 0 && (aux + i) < len; i++) {

            if (auxStruct.charAt(auxI - i) == 'a'
                    && auxStruct.charAt(aux + i) == 'a') {
                this.setInternalLoops(this.getInternalLoops() + 1);
            } else if ((auxStruct.charAt(auxI - i) == 'a'
                    && auxStruct.charAt(aux + i) != 'a')
                    || (auxStruct.charAt(auxI - i) != 'a'
                    && auxStruct.charAt(aux + i) == 'a')) {
                this.setBulge(this.getBulge() + 1);
            }
        }
    }

    /**
     *
     * @param startPosLoop
     */
    public final void setNLoop(int startPosLoop) {

        char precedes = ' ',
                precedes2 = ' ';
        int matchFirst,
                matchLast,
                startPos;

        startPos = startPosLoop;

        if (isReversed()) {
            this.setRnaHairpinSequence(reverseSequence(this.getRnaHairpinSequence()));

            matchFirst = getRnaHairpinSequence().indexOf(reverseSequence(getLoop()));
            matchLast = getRnaHairpinSequence().lastIndexOf(reverseSequence(getLoop()));

            startPos = matchLast;
        }

        try {
            if (this.getRnaHairpinSequence() != null) {
                precedes = this.getRnaHairpinSequence().charAt(startPos - 1);
                precedes2 = this.getRnaHairpinSequence().charAt(startPos - 2);
            }

            this.setPredecessorLoop(precedes);
            this.setPredecessor2Loop(precedes2);

            if (isReversed()) {
                this.setRnaHairpinSequence(reverseSequence(this.getRnaHairpinSequence()));
            }
        } catch (Exception ex) {
            out.println("\nERROR: " + ex.getMessage());
            out.println(this.getIdFasta().toRowCSV());
            out.println(this);
        }
    }

    /**
     *
     */
    public void setPercentAg() {

        float myPercent_AG = 0;
        int count;
        String aux;
        aux = getRnaHairpinSequence();

        if (aux != null) {
            count = aux.length() - aux.replace("A", "").length();
            count += aux.length() - aux.replace("G", "").length();

            myPercent_AG = count / (float) aux.length();
        }

        this.setPercentAg(myPercent_AG);
    }

    /**
     *
     * @return
     */
    public double getMfe() {
        return mfe;
    }

    /**
     *
     * @param mfe
     */
    public void setMfe(Double mfe) {
        this.setMfe((double) mfe);
    }

    /**
     * @param mfe the mfe to set
     */
    public void setMfe(double mfe) {
        this.mfe = mfe;
    }

    /**
     *
     * @return
     */
    public String getStructure() {
        return getViennaStructure();
    }

    /**
     *
     * @param structure
     */
    public void setStructure(String structure) {
        this.setViennaStructure(structure);
    }

    /**
     *
     */
    public void setPercentAu() {
        float percentAU;
        int count = 0;
        int size;
        String aux = getRnaHairpinSequence();
        size = (aux.length() - getLoop().length()) / 2;

        for (int i = 0; i < size; i++) {

            if ((aux.charAt(i) == 'A' && aux.charAt(aux.length() - 1 - i) == 'U')
                    || (aux.charAt(i) == 'U' && aux.charAt(aux.length() - 1 - i) == 'A')) {
                count++;
            }
        }

        percentAU = count / (float) size;

        this.setPercentAu(percentAU);
    }

    /**
     *
     */
    public void setPercentCg() {
        float percentCG;
        int count = 0;
        int size;
        String aux = getRnaHairpinSequence();
        size = (aux.length() - getLoop().length()) / 2;

        for (int i = 0; i < size; i++) {

            if ((aux.charAt(i) == 'C' && aux.charAt(aux.length() - 1 - i) == 'G')
                    || (aux.charAt(i) == 'G' && aux.charAt(aux.length() - 1 - i) == 'C')) {
                count++;
            }
        }

        percentCG = count / (float) size;

        this.setPercentCg(percentCG);
    }

    /**
     *
     * @param id
     */
    public void setTags(String id) {
        switch (this.getMode()) {
            case ENSEMBL:
                ((EnsemblFasta) getIdFasta()).setEnsemblTags(id);
                break;
            case FLYBASE:
                ((FlyBaseFasta) getIdFasta()).setFlyBaseTags(id);
                break;
            case BIOMART:
                ((BioMartFasta) getIdFasta()).setBioMartTags(id);
                break;
            case GENERIC:
                ((Generic) getIdFasta()).setGenericTags(id);
                break;
        }
    }

    /**
     *
     * @param gene
     * @param synonym
     * @param transcript
     * @param description
     * @param cds
     */
    public void setTags(String gene, String synonym, String transcript,
                        String description,
                        String cds) {
        getIdFasta().setGeneID(gene);
        getIdFasta().setTranscriptID(transcript);
        ((GenBank) getIdFasta()).setDescription(description);
        ((GenBank) getIdFasta()).setCDS(cds);
        ((GenBank) getIdFasta()).setSynonym(synonym);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "{" + this.idFasta.getGeneID() + "; "
                + this.startsAt + "; "
                + this.endsAt + "; "
                + this.loop + "; "
                + this.rnaHairpinSequence + "; "
                + this.viennaStructure + "}";
    }

    /**
     *
     * @return
     */
    public String toRowCSV() {

        String nVariablesValues = "";
        String additionalSeqValues = "";
        String senseValue = "";
        String fornaSequence
                = "http://nibiru.tbi.univie.ac.at/forna/forna.html?id=fasta&file=>"
                + this.getGeneSymbol()
                + "\\n" + this.rnaHairpinSequence
                + "\\n" + this.viennaStructure;
        String aux;
        boolean stop;

        for (int i = 0; i < getMaxPatternLength(); i++) {
            aux = "";
            stop = false;
            for (int j = 0; j < patternVariables.size() && !stop; j++) {
                if (patternVariables.get(j).getPosition() == i) {
                    aux = patternVariables.get(j).getValue().toString();
                    stop = true;
                    patternVariables.remove(j);
                }
            }

            nVariablesValues += aux + SourceFile.ROW_DELIMITER;
        }

        if (isHasAdditionalSequence()) {
            additionalSeqValues = this.getAdditionalSequenceCount()
                    + SourceFile.ROW_DELIMITER
                    + this.getAdditionalSequenceLocations()
                    + SourceFile.ROW_DELIMITER;
        }

        if (isHasTwoSenses()) {
            senseValue = this.isReverse() + SourceFile.ROW_DELIMITER;
        }

        return this.getIdFasta().toRowCSV()
                + this.getLoopPattern() + SourceFile.ROW_DELIMITER
                + this.getTerminalPair() + SourceFile.ROW_DELIMITER
                + this.getPredecessor2Loop() + SourceFile.ROW_DELIMITER //n-2
                + this.getPredecessorLoop() + SourceFile.ROW_DELIMITER
                + this.getLoop() + SourceFile.ROW_DELIMITER
                + this.getRnaHairpinSequence() + SourceFile.ROW_DELIMITER
                + this.getAdditional5Seq() + SourceFile.ROW_DELIMITER
                + this.getAdditional3Seq() + SourceFile.ROW_DELIMITER
                + this.getHairpinStructure() + SourceFile.ROW_DELIMITER
                + this.getStructure() + SourceFile.ROW_DELIMITER
                + this.getPairments() + SourceFile.ROW_DELIMITER /* para que me de apareamientos */
                + this.getGUPairs() + SourceFile.ROW_DELIMITER
                + this.getMismatches() + SourceFile.ROW_DELIMITER
                + this.getInternalLoops() + SourceFile.ROW_DELIMITER
                + this.getSequenceLength() + SourceFile.ROW_DELIMITER
                + this.getStartsAt() + SourceFile.ROW_DELIMITER
                + this.getEndsAt() + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercASequence()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercCSequence()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercGSequence()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercUSequence()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercentAu()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercentCg()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercentGu()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getPercentAg()) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getMfe(), 5) + SourceFile.ROW_DELIMITER
                + getFormattedNumber(this.getRelativePos()) + SourceFile.ROW_DELIMITER
                + senseValue
                + nVariablesValues
                + additionalSeqValues
                + fornaSequence + SourceFile.ROW_DELIMITER;
    }

    /**
     * @return the additionalSeqLocations
     */
    public List<Integer> getAdditionalSeqLocations() {
        return Collections.unmodifiableList(additionalSeqLocations);
    }

    /**
     *
     * @param locations
     */
    public void setAdditionalSeqLocations(final List<Integer> locations) {
        this.additionalSeqLocations = locations;
    }

    /**
     * @return the bulge
     */
    public int getBulge() {
        return bulge;
    }

    /**
     * @param bulge the bulge to set
     */
    public void setBulge(int bulge) {
        this.bulge = bulge;
    }

    /**
     * @return the idFasta
     */
    public SourceFile getIdFasta() {
        return idFasta;
    }

    /**
     * @param idFasta the idFasta to set
     */
    public void setIdFasta(SourceFile idFasta) {
        this.idFasta = idFasta;
    }

    /**
     * @return the internalLoops
     */
    public int getInternalLoops() {
        return internalLoops;
    }

    /**
     * @param internalLoops the internalLoops to set
     */
    public void setInternalLoops(int internalLoops) {
        this.internalLoops = internalLoops;
    }

    /**
     * @return the predecessor2Loop
     */
    public char getPredecessor2Loop() {
        return predecessor2Loop;
    }

    /**
     * @param predecessor2Loop the predecessor2Loop to set
     */
    public void setPredecessor2Loop(char predecessor2Loop) {
        this.predecessor2Loop = predecessor2Loop;
    }

    /**
     * @return the viennaStructure
     */
    public String getViennaStructure() {
        return viennaStructure;
    }

    /**
     * @param viennaStructure the viennaStructure to set
     */
    public void setViennaStructure(String viennaStructure) {
        this.viennaStructure = viennaStructure;
    }

    /**
     * @return the reversed
     */
    public boolean isReversed() {
        return reversed;
    }

    /**
     * @param reversed the reversed to set
     */
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

}
