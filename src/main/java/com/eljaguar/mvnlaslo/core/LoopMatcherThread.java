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

import com.eljaguar.mvnlaslo.io.InputSequence;
import com.eljaguar.mvnlaslo.io.Vienna;
import com.eljaguar.mvnlaslo.tools.OSValidator;
import com.eljaguar.mvnlaslo.tools.RNAFoldConfiguration;
import com.eljaguar.mvnlaslo.tools.RNAFoldInterface;
import com.opencsv.CSVWriter;
import org.biojava.nbio.core.sequence.DNASequence;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import static com.eljaguar.mvnlaslo.core.SequenceAnalizer.beginFullMatching;
import static java.lang.System.out;

/**
 *
 * @author David A. Mancilla
 */
public class LoopMatcherThread implements Runnable {

  private final int LENGTH_CHECK_AUX = 15;
  private final boolean extendedMode;
  private final boolean searchReverse;
  private final String additionalSequence;
  private final int maxLength;
  private final int minLength;
  private final InputSequence inputType;
  private Iterator<String> patternItr;
  private final CSVWriter writer;
  private final DNASequence dnaElement;
  private static Semaphore MUTEX = new Semaphore(1);
  private static Semaphore SEM;
  private static boolean started = false;
  private CountDownLatch latch;
  private final ResourceBundle bundle;
  private final int temperature;
  private final boolean avoidLonelyPairs;
  private final Vienna viennaElement;

  /**
   *
   * @param extendedMode
   * @param additionalSequence
   * @param maxLength
   * @param minLength
   * @param dnaElement
   * @param inputType
   * @param patternItr
   * @param writer
   * @param searchReverse
   * @param bundle
   * @param temperature
   * @param avoidLonelyPairs
   */
  public LoopMatcherThread(boolean extendedMode, String additionalSequence,
     int maxLength,  int minLength,  DNASequence dnaElement,
     InputSequence inputType,  Iterator<String> patternItr,
     CSVWriter writer,  boolean searchReverse,
     ResourceBundle bundle,  int temperature,
     boolean avoidLonelyPairs) {

     int countThreads;
    this.extendedMode = extendedMode;
    this.additionalSequence = additionalSequence;
    this.maxLength = maxLength;
    this.minLength = minLength;
    this.inputType = inputType;
    this.dnaElement = dnaElement;
    this.patternItr = patternItr;
    this.writer = writer;
    this.searchReverse = searchReverse;
    this.bundle = bundle;
    this.temperature = temperature;
    this.avoidLonelyPairs = avoidLonelyPairs;
    this.viennaElement = null;

    if (!started) {
      // 2 * n + 1
      countThreads = 2 * OSValidator.getNumberOfCPUCores() + 1;

      out.println(java.text.MessageFormat.format(bundle
               .getString("USING_N_CORES"), countThreads));
      SEM = new Semaphore(countThreads);
      started = true;
    }
  }

  /**
   * @param viennaElement
   * @param maxLength
   * @param minLength
   * @param additionalSequence
   * @param patternItr
   * @param writer
   * @param bundle
   */
  public LoopMatcherThread( Vienna viennaElement,
     int maxLength,  int minLength,  String additionalSequence,
     Iterator<String> patternItr,  CSVWriter writer,
     ResourceBundle bundle) {

     int countThreads;
    this.extendedMode = false;
    this.additionalSequence = additionalSequence;
    this.maxLength = maxLength;
    this.minLength = minLength;
    this.inputType = InputSequence.VIENNA;
    this.dnaElement = null;
    this.patternItr = patternItr;
    this.writer = writer;
    this.searchReverse = false;
    this.bundle = bundle;
    this.temperature = RNAFoldInterface.DEFAULT_TEMPERATURE;
    this.avoidLonelyPairs = false;
    this.viennaElement = viennaElement;

    if (!started) {
      // 2 * n + 1
      countThreads = 2 * OSValidator.getNumberOfCPUCores() + 1;

      out.println(java.text.MessageFormat.format(bundle
               .getString("USING_N_CORES"), countThreads));
      SEM = new Semaphore(countThreads);
      started = true;
    }
  }

  /**
   * @return the bundle
   */
  public final ResourceBundle getBundle() {
    return bundle;
  }

/**
 *
 * @param currentPattern
 */
  private void runNormalMode( String currentPattern) {
    SequenceAnalizer.beginDefaultMatching(getDnaElement(), currentPattern,
      getWriter(), false, getMaxLength(), getMinLength(),
      getInputType(), getAdditionalSequence(), temperature,
      avoidLonelyPairs);

    if (isSearchReverse()) {
      SequenceAnalizer.beginDefaultMatching(getDnaElement(),
        currentPattern, getWriter(), true, getMaxLength(),
        getMinLength(), getInputType(),
        getAdditionalSequence(), temperature,
        avoidLonelyPairs);
    }
  }

  /**
   *
   * @param currentPattern
   */
  private void runViennaMode( String currentPattern) {
    SequenceAnalizer.beginDefaultMatching(viennaElement, currentPattern, writer,
      false, maxLength, minLength, additionalSequence);
  }

