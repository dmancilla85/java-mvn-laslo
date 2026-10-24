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
package com.eljaguar.mvnlaslo.io;

/**
 * @author David
 *
 */
public class EnsemblFasta extends SourceFile {

    private static String GENE_BIOTYPE = "gene_biotype:";
    private static String TRANSCRIPT_BIOTYPE = "transcript_biotype:";
    private static String GENE_SYMBOL = "gene_symbol:";
    private static String DESCRIPTION = "description:";
    private static String HEADER = "GeneID" + ROW_DELIMITER
            + "GeneSymbol" + ROW_DELIMITER
            + "GeneBioType" + ROW_DELIMITER
            + "TranscriptID" + ROW_DELIMITER
            + "TranscriptBiotype" + ROW_DELIMITER
            + "#Splice" + ROW_DELIMITER
            + "Description" + ROW_DELIMITER;
    private String gene = "gene:";
    private String geneBiotype;
    private String transcriptBiotype;
    private String description;
    private String spliceNumber;

    /**
     *
     */
    public EnsemblFasta() {
        this.transcriptID = ""; //$NON-NLS-1$
        this.geneID = ""; //$NON-NLS-1$
        this.geneBiotype = ""; //$NON-NLS-1$
        this.transcriptBiotype = ""; //$NON-NLS-1$
        this.geneSymbol = ""; //$NON-NLS-1$
        this.description = ""; //$NON-NLS-1$
        this.spliceNumber = ""; //$NON-NLS-1$

    }

    /**
     *
     * @return
     */
    public static String getHeader() {
        return HEADER;
    }

    /**
     * @return the DESCRIPTION
     */
    public static String getDescriptionDefault() {
        return DESCRIPTION;
    }

    /**
     * @param aDESCRIPTION the DESCRIPTION to set
     */
    public static void setDescriptionDefault(String aDESCRIPTION) {
        DESCRIPTION = aDESCRIPTION;
    }

    /**
     * @return the GENE_BIOTYPE
     */
    public static String getGeneBiotypeTag() {
        return GENE_BIOTYPE;
    }

    /**
     * @param aGENE_BIOTYPE the GENE_BIOTYPE to set
     */
    public static void setGeneBiotypeTag(String aGENE_BIOTYPE) {
        GENE_BIOTYPE = aGENE_BIOTYPE;
    }

    /**
     * @return the GENE_SYMBOL
     */
    public static String getGeneSymbolTag() {
        return GENE_SYMBOL;
    }

    /**
     * @param aGENE_SYMBOL the GENE_SYMBOL to set
     */
    public static void setGeneSymbolTag(String aGENE_SYMBOL) {
        GENE_SYMBOL = aGENE_SYMBOL;
    }

    /**
     * @return the HEADER
     */
    public static String getHeaderDefault() {
        return HEADER;
    }

    /**
     * @param aHEADER the HEADER to set
     */
    public static void setHeaderDefault(String aHEADER) {
        HEADER = aHEADER;
    }

    /**
     * @return the TRANSCRIPT_BIOTYPE
     */
    public static String getTranscriptBiotypeTag() {
        return TRANSCRIPT_BIOTYPE;
    }

    /**
     * @param aTRANSCRIPT_BIOTYPE the TRANSCRIPT_BIOTYPE to set
     */
    public static void setTranscriptBiotypeTag(String aTRANSCRIPT_BIOTYPE) {
        TRANSCRIPT_BIOTYPE = aTRANSCRIPT_BIOTYPE;
    }

