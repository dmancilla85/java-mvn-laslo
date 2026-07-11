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
 * @author David A. Mancilla
 *
 */
public class BioMartFasta extends SourceFile {

    private static int nCols = 6;
    private static char fs = '|';
    private String[] columns;

    /**
     *
     */
    public BioMartFasta() {
        columns = new String[nCols];
    }

    /**
     * @return the fs
     */
    public static char getFs() {
        return fs;
    }

    /**
     * @param aFs the fs to set
     */
    public static void setFs(char aFs) {
        fs = aFs;
    }

    /**
     * @return the nCols
     */
    public static int getNumCols() {
        return nCols;
    }

    /**
     * @param anCols the nCols to set
     */
    public static void setNumCols(int anCols) {
        nCols = anCols;
    }

    /**
     *
     * @return
     */
    public static String getHeader() {
        StringBuilder header = new StringBuilder();

        for (int i = 0; i < getNumCols(); i++) {
            header.append("Column").append(i + 1).append(ROW_DELIMITER);
        }

        return header.toString();
    }

    /**
     *
     * @return
     */
    public int getNumColumns() {
        return getColumns().length;
    }

    /**
     *
     * @param idSequence
     */
    public void setBioMartTags(String idSequence) {

        String auxSeq;

        if (idSequence.contains("|")) {
            auxSeq = idSequence.replace(getFs(), '@');
        } else {
            auxSeq = idSequence.replace('=', '@');
        }

        auxSeq = auxSeq.replace(';', '-');
        String[] cols = auxSeq.split("@");

        for (int i = 0; i < cols.length && i < 6; i++) {
            getColumns()[i] = cols[i];
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String toRowCSV() {
        StringBuilder row = new StringBuilder();
        int i;

        for (i = 0; i < getColumns().length; i++) {
            row.append(getColumns()[i]).append(ROW_DELIMITER);
        }

        return row.toString();
    }

    /**
     * @return the columns
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public String[] getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(String[] columns) {
        this.columns = columns;
    }
}