  /**
   *
   * @param fold
   * @param currentPattern
   */
  private void runExtendedMode( RNAFoldInterface fold,
     String currentPattern) {
    try {
      getSEM().acquire();
      beginFullMatching(getDnaElement(),
        fold.getStructure(),
        currentPattern, getWriter(), false, getMaxLength(),
        getMinLength(), getInputType(),
        getAdditionalSequence(), temperature,
        avoidLonelyPairs);
    } catch (InterruptedException ex) {
      String msg = "#";

      if (ex.getLocalizedMessage() != null) {
        msg += ex.getLocalizedMessage() + " - ";
      }
      if (ex.getMessage() != null) {
        msg += ex.getMessage() + " - ";
      }

      out.println(java.text.MessageFormat.format(
        getBundle()
          .getString("ERROR_EX"), msg));
      out.println("1-Exception: " + ex);
    } finally {
      getSEM().release();
    }

    if (isSearchReverse()) {
      try {
        getSEM().acquire();
        beginFullMatching(getDnaElement(),
          fold.getStructure(),
          currentPattern, getWriter(), true, getMaxLength(),
          getMinLength(), getInputType(),
          getAdditionalSequence(), temperature,
          avoidLonelyPairs);

      } catch (InterruptedException ex) {
        String msg = "#";

        if (ex.getLocalizedMessage() != null) {
          msg += ex.getLocalizedMessage() + " - ";
        }
        if (ex.getMessage() != null) {
          msg += ex.getMessage() + " - ";
        }

        out.println(java.text.MessageFormat.format(
          getBundle()
            .getString("ERROR_EX"), msg));
        out.println("2-Exception: " + ex);
      } finally {
        getSEM().release();
      }
    }
  }

  /**
   *
   * @return
   */
  private RNAFoldInterface checkExtendedMode() {
    RNAFoldInterface fold;
    String sequence = getDnaElement().getRNASequence()
      .getSequenceAsString();
    String idSeq = getDnaElement().getAccession().getID() + " - "
      + getDnaElement().getAccession().getID();

    try {

      if (sequence.length() >= RNAFoldConfiguration.SEQUENCE_MAX_SIZE) {
        String idAux;

        if (idSeq == null) {
          idAux = "*";
        } else {
          if (idSeq.length() > LENGTH_CHECK_AUX) {
            idAux = idSeq.substring(0, LENGTH_CHECK_AUX - 1);
          } else {
            idAux = idSeq;
          }
        }

        out.println(idAux + " - Error: Provided sequence exceeds size limit of "
          + RNAFoldConfiguration.SEQUENCE_MAX_SIZE + " nt.");
        getLatch().countDown();
        return null;
      }

      fold = new RNAFoldInterface(sequence, temperature, avoidLonelyPairs);

    } catch (Exception ex) {
      out.println("[" + idSeq + "] Error. Sequence Length: "
        + sequence.length());

      if (ex.getMessage() != null) {
        if (ex.getMessage().length() > 0) {
          out.println(this.dnaElement.getAccession() + " - RNAFold ERROR: "
            + ex.getMessage());
        } else {
          out.println(this.dnaElement.getAccession()
            + " - RNAFold unknown error.");
        }
      }

      return null;
    }

    if (fold.gotError()) {
      out.println("**RNAFold error**");
      return null;
    }

    return fold;
  }

  /**
   *
   */
  @Override
  public final void run() {

    RNAFoldInterface fold = new RNAFoldInterface();
    String currentPattern="";
    Iterator<String> patterns = getPatternItr();

    // sólo para modo dos
    if (isExtendedMode()) {
      fold = checkExtendedMode();
    }
    // III. Loop level
    while (patterns.hasNext()) {

      try{
      currentPattern = patterns.next().trim().toUpperCase();
      } catch(Exception e){
        out.println("err: "+e.getMessage());
      }
      // 1. Stem research
      if (this.inputType == InputSequence.VIENNA) {
        runViennaMode(currentPattern);
      } else {
        if (isExtendedMode()) {
          runExtendedMode(fold, currentPattern);
        } else {
          runNormalMode(currentPattern);
        }
      }
    }
    getLatch().countDown();
  }

  /**
   * @return the additionalSequence
   */
  public final String getAdditionalSequence() {
    return additionalSequence;
  }

  /**
   * @return the dnaElement
   */
  public final DNASequence getDnaElement() {
    return dnaElement;
  }

  /**
   * @return the inputType
   */
  public final InputSequence getInputType() {
    return inputType;
  }

  /**
   * @return the latch
   */
  public final CountDownLatch getLatch() {
    return latch;
  }

  /**
   * @return the maxLength
   */
  public final int getMaxLength() {
    return maxLength;
  }

  /**
   * @return the minLength
   */
  public final int getMinLength() {
    return minLength;
  }

  /**
   * @return the patternItr
   */
  public final Iterator<String> getPatternItr() {
    return patternItr;
  }

  /**
   * @return the writer
   */
  public final CSVWriter getWriter() {
    return writer;
  }

  /**
   * @return the extendedMode
   */
  public final boolean isExtendedMode() {
    return extendedMode;
  }

  /**
   * @return the searchReverse
   */
  public final boolean isSearchReverse() {
    return searchReverse;
  }

  /**
   * @param patternItr the patternItr to set
   */
  public final void setPatternItr(Iterator<String> patternItr) {
    this.patternItr = patternItr;
  }

  /**
   *
   * @return the MUTEX
   */
  public final static Semaphore getMUTEX() {
    return MUTEX;
  }

  /**
   * @return the SEM
   */
  public final static Semaphore getSEM() {
    return SEM;
  }

  /**
   * @return the started
   */
  public final static boolean isStarted() {
    return started;
  }

  /**
   * @param aMUTEX the MUTEX to set
   */
  public final static void setMUTEX( Semaphore aMUTEX) {
    MUTEX = aMUTEX;
  }

  /**
   * @param aSEM the SEM to set
   */
  public final static void setSEM( Semaphore aSEM) {
    SEM = aSEM;
  }

  /**
   * @param aStarted the started to set
   */
  public final static void setStarted( boolean aStarted) {
    started = aStarted;
  }

  /**
   *
   * @param latch
   */
  public final void setLatch( CountDownLatch latch) {
    this.latch = latch;
  }
}