    /**
     *
     * @return
     */
    @Override
    public String toRowCSV() {
        return getGeneID() + ROW_DELIMITER
                + getGeneSymbol() + ROW_DELIMITER
                + getGeneBiotype() + ROW_DELIMITER
                + getTranscriptID() + ROW_DELIMITER
                + getTranscriptBiotype() + ROW_DELIMITER
                + getSpliceNumber() + ROW_DELIMITER
                + getDescription() + ROW_DELIMITER;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "EnsemblFastaID [transcriptID=" + getTranscriptID() + ", geneID=" + getGeneID() + ", geneBiotype=" + getGeneBiotype() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                + ", trancriptBiotype=" + getTranscriptBiotype() + ", geneSymbol=" + getGeneSymbol() + ", description=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + getDescription() + ", spliceNumber=" + getSpliceNumber() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     *
     * @param idSequence
     */
    public void setEnsemblTags(String idSequence) {

        int index, index2;
        String idsequence;
        String aux;

        if (idSequence == null || idSequence.length() <= 0) {
            return;
        }

        if (idSequence.indexOf(' ') < 0) {
            setGeneID(idSequence);
            return;
        }

        aux = idSequence.substring(0, idSequence.indexOf(' '));

        // Just to don't break the .csv file
        idsequence = idSequence.replaceAll(";", ":"); //$NON-NLS-1$ //$NON-NLS-2$

        // get Splice number and transcriptID
        index = aux.indexOf('.');

        if (index > 0) {
            setSpliceNumber(aux.substring(index + 1).trim());
            setTranscriptID(aux.substring(0, index - 1).trim());
        } else {
            setTranscriptID(aux.trim());
        }

        // get GeneID
        index = idSequence.indexOf(getGene());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(' ');

            if (index2 > 0) {
                aux = idsequence.substring(getGene().length(), index2);
            } else {
                aux = idsequence.substring(getGene().length());
            }

            setGeneID(aux.trim());
        }

        // get Gene Biotype
        index = idsequence.indexOf(EnsemblFasta.getGeneBiotypeTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(' ');

            if (index2 > 0) {
                aux = idsequence.substring(EnsemblFasta.getGeneBiotypeTag().length(), index2);
            } else {
                aux = idsequence.substring(EnsemblFasta.getGeneBiotypeTag().length());
            }

            setGeneBiotype(aux.trim());
        }
        // get Trancript Biotype
        index = idsequence.indexOf(EnsemblFasta.getTranscriptBiotypeTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(' ');

            if (index2 > 0) {
                aux = idsequence.substring(EnsemblFasta.getTranscriptBiotypeTag().length(), index2);
            } else {
                aux = idsequence.substring(EnsemblFasta.getTranscriptBiotypeTag().length());
            }

            setTranscriptBiotype(aux.trim());
        }

        // get Gene Symbol
        index = idsequence.indexOf(EnsemblFasta.getGeneSymbolTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(' ');

            if (index2 > 0) {
                aux = idsequence.substring(EnsemblFasta.getGeneSymbolTag().length(), index2);
            } else {
                aux = idsequence.substring(EnsemblFasta.getGeneSymbolTag().length());
            }

            setGeneSymbol(aux.trim());
        }

        // Description
        index = idsequence.indexOf(EnsemblFasta.getDescriptionDefault());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.lastIndexOf(':');

            if (index2 > 0) {
                aux = idsequence.substring(EnsemblFasta.getDescriptionDefault().length(), idsequence.lastIndexOf(' '));
            } else {
                aux = idsequence.substring(EnsemblFasta.getDescriptionDefault().length());
            }

            setDescription(aux.trim());
        }

    }

    /**
     * @return the gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @param gene the gene to set
     */
    public void setGene(String gene) {
        this.gene = gene;
    }

    /**
     *
     * @return
     */
    public String getGeneBiotype() {
        return geneBiotype;
    }

    /**
     *
     * @param geneBiotype
     */
    public void setGeneBiotype(String geneBiotype) {
        this.geneBiotype = geneBiotype;
    }

    /**
     *
     * @return
     */
    public String getTranscriptBiotype() {
        return transcriptBiotype;
    }

    /**
     *
     * @param transcriptBiotype
     */
    public void setTranscriptBiotype(String transcriptBiotype) {
        this.transcriptBiotype = transcriptBiotype;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getSpliceNumber() {
        return spliceNumber;
    }

    /**
     *
     * @param spliceNumber
     */
    public void setSpliceNumber(String spliceNumber) {
        this.spliceNumber = spliceNumber;
    }
}
