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
 * SwingWorker that bridges the GUI with the LoopMatcher engine.
 * Kept for backward compatibility with existing GUIFrame code.
 */
class GUISwingWorker extends SwingWorker<Integer, Void> {

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

    public LoopMatcher getLoop() {
        return frame.getLoopMatcher();
    }
}
