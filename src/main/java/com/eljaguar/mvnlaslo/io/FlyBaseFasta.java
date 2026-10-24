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
public class FlyBaseFasta extends SourceFile {

    private static String TYPE = "type=";
    private static String LOCATION = "loc=";
    private static String NAME = "name=";
    private static String DBXREF = "dbxref=";
    private static String SCORE = "score=";
    private static String CHECKSUM = "MD5=";
    private static String GENE_ID = "parent=";
    private static String RELEASE = "release=";
    private static String SPECIES = "species=";
    private static String HEADER =
            "GeneID" + ROW_DELIMITER
                    + "TranscriptID" + ROW_DELIMITER
                    + "Name" + ROW_DELIMITER
                    + "Release" + ROW_DELIMITER
                    + "DBXREF" + ROW_DELIMITER
                    + "Score" + ROW_DELIMITER
                    + "Checksum" + ROW_DELIMITER
                    + "Species" + ROW_DELIMITER;
    private String location;
    private String name;
    private String dbxref;
    private String score;
    private String checksum;
    private String release;
    private String species;

    /**
     *
     */
    public FlyBaseFasta() {
        this.transcriptID = "";
        this.geneID = "";
        this.location = "";     //$NON-NLS-1$
        this.checksum = "";     //$NON-NLS-1$
        this.release = "";      //$NON-NLS-1$
        this.species = "";      //$NON-NLS-1$
        this.name = "";         //$NON-NLS-1$
        this.dbxref = "";       //$NON-NLS-1$
        this.score = "";        //$NON-NLS-1$
    }

    /**
     * @return the CHECKSUM
     */
    public static String getChecksumTag() {
        return CHECKSUM;
    }

    /**
     * @param aCHECKSUM the CHECKSUM to set
     */
    public static void setChecksumTag(String aCHECKSUM) {
        CHECKSUM = aCHECKSUM;
    }

    /**
     * @return the DBXREF
     */
    public static String getDbxrefTag() {
        return DBXREF;
    }

    /**
     * @param aDBXREF the DBXREF to set
     */
    public static void setDbxrefTag(String aDBXREF) {
        DBXREF = aDBXREF;
    }

    /**
     * @return the GENE_ID
     */
    public static String getGeneIdTag() {
        return GENE_ID;
    }

    /**
     * @param aGENE_ID the GENE_ID to set
     */
    public static void setGeneIdTag(String aGENE_ID) {
        GENE_ID = aGENE_ID;
    }

    /**
     * @return the HEADER
     */
    public static String getHeader() {
        return HEADER;
    }

    /**
     * @param aHEADER the HEADER to set
     */
    public static void setHeader(String aHEADER) {
        HEADER = aHEADER;
    }

    /**
     * @return the LOCATION
     */
    public static String getLocationTag() {
        return LOCATION;
    }

    /**
     * @param aLOCATION the LOCATION to set
     */
    public static void setLocationTag(String aLOCATION) {
        LOCATION = aLOCATION;
    }

    /**
     * @return the NAME
     */
    public static String getNameTag() {
        return NAME;
    }

    /**
     * @param aNAME the NAME to set
     */
    public static void setNameTag(String aNAME) {
        NAME = aNAME;
    }

    /**
     * @return the RELEASE
     */
    public static String getReleaseTag() {
        return RELEASE;
    }

    /**
     * @param aRELEASE the RELEASE to set
     */
    public static void setReleaseTag(String aRELEASE) {
        RELEASE = aRELEASE;
    }

    /**
     * @return the SCORE
     */
    public static String getScoreTag() {
        return SCORE;
    }

    /**
     * @param aSCORE the SCORE to set
     */
    public static void setScoreTag(String aSCORE) {
        SCORE = aSCORE;
    }

    /**
     * @return the SPECIES
     */
    public static String getSpeciesTag() {
        return SPECIES;
    }

    /**
     * @param aSPECIES the SPECIES to set
     */
    public static void setSpeciesTag(String aSPECIES) {
        SPECIES = aSPECIES;
    }

