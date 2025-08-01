/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2007-2015 Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.broad.igv.ui.action;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.logging.log4j.*;
import org.broad.igv.session.Session;
import org.broad.igv.session.SessionWriter;
import org.broad.igv.ui.IGV;
import org.broad.igv.util.LongRunningTask;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jrobinso
 */
public class ReloadTracksMenuAction extends MenuAction {

    static Logger log = LogManager.getLogger(SaveSessionMenuAction.class);
    IGV igv;

    /**
     * @param label
     * @param mnemonic
     * @param igv
     */
    public ReloadTracksMenuAction(String label, int mnemonic, IGV igv) {
        super(label, null, mnemonic);
        this.igv = igv;
    }

    /**
     * Method description
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String currentSessionFilePath = igv.getSession().getPath();
        Session currentSession = igv.getSession();
        currentSession.setPath(currentSessionFilePath);
        String xml = (new SessionWriter()).createXmlFromSession(currentSession, null);

        igv.resetSession(currentSessionFilePath);
        final InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        Runnable runnable = () -> {
            try {
                igv.restoreSessionFromStream(currentSessionFilePath, null, inputStream);
            } catch (IOException ex) {
                log.error("Error reloading tracks", ex);
            }
        };
        LongRunningTask.submit(runnable);
    }
}
