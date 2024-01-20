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

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author unknown
 */
public class TextAreaOutputStream
        extends OutputStream {

    private final byte[] oneByte;
    private Appender appender; // most recent action

    public TextAreaOutputStream(JTextArea textArea) {
        this(textArea, 1000);
    }

    public TextAreaOutputStream(JTextArea textArea, int maxLine) {
        if (maxLine < 1) {
            throw new 
        IllegalArgumentException("TextAreaOutputStream maximum lines must be " +
                "positive (value=" + maxLine + ")");
        }
        oneByte = new byte[1];
        appender = new Appender(textArea, maxLine);
    }

    /**
     * Clear the current console text area.
     */
    public synchronized void clear() {
        if (appender != null) {
            appender.clear();
        }
    }

    @Override
    public synchronized void close() {
        appender = null;
    }

    @Override
    public synchronized void flush() {
        // IDK why is empty
    }

    @Override
    public synchronized void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    @Override
    public synchronized void write(byte @NotNull [] ba) {
        write(ba, 0, ba.length);
    }

    @Override
    public synchronized void write(byte @NotNull [] ba, int str, int len) {
        if (appender != null) {
            appender.append(bytesToString(ba, str, len));
        }
    }

    private static String bytesToString(byte[] ba, int str, int len) {
        return new String(ba, str, len, StandardCharsets.UTF_8);
    }
} /* END PUBLIC CLASS */