    /**
     * @return the TYPE
     */
    public static String getTypeTag() {
        return TYPE;
    }

    /**
     * @param aTYPE the TYPE to set
     */
    public static void setTypeTag(String aTYPE) {
        TYPE = aTYPE;
    }

    @Override
    public String toString() {
        return "FlyBaseFastaID [transcriptID=" + getTranscriptID() + ", "
                + "geneID=" + getGeneID() + ", location=" + getLocation()
                + ", name=" + getName() + ", dbxref=" + getDbxref() + ", "
                + "score=" + getScore() + ", checksum=" + getChecksum()
                + ", release=" + getRelease() + ", species=" + getSpecies() + "]";
    }

    /**
     *
     * @param idSequence
     */
    public void setFlyBaseTags(String idSequence) {

        int index, index2;
        String idsequence;

        idsequence = idSequence;

        if (idSequence == null || idSequence.length() <= 0) {
            return;
        }

        String aux = idSequence.substring(0, idSequence.indexOf(' '));

        // get Splice number and transcriptID
        index = aux.indexOf('.');

        if (index > 0) {

            // this.id_ensbl.spliceNumber = aux.substring(index + 1).trim();
            setTranscriptID(aux.substring(0, index - 1).trim());
        } else {
            setTranscriptID(aux.trim());
        }

        //index2 = getTranscriptID().indexOf(';');

        //if (index2 > 0) {
        //    aux = getTranscriptID().substring(getTranscriptID().length(), index2);
        //}

        // get location
        index = idSequence.indexOf(FlyBaseFasta.getLocationTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getLocationTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getLocationTag().length());
            }

            setLocation(aux.trim());
        }

        // get name
        index = idsequence.indexOf(FlyBaseFasta.getNameTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getNameTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getNameTag().length());
            }

            setName(aux.trim());
        }

        // get dbxref
        index = idsequence.indexOf(FlyBaseFasta.getDbxrefTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getDbxrefTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getDbxrefTag().length());
            }

            setDbxref(aux.trim());
        }

        // get score
        index = idsequence.indexOf(FlyBaseFasta.getScoreTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getScoreTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getScoreTag().length());
            }

            setScore(aux.trim());
        }

        // get checksum
        index = idsequence.indexOf(FlyBaseFasta.getChecksumTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getChecksumTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getChecksumTag().length());
            }

            setChecksum(aux.trim());
        }

        // get GeneID
        index = idsequence.indexOf(FlyBaseFasta.getGeneIdTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getGeneIdTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getGeneIdTag().length());
            }

            setGeneID(aux.trim());
        }

        // get release
        index = idsequence.indexOf(FlyBaseFasta.getReleaseTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getReleaseTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getReleaseTag().length());
            }

            setRelease(aux.trim());
        }

        // get specie
        index = idsequence.indexOf(FlyBaseFasta.getSpeciesTag());

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFasta.getSpeciesTag().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFasta.getSpeciesTag().length());
            }

            setSpecies(aux.trim());
        }
    }

    @Override
    public String toRowCSV() {
        return getGeneID() + ROW_DELIMITER
                + getTranscriptID() + ROW_DELIMITER
                + getName() + ROW_DELIMITER
                + getRelease() + ROW_DELIMITER
                + getDbxref() + ROW_DELIMITER
                + getScore() + ROW_DELIMITER
                + getChecksum() + ROW_DELIMITER
                + getSpecies() + ROW_DELIMITER;
    }

    /**
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * @param checksum the checksum to set
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * @return the dbxref
     */
    public String getDbxref() {
        return dbxref;
    }

    /**
     * @param dbxref the dbxref to set
     */
    public void setDbxref(String dbxref) {
        this.dbxref = dbxref;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the release
     */
    public String getRelease() {
        return release;
    }

    /**
     * @param release the release to set
     */
    public void setRelease(String release) {
        this.release = release;
    }

    /**
     * @return the score
     */
    public String getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * @return the species
     */
    public String getSpecies() {
        return species;
    }

    /**
     * @param species the species to set
     */
    public void setSpecies(String species) {
        this.species = species;
    }
}
