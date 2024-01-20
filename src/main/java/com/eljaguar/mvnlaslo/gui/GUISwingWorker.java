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
package com.eljaguar.mvnlaslo.gui;

import com.eljaguar.mvnlaslo.core.LoopMatcher;
import com.eljaguar.mvnlaslo.io.GenBank;
import org.biojava.nbio.core.sequence.DNASequence;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.lang.System.out;

/**
 *
 * @author David A. Mancilla
 */
class GUISwingWorker extends
        SwingWorker<Integer, Void> {

    private final GUIFrame frame;

    public GUISwingWorker(GUIFrame frame) {
        this.frame = frame;
    }

    @Override
    protected Integer doInBackground() {

        LinkedHashMap<String, DNASequence> dnaFile;
        String pathIn;
        File[] listOfFiles;

        LoopMatcher lm = this.getLoop();

        if (frame.getTab1().getSelectedIndex() == 1) {

            try {
                out.println(frame.getCurrentBundle().getString("DOWNLOAD_NCBI"));

                listOfFiles = new File[frame.getGeneList().size()];
                int i = 0;

                for (String e : frame.getGeneList()) {
                    List<String> geneList = new ArrayList<>();
                    geneList.add(e);
                    dnaFile = (LinkedHashMap<String, DNASequence>) GenBank.downLoadSequenceForId(geneList);

                    if (!dnaFile.isEmpty()) {
                        // call the file as the first ncbi id
                        pathIn = GenBank.makeFile(frame.getPathOut(), dnaFile,
                                geneList.get(0).trim());
                        
                        if (pathIn == null) {
                            out.println("Skipping this file...");
                        } else {
                            out.print(frame.getCurrentBundle().getString("NCBI_DONE"));
                            listOfFiles[i++] = new File(pathIn);
                        }
                    }
                }

            } catch (Exception ex) {
                out.printf(frame.getCurrentBundle().getString("ERROR"),
                        ex.getLocalizedMessage());
                out.println("*Method: doInBackground*downloadSequence");
                frame.setIsRunning(false);
                this.cancel(true);
                return 0;
            }

            lm.setFileList(listOfFiles);

        }

        lm.startReadingFiles();
        return 1;
    }

    @Override
    protected void done() {
        out.flush();
        MessageBox.showInformationBox(frame.getCurrentBundle().getString("END_MSG"),
                frame.getCurrentBundle().getString("END_TITLE"));
        frame.getProgressBar().setValue(100);
        frame.setIsRunning(false);
    }

    /**
     * @return the current process instance.
     */
    public LoopMatcher getLoop() {
        return frame.getLoopMatcher();
    }


}
