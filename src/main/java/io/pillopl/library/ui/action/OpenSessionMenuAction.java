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

import org.apache.logging.log4j.*;
import org.broad.igv.prefs.PreferencesManager;
import org.broad.igv.ui.IGV;
import org.broad.igv.ui.util.FileDialogUtils;
import org.broad.igv.util.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This menu action classes is used for both "Open Session ..." and load recent
 * session menu items.  In the "Open Session..." the user has to specify a
 * session file through the file menu.  For "load recent"  the action is
 * instantiated with a specific session file.
 *
 * @author jrobinso
 */
public class OpenSessionMenuAction extends MenuAction {

    private static Logger log = LogManager.getLogger(OpenSessionMenuAction.class);
    private IGV igv;
    private String sessionFile = null;
    private boolean autoload = false;

    public OpenSessionMenuAction(String label, String sessionFile, IGV igv) {
        super(label);
        this.sessionFile = sessionFile;
        this.igv = igv;
        autoload = true;
    }

    public OpenSessionMenuAction(String label, int mnemonic, IGV igv) {
        super(label, null, mnemonic);
        this.igv = igv;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (sessionFile == null || autoload == false) {
            File lastSessionDirectory = PreferencesManager.getPreferences().getLastTrackDirectory();
            File tmpFile = FileDialogUtils.chooseFile("Open Session", lastSessionDirectory, JFileChooser.FILES_ONLY);

            if (tmpFile == null) {
                return;
            }
            sessionFile = tmpFile.getAbsolutePath();
            PreferencesManager.getPreferences().setLastTrackDirectory(tmpFile.getParentFile());
        }
        doRestoreSession();


    }

    final public void doRestoreSession() {
        if (sessionFile != null) {
            if (FileUtils.isRemote(sessionFile)) {
                boolean merge = false;
                igv.doRestoreSession(sessionFile, null, merge);
            } else {
                File f = new File(sessionFile);
                igv.doRestoreSession(f, null);
            }
        }
    }

}

