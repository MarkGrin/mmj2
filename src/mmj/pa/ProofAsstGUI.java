//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ProofAsstGUI.java  0.11 08/11/2013
 *
 * Version 0.02:
 * ==> Add renumber feature
 *
 * 09-Sep-2006 - Version 0.03 - TMFF enhancement.
 *
 * Version 0.04 06/01/2007
 * ==> misc.
 *
 * Version 0.05 08/01/2007
 * ==> Modified to not rebuild the RequestMessagesGUI frame
 *     each time. The user should position the screen and
 *     resize it so that it is visible underneath (or above)
 *     the ProofAsstGUI screen -- or just Alt-Tab to view
 *     any messages.
 *
 * Version 0.06 09/11/2007
 * ==> Bug fix -> set foreground/background at initialization.
 * ==> Modify setProofTextAreaCursorPos(ProofWorksheet w) to
 *     compute the column number of the ProofAsstCursor's
 *     fieldId.
 * ==> Added stuff for new "Set Indent" and
 *     "Reformat Proof: Swap Alt" menu items.
 *
 * Version 0.07 02/01/2008
 * ==> Add "accelerator" key definitions for
 *         Edit/Increase Font Size = Ctrl + "="
 *         Edit/Decrease Font Size = Ctrl + "-"
 *         Edit/Reformat Proof     = Ctrl + "R"
 *     Note: Ctrl + "+" seems to require Ctrl-Shift + "+",
 *           so in practice we code for Ctrl + "=", since
 *           "=" and "+" are most often on the same physical
 *           key and "=" is the unshifted glyph.
 *     Note: These Ctrl-Plus/Ctrl-Minus commands to increase/
 *           decrease font size are familiar to users of
 *           the Mozilla browser...
 * ==> Fix bug: Edit/Decrease Font Size now checks for
 *           minimum font size allowed (8) and does not
 *           allow further reductions (a request to go from
 *           8 to 6 is treated as a change from 8 to 8.) This
 *           bug manifested as 'Exception in thread
 *           "AWT-EventQueue-0" java.lang.ArithmeticException:
 *           / by zero at javax.swing.text.PlainView.paint(
 *           Unknown Source)'. Also added similar range checking
 *           for Edit/Increase Font Size.
 * ==> Modify request processing for unify and tmffReformat
 *     to pass offset of caret plus one as "inputCaretPos"
 *     for use in later caret positioning.
 * ==> Tweak: Do not reformat when format number or indent
 *            amount is changed. This allows for single step
 *            reformatting -- but requires that the user
 *            manually initiate reformatting after changing
 *            format number or indent amount.
 * ==> Add "Reformat Step" and "Reformat Step: Swap Alt" to
 *     popup menu. Then modified tmffReformat-related stuff
 *     to pass the boolean "inputCursorStep" to the standard
 *     reformatting procedure(s) so that the request can be
 *     handled using the regular, all-steps logic.
 * ==> Turn "Greetings, friend" literal into PaConstants
 *     constant, PROOF_ASST_GUI_STARTUP_MSG.
 * ==> Add "Incomplete Step Cursor" Edit menu item.
 *
 * Version 0.08 03/01/2008
 * ==> Add StepSelectorSearch to Unify menu
 * ==> Add "callback" function for use by StepSelectionDialog,
 *         proofAsstGUI.unifyWithStepSelectorChoice()
 * ==> Add Unify + Rederive to Unify menu
 * ==> Eliminate Unify + Get Hints from Unify Menu
 *
 * Version 0.09 08/01/2008
 * ==> Add TheoremLoader stuff.
 *
 * Version 0.10 - Nov-01-2011:  comment update.
 * ==> Add File Menu Export Via GMFF item
 * ==> Modified to use Cursor dontScroll flag
 * ==> Added Ctrl keys for File menu items:
 *     - fileOpenItem     = Ctrl-P
 *     - fileGetProofItem = Ctrl-G
 *
 * Version 0.11 - Aug-11-2013:
 * ==> Add Maximization on startup.
 */

package mmj.pa;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import mmj.lang.*;
import mmj.tl.*;
import mmj.tmff.TMFFConstants;
import mmj.tmff.TMFFException;

/**
 * The {@code ProofAsstGUI} class is the main user interface for the mmj2 Proof
 * Assistant feature.
 * <p>
 * A proof is represented in the GUI as a single text area, and the GUI knows
 * nothing about the contents inside; all work on the proof is done elsewhere
 * via mmj.pa.ProofAsst.java.
 * <p>
 * Note: ProofAsstGUI is single-threaded in the ProofAsst process which is
 * triggered in BatchMMJ2. The RunParm that triggers ProofAsstGUI does not
 * terminate until ProofAsstGUI terminates.
 * <p>
 * The main issues dealt with in the GUI have to do with doing all of the screen
 * updating code on the Java event thread. Unification is performed using a
 * separate thread which "calls back" to ProofAsstGUI when/if the Unificatin
 * process is complete. (As of February 2006, the longest theorem unification
 * computation is around 1/2 second.)
 */
public class ProofAsstGUI {

    // save constructor parms: proofAsst, proofAsstPreferences
    private final ProofAsst proofAsst;
    private final ProofAsstPreferences proofAsstPreferences;

    private TlPreferences tlPreferences;

    private JFrame mainFrame;

    private JTextArea proofTextArea;

    private JScrollPane proofTextScrollPane;

    private JSplitPane myPane;
    private JTextArea proofMessageArea;
    private JScrollPane proofMessageScrollPane;

    private String proofTheoremLabel = "";

    private ProofTextChanged proofTextChanged;
    private boolean savedSinceNew;

    private CompoundUndoManager undoManager;
    private JMenuItem editUndoItem;
    private JMenuItem editRedoItem;

    private JFileChooser fileChooser;
    private String screenTitle;

    private JFileChooser mmtFolderChooser;

    private JMenuItem cancelRequestItem;

    private JMenuItem fontStyleBoldItem;
    private JMenuItem fontStylePlainItem;

    private JPopupMenu popupMenu;

    private RequestThreadStuff requestThreadStuff;

    private Font proofFont;

    private RequestMessagesGUI requestMessagesGUI;

    private StepSelectorDialog stepSelectorDialog;
    private final ProofAsstGUI proofAsstGUI;

    /**
     * Sequence number of Proof Worksheet theorem.
     * <p>
     * Set to MObj.seq if proof theorem already exists. Otherwise, set to
     * LOC_AFTER stmt sequnce + 1 if LOC_AFTER input (else Integer.MAX_VALUE).
     */
    private int currProofMaxSeq = Integer.MAX_VALUE;

    /**
     * Get sequence number of Proof Worksheet theorem.
     * <p>
     * Equals MObj.seq if proof theorem already exists. Otherwise, set to
     * LOC_AFTER stmt sequnce + 1 if LOC_AFTER input (else Integer.MAX_VALUE).
     * 
     * @return currProofMaxSeq number of Proof Worksheet theorem.
     */
    public int getCurrProofMaxSeq() {
        return currProofMaxSeq;
    }

    /**
     * Set sequence number of Proof Worksheet theorem.
     * <p>
     * Equals MObj.seq if proof theorem already exists. Otherwise, set to
     * LOC_AFTER stmt sequnce + 1 if LOC_AFTER input (else Integer.MAX_VALUE).
     * 
     * @param currProofMaxSeq number of Proof Worksheet theorem.
     */
    public void setCurrProofMaxSeq(final int currProofMaxSeq) {
        this.currProofMaxSeq = currProofMaxSeq;
    }

    /**
     * Default constructor used only in test mode when ProofAsstGUI invoked
     * directly from command line.
     */
    public ProofAsstGUI() {
        proofAsst = null;
        proofAsstPreferences = new ProofAsstPreferences();
        proofAsstGUI = this;

        buildFileChooser(new File(PaConstants.SAMPLE_PROOF_LABEL
            + proofAsstPreferences.getDefaultFileNameSuffix()));

        updateScreenTitle(fileChooser.getSelectedFile());

        buildGUI(PaConstants.SAMPLE_PROOF_TEXT);
    }

    /**
     * Normal constructor for setting up ProofAsstGUI.
     * 
     * @param proofAsst ProofAsst object
     * @param proofAsstPreferences variable settings
     * @param theoremLoader mmj.tl.TheoremLoader object
     */
    public ProofAsstGUI(final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final TheoremLoader theoremLoader)
    {

        this.proofAsst = proofAsst;
        this.proofAsstPreferences = proofAsstPreferences;

        tlPreferences = theoremLoader.getTlPreferences();
        buildMMTFolderChooser();

        proofAsstGUI = this;

        final File startupProofWorksheetFile = proofAsstPreferences
            .getStartupProofWorksheetFile();

        if (startupProofWorksheetFile == null) {
            if (proofAsstPreferences.getProofFolder() != null)
                buildFileChooser(new File(
                    proofAsstPreferences.getProofFolder(),
                    PaConstants.SAMPLE_PROOF_LABEL
                        + proofAsstPreferences.getDefaultFileNameSuffix()));
            else
                buildFileChooser(new File(PaConstants.SAMPLE_PROOF_LABEL
                    + proofAsstPreferences.getDefaultFileNameSuffix()));
            updateScreenTitle(fileChooser.getSelectedFile());
            buildGUI(PaConstants.SAMPLE_PROOF_TEXT);
        }
        else {
            buildFileChooser(startupProofWorksheetFile);
            updateScreenTitle(fileChooser.getSelectedFile());
            buildGUI(readProofTextFromFile(startupProofWorksheetFile));
        }
    }

    public void unifyWithStepSelectorChoice(final StepRequest stepRequest) {

        startUnificationAction(false, // no renum
            false, // convert work vars
            null, // no preprocess request
            stepRequest, // s/b SELECTOR_CHOICE
            null); // no TL Request
    }

    public boolean newGeneralSearch(final String s) {
        return startRequestAction(new RequestNewGeneralSearch(s));
    }

    public boolean searchAndShowResults() {
        return startRequestAction(new RequestSearchAndShowResults());
    }

    public boolean refineAndShowResults() {
        return startRequestAction(new RequestRefineAndShowResults());
    }

    public boolean reshowSearchOptions() {
        return startRequestAction(new RequestReshowSearchOptions());
    }

    public boolean reshowSearchResults() {
        return startRequestAction(new RequestReshowSearchResults());
    }

    public boolean reshowProofAsstGUI() {
        return startRequestAction(new RequestReshowProofAsstGUI());
    }

    public boolean searchOptionsPlusButton() {
        return startRequestAction(new RequestSearchOptionsPlusButton());
    }

    public boolean searchOptionsMinusButton() {
        return startRequestAction(new RequestSearchOptionsMinusButton());
    }

    public boolean searchResultsPlusButton() {
        return startRequestAction(new RequestSearchResultsPlusButton());
    }

    public boolean searchResultsMinusButton() {
        return startRequestAction(new RequestSearchResultsMinusButton());
    }

    private void buildGUI(final String newProofText) {
        displayRequestMessagesGUI(PaConstants.PROOF_ASST_GUI_STARTUP_MSG);

        JFrame.setDefaultLookAndFeelDecorated(true);

        mainFrame = buildMainFrame();

        final JMenuBar menuBar = buildProofMenuBar();

        mainFrame.setJMenuBar(menuBar);

        popupMenu = buildPopupMenu();

        myPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        if (proofAsstPreferences.getTextAtTop()) {
            buildGUIProofTextStuff(newProofText);
            buildGUIMessageTextStuff();
            myPane.setResizeWeight(1);
        }
        else {
            buildGUIMessageTextStuff();
            buildGUIProofTextStuff(newProofText);
        }

        mainFrame.getContentPane().add(myPane);

    }

    private void buildGUIProofTextStuff(final String newProofText) {
        proofTextArea = buildProofTextArea(newProofText);

        proofTextScrollPane = buildProofTextScrollPane(proofTextArea);

        proofTextArea.addMouseListener(new PopupMenuListener(proofTextArea));

        proofTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2)
                    unifyWithStepSelectorChoice(new StepRequest(
                        PaConstants.STEP_REQUEST_SELECTOR_SEARCH));
            }
        });

        /* workaround - otherwise ctrl-H will act like backspace */
        proofTextArea.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK), "");

        myPane.add(proofTextScrollPane);

    }

    private void buildGUIMessageTextStuff() {
        proofMessageArea = buildProofMessageArea(PaConstants.PROOF_ASST_GUI_STARTUP_MSG);

        proofMessageScrollPane = buildProofMessageScrollPane(proofMessageArea);

        proofMessageArea.addMouseListener(new PopupMenuListener(
            proofMessageArea));

        myPane.add(proofMessageScrollPane);
    }

    private void updateMainFrameTitle() {
        mainFrame.setTitle(screenTitle);
    }

    private void buildMMTFolderChooser() {
        final MMTFolder mmtFolder = tlPreferences.getMMTFolder();
        if (mmtFolder.getFolderFile() == null)
            mmtFolderChooser = new JFileChooser();
        else {
            mmtFolderChooser = new JFileChooser(mmtFolder.getFolderFile());
            mmtFolderChooser.setSelectedFile(mmtFolder.getFolderFile());
        }

        mmtFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        mmtFolderChooser.setAcceptAllFileFilterUsed(false);
    }

    private void buildFileChooser(final File defaultFile) {

        fileChooser = new JFileChooser(proofAsstPreferences.getProofFolder());

        fileChooser.addChoosableFileFilter(new proofAsstFileFilter());

        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.setSelectedFile(defaultFile);
    }

    private void updateFileChooserFileForProofLabel(final String label) {
        final File prevFile = fileChooser.getSelectedFile();

        String prevParent = null;

        if (prevFile != null)
            prevParent = prevFile.getParent();

        final File newFile = new File(prevParent, label
            + proofAsstPreferences.getDefaultFileNameSuffix());

        fileChooser.setSelectedFile(newFile);

    }

    private class proofAsstFileFilter extends
        javax.swing.filechooser.FileFilter
    {
        @Override
        public boolean accept(final File file) {
            if (file.isDirectory())
                return true;
            final String fileName = file.getName();
            if (fileName != null
                && (fileName
                    .endsWith(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP)
                    || fileName
                        .endsWith(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP2)
                    || fileName
                        .endsWith(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT) || fileName
                        .endsWith(PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT2)))
                return true;
            return false;
        }
        @Override
        public String getDescription() {
            return PaConstants.PA_GUI_FILE_CHOOSER_DESCRIPTION;
        }

    }

    private class ProofTextChanged implements DocumentListener {
        boolean changes;

        public ProofTextChanged(final boolean changes) {
            this.changes = changes;
        }
        public synchronized boolean getChanges() {
            return changes;
        }
        public synchronized void setChanges(final boolean changes) {
            this.changes = changes;
        }
        public void changedUpdate(final DocumentEvent e) {
            setChanges(true);
        }
        public void insertUpdate(final DocumentEvent e) {
            setChanges(true);
        }
        public void removeUpdate(final DocumentEvent e) {
            setChanges(true);
        }
    }

    private void clearUndoRedoCaches() {
        if (proofAsstPreferences.getUndoRedoEnabled()) {
            undoManager.discardAllEdits();
            updateUndoRedoItems();
        }
    }

    private void updateUndoRedoItems() {
        editUndoItem.setEnabled(undoManager.canUndo());
        editRedoItem.setEnabled(undoManager.canRedo());
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
    private String getProofTextAreaText() {
        return proofTextArea.getText();
    }
    private void setProofTextAreaText(final String s) {
        undoManager.updateCursorPosition();
        proofTextArea.setText(s);
    }

    private void setProofTextAreaCursorPos(final ProofWorksheet w,
        final int proofTextLength)
    {

        final ProofAsstCursor cursor = w.getProofCursor();

        if (cursor.proofWorkStmt != null) {

            final int n = w.computeProofWorkStmtLineNbr(cursor.proofWorkStmt);

            if (n < 1) {
                // default it
                cursor.caretLine = 1;
                cursor.caretCol = 1;
            }
            else {
                cursor.caretLine = n;
                cursor.caretCol = cursor.proofWorkStmt
                    .computeFieldIdCol(cursor.fieldId);
            }

            cursor.proofWorkStmt = null;
            cursor.caretCharNbr = -1; // just in case
            cursor.scrollToLine = -1; // just in case
            cursor.scrollToCol = -1; // just in case
        }

        setProofTextAreaCursorPos(cursor, proofTextLength);
    }

    private void setProofTextAreaCursorPos(final ProofAsstCursor cursor,
        final int proofTextLength)
    {
        try {

            int caretPosition = 0;
            int row = 0;
            int col = 0;

            if (cursor.caretCharNbr > 0)
                caretPosition = cursor.caretCharNbr - 1;
            else if (cursor.caretLine > 0) {
                row = cursor.caretLine - 1;
                if (cursor.caretCol > 0)
                    col = cursor.caretCol - 1;
                else
                    col = 0;
                final int offset = proofTextArea.getLineStartOffset(row);
                caretPosition = offset + col;
            }

            // just to be safe instead of sorry...
            if (caretPosition >= proofTextLength)
                caretPosition = proofTextLength - 1;
            if (caretPosition < 0)
                caretPosition = 0;

            proofTextArea.setCaretPosition(caretPosition);

            final JViewport v = proofTextScrollPane.getViewport();

            final int vHeight = v.getView().getHeight();

            row = 0;
            col = 0;

            if (cursor.scrollToLine != cursor.caretLine
                && cursor.scrollToLine > 0 && cursor.caretLine > 0)
                row = cursor.scrollToLine - 1;
            else if (cursor.caretCharNbr > 0)
                row = proofTextArea.getLineOfOffset(cursor.caretCharNbr - 1);
            else if (cursor.caretLine > 0)
                row = cursor.caretLine - 1;

            if (cursor.scrollToCol > 0)
                col = cursor.scrollToCol - 1;

            final int vPos = vHeight * row / proofTextArea.getLineCount();

            if (cursor.getDontScroll())
                cursor.setDontScroll(false);
            else
                v.scrollRectToVisible(new Rectangle(col, // x
                    vPos, // y
                    1, // width
                    1)); // height
        } catch (final Exception e) {
            // ignore, don't care, did our best.
        }
    }

    private void updateScreenTitle(final File file) {
        screenTitle = buildScreenTitle(file);
    }

    /*
     * Build title using ProofAsstGUI caption + full path name.
     * If title length > textColumns - 15
     *     build title using ProofAsstGUI caption + just file name
     *         if title length > textColumns - 15
     *             build title using just file name
     *             if title length > textColumns - 15
     *                 build title using just ProofAsstGUI caption.
     */
    private String buildScreenTitle(final File file) {
        final int maxLength = proofAsstPreferences.getTextColumns() - 15;

        if (file == null || file.getName().length() > maxLength)
            return PaConstants.PROOF_ASST_FRAME_TITLE;

        final StringBuilder s = new StringBuilder(maxLength);

        s.append(PaConstants.PROOF_ASST_FRAME_TITLE);

        if (appendToScreenTitle(s, " - ") < 0)
            return s.toString();

        if (appendToScreenTitle(s, file.getPath()) < 0)
            if (appendToScreenTitle(s, file.getName()) < 0)
                return file.getName();
        return s.toString();
    }
    private int appendToScreenTitle(final StringBuilder s, final String t) {
        if (t.length() > s.capacity())
            return -1;
        s.append(t);
        return 0;
    }

    private JFrame buildMainFrame() {

        final JFrame frame = new JFrame(screenTitle);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                if (saveIfAskedBeforeExit(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_EXIT) == JOptionPane.CANCEL_OPTION)
                    return;
                System.exit(0);
            }
        });

        return frame;
    }

    private JTextArea buildProofMessageArea(final String text) {
        final JTextArea textArea = new JTextArea(text,
            proofAsstPreferences.getErrorMessageRows(),
            proofAsstPreferences.getErrorMessageColumns());
        final Font frameFont = new Font(PaConstants.AUX_FRAME_FONT_FAMILY,
            Font.BOLD, proofAsstPreferences.getFontSize());

        textArea.setFont(frameFont);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);

        /* workaround - otherwise ctrl-H will act like backspace */
        proofTextArea.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK), "");
        return textArea;
    }
    private JScrollPane buildProofMessageScrollPane(
        final JTextArea proofMessageArea)
    {
        final JScrollPane scrollPane = new JScrollPane(proofMessageArea);
        scrollPane
            .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    private JTextArea buildProofTextArea(final String text) {

        final JTextArea textArea = new JTextArea(text,
            proofAsstPreferences.getTextRows(),
            proofAsstPreferences.getTextColumns());

        buildProofFont();

        textArea.setFont(proofFont);
        textArea.setLineWrap(proofAsstPreferences.getLineWrap());
        textArea.setCursor(null); // use arrow instead of thingamabob
        textArea.setTabSize(PaConstants.PROOF_TEXT_TAB_LENGTH); // disable it
                                                                // using 1.
        textArea.setForeground(proofAsstPreferences.getForegroundColor());
        textArea.setBackground(proofAsstPreferences.getBackgroundColor());

        proofTextChanged = new ProofTextChanged(false);
        textArea.getDocument().addDocumentListener(proofTextChanged);

        if (proofAsstPreferences.getUndoRedoEnabled())
            undoManager = new CompoundUndoManager(textArea, new Runnable() {
                public void run() {
                    updateUndoRedoItems();
                }
            });

        savedSinceNew = false;

        return textArea;
    }

    private void buildProofFont() {
        if (proofAsstPreferences.getFontBold())
            proofFont = new Font(proofAsstPreferences.getFontFamily(),
                Font.BOLD, proofAsstPreferences.getFontSize());
        else
            proofFont = new Font(proofAsstPreferences.getFontFamily(),
                Font.PLAIN, proofAsstPreferences.getFontSize());
    }

    private JScrollPane buildProofTextScrollPane(final JTextArea proofTextArea)
    {

        final JScrollPane scrollPane = new JScrollPane(proofTextArea);

        scrollPane
            .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        return scrollPane;
    }

    private JPopupMenu buildPopupMenu() {
        final JPopupMenu m = new JPopupMenu();

        JMenuItem i;

        i = new JMenuItem(new DefaultEditorKit.CutAction());
        i.setText(PaConstants.PA_GUI_EDIT_MENU_CUT_ITEM_TEXT);
        m.add(i);

        i = new JMenuItem(new DefaultEditorKit.CopyAction());
        i.setText(PaConstants.PA_GUI_EDIT_MENU_COPY_ITEM_TEXT);
        m.add(i);

        i = new JMenuItem(new DefaultEditorKit.PasteAction());
        i.setText(PaConstants.PA_GUI_EDIT_MENU_PASTE_ITEM_TEXT);
        m.add(i);

        i = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doTMFFReformatAction(true, false);
            }
        });
        i.setText(PaConstants.PA_GUI_POPUP_MENU_REFORMAT_STEP_TEXT);
        m.add(i);

        i = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doTMFFReformatAction(true, true);
            }
        });
        i.setText(PaConstants.PA_GUI_POPUP_MENU_REFORMAT_SWAP_ALT_STEP_TEXT);
        m.add(i);

        i = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                unifyWithStepSelectorChoice(new StepRequest(
                    PaConstants.STEP_REQUEST_SELECTOR_SEARCH));
            }
        });
        i.setText(PaConstants.PA_GUI_UNIFY_MENU_STEP_SELECTOR_SEARCH_ITEM_TEXT);
        m.add(i);

        i = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                reshowStepSelectorDialogAction();
            }
        });
        i.setText(PaConstants.PA_GUI_UNIFY_MENU_RESHOW_STEP_SELECTOR_DIALOG_ITEM_TEXT);
        m.add(i);
        i = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doSearchOptionsItemAction();
            }
        });
        i.setText(PaConstants.SEARCH_OPTIONS_ITEM_TEXT);
        m.add(i);
        i = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doStepSearchItemAction();
            }
        });
        i.setText(PaConstants.STEP_SEARCH_ITEM_TEXT);
        m.add(i);
        i = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent actionevent) {
                doGeneralSearchItemAction();
            }
        });
        i.setText(PaConstants.GENERAL_SEARCH_ITEM_TEXT);
        m.add(i);

        return m;
    }

    private class PopupMenuListener extends MouseAdapter {
        JTextComponent source;

        public PopupMenuListener(final JTextComponent source) {
            this.source = source;
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            popupMenuForMouse(e);
        }
        @Override
        public void mouseReleased(final MouseEvent e) {
            popupMenuForMouse(e);
        }
        public void popupMenuForMouse(final MouseEvent e) {
            if (e.isPopupTrigger()) {
                source.setCaretPosition(source.viewToModel(e.getPoint()));
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private JMenuBar buildProofMenuBar() {
        final JMenuBar m = new JMenuBar();
        m.add(buildFileMenu());
        m.add(buildEditMenu());
        m.add(buildCancelMenu());
        m.add(buildUnifyMenu());
        m.add(buildSearchMenu());
        m.add(buildTLMenu());
        m.add(buildGMFFMenu());
        m.add(buildHelpMenu());
        return m;
    }

    private JMenu buildFileMenu() {

        final JMenu fileMenu = new JMenu(PaConstants.PA_GUI_FILE_MENU_TITLE);
        fileMenu.setMnemonic(KeyEvent.VK_F);

        final JMenuItem fileSaveItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileSaveAction(false);
            }
        });
        fileSaveItem.setText(PaConstants.PA_GUI_FILE_MENU_SAVE_ITEM_TEXT);
        fileSaveItem.setMnemonic(KeyEvent.VK_S);
        fileSaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
            ActionEvent.CTRL_MASK));
        fileMenu.add(fileSaveItem);

        final JMenuItem fileNewItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileNewAction();
            }
        });
        fileNewItem.setText(PaConstants.PA_GUI_FILE_MENU_NEW_ITEM_TEXT);
        fileNewItem.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(fileNewItem);

        final JMenuItem fileNewNextItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileNewNextAction();
            }
        });
        fileNewNextItem
            .setText(PaConstants.PA_GUI_FILE_MENU_NEW_NEXT_ITEM_TEXT);
        fileNewNextItem.setMnemonic(KeyEvent.VK_E);
        fileMenu.add(fileNewNextItem);

        final JMenuItem fileOpenItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileOpenAction();
            }
        });
        fileOpenItem.setText(PaConstants.PA_GUI_FILE_MENU_OPEN_ITEM_TEXT);
        fileOpenItem.setMnemonic(KeyEvent.VK_P);
        fileOpenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
            ActionEvent.CTRL_MASK));
        fileMenu.add(fileOpenItem);

        final JMenuItem fileGetProofItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileGetProofAction();
            }
        });
        fileGetProofItem
            .setText(PaConstants.PA_GUI_FILE_MENU_GET_PROOF_ITEM_TEXT);
        fileGetProofItem.setMnemonic(KeyEvent.VK_G);
        fileGetProofItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
            ActionEvent.CTRL_MASK));
        fileMenu.add(fileGetProofItem);

        final JMenuItem fileGetFwdProofItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doFileGetFwdProofAction();
                }
            });
        fileGetFwdProofItem
            .setText(PaConstants.PA_GUI_FILE_MENU_GET_FWD_PROOF_ITEM_TEXT);
        fileGetFwdProofItem.setMnemonic(KeyEvent.VK_F);
        fileGetFwdProofItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        fileMenu.add(fileGetFwdProofItem);

        final JMenuItem fileGetBwdProofItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doFileGetBwdProofAction();
                }
            });
        fileGetBwdProofItem
            .setText(PaConstants.PA_GUI_FILE_MENU_GET_BWD_PROOF_ITEM_TEXT);
        fileGetBwdProofItem.setMnemonic(KeyEvent.VK_B);
        fileGetBwdProofItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_B, ActionEvent.CTRL_MASK));
        fileMenu.add(fileGetBwdProofItem);

        final JMenuItem fileCloseItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileCloseAction();
            }
        });
        fileCloseItem.setText(PaConstants.PA_GUI_FILE_MENU_CLOSE_ITEM_TEXT);
        fileCloseItem.setMnemonic(KeyEvent.VK_L);
        fileMenu.add(fileCloseItem);

        final JMenuItem fileSaveAsItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileSaveAsAction();
            }
        });
        fileSaveAsItem.setText(PaConstants.PA_GUI_FILE_MENU_SAVE_AS_ITEM_TEXT);
        fileSaveAsItem.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(fileSaveAsItem);

        final JMenuItem fileExportViaGMFFItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doFileExportViaGMFFAction();
                }
            });
        fileExportViaGMFFItem
            .setText(PaConstants.PA_GUI_FILE_MENU_EXPORT_VIA_GMFF_ITEM_TEXT);
        fileExportViaGMFFItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_1, ActionEvent.CTRL_MASK));

        fileMenu.add(fileExportViaGMFFItem);

        final JMenuItem fileExitItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doFileExitAction();
            }
        });
        fileExitItem.setText(PaConstants.PA_GUI_FILE_MENU_EXIT_ITEM_TEXT);
        fileExitItem.setMnemonic(KeyEvent.VK_X);
        fileExitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
            ActionEvent.CTRL_MASK));
        fileMenu.add(fileExitItem);

        return fileMenu;
    }

    private JMenu buildEditMenu() {
        final JMenu editMenu = new JMenu(PaConstants.PA_GUI_EDIT_MENU_TITLE);
        editMenu.setMnemonic(KeyEvent.VK_E);

        if (proofAsstPreferences.getUndoRedoEnabled()) {
            editUndoItem = new JMenuItem(new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    startRequestAction(new RequestEditUndo());
                }
            });
            editUndoItem.setText(PaConstants.PA_GUI_EDIT_MENU_UNDO_ITEM_TEXT);
            editUndoItem.setMnemonic(KeyEvent.VK_U);
            editUndoItem.setEnabled(false);
            editUndoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                ActionEvent.CTRL_MASK));
            editMenu.add(editUndoItem);

            editRedoItem = new JMenuItem(new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    startRequestAction(new RequestEditRedo());
                }
            });
            editRedoItem.setText(PaConstants.PA_GUI_EDIT_MENU_REDO_ITEM_TEXT);
            editRedoItem.setMnemonic(KeyEvent.VK_R);
            editRedoItem.setEnabled(false);
            editRedoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                ActionEvent.CTRL_MASK));
            editMenu.add(editRedoItem);
        }

        final JMenuItem cutItem = new JMenuItem(
            new DefaultEditorKit.CutAction());
        cutItem.setText(PaConstants.PA_GUI_EDIT_MENU_CUT_ITEM_TEXT);
        cutItem.setMnemonic(KeyEvent.VK_T);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            ActionEvent.CTRL_MASK));
        editMenu.add(cutItem);

        final JMenuItem copyItem = new JMenuItem(
            new DefaultEditorKit.CopyAction());
        copyItem.setText(PaConstants.PA_GUI_EDIT_MENU_COPY_ITEM_TEXT);
        copyItem.setMnemonic(KeyEvent.VK_C);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            ActionEvent.CTRL_MASK));
        editMenu.add(copyItem);

        final JMenuItem pasteItem = new JMenuItem(
            new DefaultEditorKit.PasteAction());
        pasteItem.setText(PaConstants.PA_GUI_EDIT_MENU_PASTE_ITEM_TEXT);
        pasteItem.setMnemonic(KeyEvent.VK_P);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
            ActionEvent.CTRL_MASK));
        editMenu.add(pasteItem);

        final JMenuItem setIncompleteStepCursorItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetIncompleteStepCursorItemAction();
                }
            });
        setIncompleteStepCursorItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_SET_INCOMPLETE_STEP_CURSOR_ITEM_TEXT);
        editMenu.add(setIncompleteStepCursorItem);

        final JMenuItem setSoftDjErrorItem = new JMenuItem(new AbstractAction()
        {
            public void actionPerformed(final ActionEvent e) {
                doSetSoftDjErrorItemAction();
            }
        });
        setSoftDjErrorItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_SET_SOFT_DJ_ERROR_ITEM_TEXT);
        editMenu.add(setSoftDjErrorItem);

        final JMenuItem setFontFamilyItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doSetFontFamilyItemAction();
            }
        });
        setFontFamilyItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_SET_FONT_FAMILY_ITEM_TEXT);
        editMenu.add(setFontFamilyItem);

        fontStyleBoldItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                if (!proofAsstPreferences.getFontBold()) {
                    proofAsstPreferences.setFontBold(true);
                    fontStyleBoldItem.setEnabled(false);
                    fontStylePlainItem.setEnabled(true);
                    updateFrameFont(proofFont.deriveFont(Font.BOLD));
                }

            }
        });
        fontStyleBoldItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_FONT_STYLE_BOLD_ITEM_TEXT);
        if (proofAsstPreferences.getFontBold())
            fontStyleBoldItem.setEnabled(false);
        else
            fontStyleBoldItem.setEnabled(true);
        editMenu.add(fontStyleBoldItem);

        fontStylePlainItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                if (proofAsstPreferences.getFontBold()) {
                    proofAsstPreferences.setFontBold(false);
                    fontStylePlainItem.setEnabled(false);
                    fontStyleBoldItem.setEnabled(true);
                    updateFrameFont(proofFont.deriveFont(Font.PLAIN));
                }

            }
        });
        fontStylePlainItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_FONT_STYLE_PLAIN_ITEM_TEXT);
        if (proofAsstPreferences.getFontBold())
            fontStylePlainItem.setEnabled(true);
        else
            fontStylePlainItem.setEnabled(false);
        editMenu.add(fontStylePlainItem);

        final JMenuItem largerFontItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                int fontSize = proofAsstPreferences.getFontSize()
                    + PaConstants.PROOF_ASST_FONT_SIZE_CHG_AMT;

                // 2007-12-06 - FIX BUG
                if (fontSize > PaConstants.PROOF_ASST_FONT_SIZE_MAX)
                    fontSize = PaConstants.PROOF_ASST_FONT_SIZE_MAX;

                proofAsstPreferences.setFontSize(fontSize);
                updateFrameFont(proofFont.deriveFont((float)fontSize));

            }
        });
        largerFontItem.setText(PaConstants.PA_GUI_EDIT_MENU_INC_FONT_ITEM_TEXT);
        largerFontItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
        editMenu.add(largerFontItem);

        final JMenuItem smallerFontItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                int fontSize = proofAsstPreferences.getFontSize()
                    - PaConstants.PROOF_ASST_FONT_SIZE_CHG_AMT;

                // 2007-12-06 - FIX BUG
                if (fontSize < PaConstants.PROOF_ASST_FONT_SIZE_MIN)
                    fontSize = PaConstants.PROOF_ASST_FONT_SIZE_MIN;

                proofAsstPreferences.setFontSize(fontSize);
                updateFrameFont(proofFont.deriveFont((float)fontSize));
            }
        });
        smallerFontItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_DEC_FONT_ITEM_TEXT);
        smallerFontItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        editMenu.add(smallerFontItem);

        final JMenuItem setForegroundColorItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetForegroundColorItemAction();
                }
            });
        setForegroundColorItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_SET_FOREGROUND_ITEM_TEXT);
        editMenu.add(setForegroundColorItem);

        final JMenuItem setBackgroundColorItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetBackgroundColorItemAction();
                }
            });
        setBackgroundColorItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_SET_BACKGROUND_ITEM_TEXT);
        editMenu.add(setBackgroundColorItem);

        final JMenuItem setFormatNbrItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doSetFormatNbrItemAction();
            }
        });
        setFormatNbrItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_SET_FORMAT_NBR_ITEM_TEXT);
        editMenu.add(setFormatNbrItem);

        final JMenuItem setIndentItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doSetIndentItemAction();
            }
        });
        setIndentItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_SET_INDENT_ITEM_TEXT);
        editMenu.add(setIndentItem);

        final JMenuItem reformatItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doTMFFReformatAction(false, false);
            }
        });
        reformatItem.setText(PaConstants.PA_GUI_EDIT_MENU_REFORMAT_ITEM_TEXT);
        reformatItem.setMnemonic(KeyEvent.VK_R);
        reformatItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            ActionEvent.CTRL_MASK));
        editMenu.add(reformatItem);

        final JMenuItem reformatSwapAltItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doTMFFReformatAction(false, true);
                }
            });
        reformatSwapAltItem
            .setText(PaConstants.PA_GUI_EDIT_MENU_REFORMAT_SWAP_ALT_ITEM_TEXT);
        reformatSwapAltItem.setMnemonic(KeyEvent.VK_O);
        reformatSwapAltItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        editMenu.add(reformatSwapAltItem);

        return editMenu;
    }

    private void updateFrameFont(final Font font) {
        proofFont = font;
        proofTextArea.setFont(font);
        proofMessageArea.setFont(font);
        mainFrame.pack();
    }

    private JMenu buildCancelMenu() {

        final JMenu cancelMenu = new JMenu(PaConstants.PA_GUI_CANCEL_MENU_TITLE);
        cancelMenu.setMnemonic(KeyEvent.VK_C);

        cancelRequestItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                cancelRequestAction();
            }
        });
        cancelRequestItem
            .setText(PaConstants.PA_GUI_CANCEL_MENU_KILL_ITEM_TEXT);
        cancelRequestItem.setMnemonic(KeyEvent.VK_K);
        cancelRequestItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
            ActionEvent.CTRL_MASK));
        cancelRequestItem.setEnabled(false);
        cancelMenu.add(cancelRequestItem);

        return cancelMenu;
    }

    private JMenu buildUnifyMenu() {

        final JMenu unifyMenu = new JMenu(PaConstants.PA_GUI_UNIFY_MENU_TITLE);
        unifyMenu.setMnemonic(KeyEvent.VK_U);

        final JMenuItem startUnificationItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    startUnificationAction(false, // no renum
                        false, // convert work vars
                        null, // no preprocess request
                        null, // no Step Request
                        null); // no TL Request

                }
            });
        startUnificationItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_START_ITEM_TEXT);
        startUnificationItem.setMnemonic(KeyEvent.VK_U);
        startUnificationItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        unifyMenu.add(startUnificationItem);

        final JMenuItem startUnifyWRenumItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    startUnificationAction(true, // yes, renum
                        false, // convert work vars
                        null, // no preprocess request
                        null, // no Step Request
                        null); // no TL Request
                }
            });
        startUnifyWRenumItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_START_UR_ITEM_TEXT);
        startUnifyWRenumItem.setMnemonic(KeyEvent.VK_R);
        unifyMenu.add(startUnifyWRenumItem);

        final JMenuItem startUnifyWRederiveItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    startUnificationAction(true, // yes, renum
                        false, // convert work vars
                        new EraseWffsPreprocessRequest(), //
                        null, // no Step Request
                        null); // no TL Request
                }
            });
        startUnifyWRederiveItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_REDERIVE_ITEM_TEXT);
        startUnifyWRederiveItem.setMnemonic(KeyEvent.VK_E);
        unifyMenu.add(startUnifyWRederiveItem);

        final JMenuItem startUnifyWNoConvertItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    startUnificationAction(true, // yes, renum
                        true, // don't convert work vars
                        null, // no preprocess request
                        null, // no Step Request
                        null); // no TL Request
                }
            });
        startUnifyWNoConvertItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_NO_WV_ITEM_TEXT);
        startUnifyWNoConvertItem.setMnemonic(KeyEvent.VK_W);
        startUnifyWNoConvertItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_U, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        unifyMenu.add(startUnifyWNoConvertItem);

        final JMenuItem startUnifyEraseNoConvertItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    startUnificationAction(true, // yes, renum
                        true, // don't convert work vars
                        new EraseWffsPreprocessRequest(), //
                        null, // no Step Request
                        null); // no TL Request
                }
            });
        startUnifyEraseNoConvertItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_ERASE_NO_WV_ITEM_TEXT);
        startUnifyEraseNoConvertItem.setMnemonic(KeyEvent.VK_Y);
        unifyMenu.add(startUnifyEraseNoConvertItem);

        final JMenuItem startUnifyWStepSelectorSearchItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    unifyWithStepSelectorChoice(new StepRequest(
                        PaConstants.STEP_REQUEST_SELECTOR_SEARCH));
                }
            });
        startUnifyWStepSelectorSearchItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_STEP_SELECTOR_SEARCH_ITEM_TEXT);
        startUnifyWStepSelectorSearchItem.setMnemonic(KeyEvent.VK_S);
        startUnifyWStepSelectorSearchItem.setAccelerator(KeyStroke
            .getKeyStroke(KeyEvent.VK_8, ActionEvent.CTRL_MASK));
        unifyMenu.add(startUnifyWStepSelectorSearchItem);

        // clone of startUnificationItem
        final JMenuItem reshowStepSelectorDialogItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    reshowStepSelectorDialogAction();
                }
            });
        reshowStepSelectorDialogItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_RESHOW_STEP_SELECTOR_DIALOG_ITEM_TEXT);
        reshowStepSelectorDialogItem.setMnemonic(KeyEvent.VK_D);
        reshowStepSelectorDialogItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_9, ActionEvent.CTRL_MASK));
        unifyMenu.add(reshowStepSelectorDialogItem);

        final JMenuItem setMaxResultsItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doSetMaxResultsItemAction();
            }
        });
        setMaxResultsItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_SET_MAX_RESULTS_ITEM_TEXT);
        unifyMenu.add(setMaxResultsItem);

        final JMenuItem setShowSubstitutionsItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetShowSubstitutionsItemAction();
                }
            });
        setShowSubstitutionsItem
            .setText(PaConstants.PA_GUI_UNIFY_MENU_SET_SHOW_SUBST_ITEM_TEXT);
        unifyMenu.add(setShowSubstitutionsItem);

        return unifyMenu;
    }

    private JMenu buildSearchMenu() {
        final JMenu searchMenu = new JMenu(PaConstants.PA_GUI_SEARCH_MENU_TITLE);
        searchMenu.setMnemonic(KeyEvent.VK_S);

        final JMenuItem searchOptionsItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doSearchOptionsItemAction();
            }
        });
        searchOptionsItem.setMnemonic(KeyEvent.VK_O);
        searchOptionsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
            ActionEvent.CTRL_MASK));
        searchOptionsItem.setText(PaConstants.SEARCH_OPTIONS_ITEM_TEXT);
        searchMenu.add(searchOptionsItem);

        final JMenuItem stepSearchItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doStepSearchItemAction();
            }
        });
        stepSearchItem.setMnemonic(KeyEvent.VK_S);
        stepSearchItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
            ActionEvent.CTRL_MASK));
        stepSearchItem.setText(PaConstants.STEP_SEARCH_ITEM_TEXT);
        searchMenu.add(stepSearchItem);

        final JMenuItem generalSearchItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent actionevent) {
                doGeneralSearchItemAction();
            }
        });
        generalSearchItem.setMnemonic(KeyEvent.VK_G);
        generalSearchItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
            ActionEvent.CTRL_MASK));
        generalSearchItem.setText(PaConstants.GENERAL_SEARCH_ITEM_TEXT);
        searchMenu.add(generalSearchItem);

        final JMenuItem reshowSearchItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doReshowSearchResultsItemAction();
            }
        });
        reshowSearchItem.setMnemonic(KeyEvent.VK_R);
        reshowSearchItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
            ActionEvent.CTRL_MASK));
        reshowSearchItem.setText(PaConstants.RESHOW_SEARCH_RESULTS_ITEM_TEXT);
        searchMenu.add(reshowSearchItem);
        return searchMenu;
    }

    private JMenu buildTLMenu() {

        final JMenu tlMenu = new JMenu(PaConstants.PA_GUI_TL_MENU_TITLE);
        tlMenu.setMnemonic(KeyEvent.VK_T);

        final JMenuItem unifyPlusStoreInLogSysAndMMTFolder = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doUnifyPlusStoreInLogSysAndMMTFolderItemAction();
                }
            });
        unifyPlusStoreInLogSysAndMMTFolder
            .setText(PaConstants.PA_GUI_TL_MENU_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER_TEXT);
        tlMenu.add(unifyPlusStoreInLogSysAndMMTFolder);

        final JMenuItem unifyPlusStoreInMMTFolderItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doUnifyPlusStoreInMMTFolderItemAction();
                }
            });
        unifyPlusStoreInMMTFolderItem
            .setText(PaConstants.PA_GUI_TL_MENU_UNIFY_PLUS_STORE_IN_MMT_FOLDER_TEXT);
        tlMenu.add(unifyPlusStoreInMMTFolderItem);

        final JMenuItem loadTheoremsFromMMTFolderItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doLoadTheoremsFromMMTFolderItemAction();
                }
            });
        loadTheoremsFromMMTFolderItem
            .setText(PaConstants.PA_GUI_TL_MENU_LOAD_THEOREMS_FROM_MMT_FOLDER_TEXT);
        tlMenu.add(loadTheoremsFromMMTFolderItem);

        final JMenuItem extractTheoremToMMTFolderItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doExtractTheoremToMMTFolderItemAction();
                }
            });
        extractTheoremToMMTFolderItem
            .setText(PaConstants.PA_GUI_TL_MENU_EXTRACT_THEOREM_TO_MMT_FOLDER_TEXT);
        tlMenu.add(extractTheoremToMMTFolderItem);

        final JMenuItem verifyAllProofs = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                doVerifyAllProofsItemAction();
            }
        });
        verifyAllProofs
            .setText(PaConstants.PA_GUI_TL_MENU_VERIFY_ALL_PROOFS_TEXT);
        tlMenu.add(verifyAllProofs);

        final JMenuItem setTLMMTFolderItem = new JMenuItem(new AbstractAction()
        {
            public void actionPerformed(final ActionEvent e) {
                doSetTLMMTFolderItemAction();
            }
        });
        setTLMMTFolderItem.setText(PaConstants.PA_GUI_TL_MENU_MMT_FOLDER_TEXT);
        tlMenu.add(setTLMMTFolderItem);

        final JMenuItem setTLDjVarsOptionItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetTLDjVarsOptionItemAction();
                }
            });
        setTLDjVarsOptionItem
            .setText(PaConstants.PA_GUI_TL_MENU_DJ_VARS_OPTION_TEXT);
        tlMenu.add(setTLDjVarsOptionItem);

        final JMenuItem setTLAuditMessagesItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetTLAuditMessagesItemAction();
                }
            });
        setTLAuditMessagesItem
            .setText(PaConstants.PA_GUI_TL_MENU_AUDIT_MESSAGES_TEXT);
        tlMenu.add(setTLAuditMessagesItem);

        final JMenuItem setProofCompressionItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetProofCompressionItemAction();
                }
            });
        setProofCompressionItem
            .setText(PaConstants.PA_GUI_TL_MENU_COMPRESSION_TEXT);
        tlMenu.add(setProofCompressionItem);

        final JMenuItem setTLStoreMMIndentAmtItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetTLStoreMMIndentAmtItemAction();
                }
            });
        setTLStoreMMIndentAmtItem
            .setText(PaConstants.PA_GUI_TL_MENU_STORE_MM_INDENT_AMT_TEXT);
        tlMenu.add(setTLStoreMMIndentAmtItem);

        final JMenuItem setTLStoreMMRightColItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetTLStoreMMRightColItemAction();
                }
            });
        setTLStoreMMRightColItem
            .setText(PaConstants.PA_GUI_TL_MENU_STORE_MM_RIGHT_COL_TEXT);
        tlMenu.add(setTLStoreMMRightColItem);

        final JMenuItem setTLStoreFormulasAsIsItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doSetTLStoreFormulasAsIsItemAction();
                }
            });
        setTLStoreFormulasAsIsItem
            .setText(PaConstants.PA_GUI_TL_MENU_STORE_FORMULAS_AS_IS_TEXT);
        tlMenu.add(setTLStoreFormulasAsIsItem);

        return tlMenu;
    }

    private JMenu buildGMFFMenu() {

        final JMenu gmffMenu = new JMenu(PaConstants.PA_GUI_GMFF_MENU_TITLE);
        gmffMenu.setMnemonic(KeyEvent.VK_G);

        // this item is a copy of the File Menu item
        final JMenuItem fileExportViaGMFFItem = new JMenuItem(
            new AbstractAction() {
                public void actionPerformed(final ActionEvent e) {
                    doFileExportViaGMFFAction();
                }
            });
        fileExportViaGMFFItem
            .setText(PaConstants.PA_GUI_FILE_MENU_EXPORT_VIA_GMFF_ITEM_TEXT);
        fileExportViaGMFFItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_1, ActionEvent.CTRL_MASK));

        gmffMenu.add(fileExportViaGMFFItem);

        return gmffMenu;
    }

    private JMenu buildHelpMenu() {

        final JMenu helpMenu = new JMenu(PaConstants.PA_GUI_HELP_MENU_TITLE);
        helpMenu.setMnemonic(KeyEvent.VK_H);

        final JMenuItem generalInfoItem = new JMenuItem(
            PaConstants.PA_GUI_HELP_MENU_GENERAL_ITEM_TEXT);
        generalInfoItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final HelpGeneralInfoGUI h = new HelpGeneralInfoGUI(
                    proofAsstPreferences);
                h.showFrame(h.buildFrame());
            }
        });
        helpMenu.add(generalInfoItem);

        final JMenuItem helpAboutItem = new JMenuItem(
            PaConstants.PA_GUI_HELP_ABOUT_ITEM_TEXT);
        helpAboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final Runtime r = Runtime.getRuntime();
                r.gc(); // run garbage collector
                final String about = LangException.format(
                    PaConstants.HELP_ABOUT_TEXT, r.maxMemory(), r.freeMemory(),
                    r.totalMemory());
                try {
                    JOptionPane.showMessageDialog(getMainFrame(), about,
                        PaConstants.HELP_ABOUT_TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (final HeadlessException f) {}
            }
        });
        helpMenu.add(helpAboutItem);

        return helpMenu;
    }

    /**
     * =============== Edit menu stuff ===============
     */

    private void doSetFormatNbrItemAction() {
        final int oldFormatNbr = proofAsstPreferences.getTMFFPreferences()
            .getCurrFormatNbr();

        final int newFormatNbr = getNewFormatNbr(oldFormatNbr);

        if (newFormatNbr != oldFormatNbr) {
            if (newFormatNbr < 0)
                return;
            proofAsstPreferences.getTMFFPreferences().setCurrFormatNbr(
                newFormatNbr);
            // TWEAK: 2008-02-01 -> Do not reformat when
            // format number is changed.
            // doTMFFReformatAction(false);
            // END-TWEAK: 2008-02-01
        }
    }

    private void doSetIndentItemAction() {
        final int oldIndent = proofAsstPreferences.getTMFFPreferences()
            .getUseIndent();

        final int newIndent = getNewIndent(oldIndent);

        if (newIndent != oldIndent) {
            if (newIndent < 0)
                return;
            proofAsstPreferences.getTMFFPreferences().setUseIndent(newIndent);
            // TWEAK: 2008-02-01 -> Do not reformat when
            // indent amount is changed.
            // doTMFFReformatAction(false);
            // END-TWEAK: 2008-02-01
        }
    }

    private void doSetForegroundColorItemAction() {
        final Color oldColor = proofAsstPreferences.getForegroundColor();
        final String colorChooserTitle = new String(
            PaConstants.PA_GUI_EDIT_MENU_SET_FOREGROUND_ITEM_TEXT
                + PaConstants.COLOR_CHOOSE_TITLE_2
                + Integer.toString(oldColor.getRed())
                + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
                + Integer.toString(oldColor.getGreen())
                + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
                + Integer.toString(oldColor.getBlue()));
        final Color newColor = getNewColor(oldColor, colorChooserTitle);
        if (!newColor.equals(oldColor)) {
            proofAsstPreferences.setForegroundColor(newColor);
            proofTextArea.setForeground(newColor);
        }
    }

    private void doSetBackgroundColorItemAction() {
        final Color oldColor = proofAsstPreferences.getBackgroundColor();
        final String colorChooserTitle = new String(
            PaConstants.PA_GUI_EDIT_MENU_SET_BACKGROUND_ITEM_TEXT
                + PaConstants.COLOR_CHOOSE_TITLE_2
                + Integer.toString(oldColor.getRed())
                + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
                + Integer.toString(oldColor.getGreen())
                + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
                + Integer.toString(oldColor.getBlue()));
        final Color newColor = getNewColor(oldColor, colorChooserTitle);
        if (!newColor.equals(oldColor)) {
            proofAsstPreferences.setBackgroundColor(newColor);
            proofTextArea.setBackground(newColor);
        }
    }

    private Color getNewColor(final Color oldColor, final String title) {
        Color newColor = JColorChooser.showDialog(proofTextArea, title,
            oldColor);
        if (newColor == null)
            newColor = oldColor;
        return newColor;
    }

    private void doSetIncompleteStepCursorItemAction() {

        final String newIncompleteStepCursorOption = getNewIncompleteStepCursorOption();

        if (newIncompleteStepCursorOption != null)
            proofAsstPreferences
                .setIncompleteStepCursor(newIncompleteStepCursorOption);
    }

    private void doSetSoftDjErrorItemAction() {

        final String newSoftDjErrorOption = getNewSoftDjErrorOption();

        if (newSoftDjErrorOption != null)
            proofAsstPreferences
                .setDjVarsSoftErrorsOption(newSoftDjErrorOption);
    }

    private void doSetFontFamilyItemAction() {
        final String oldFontFamily = proofAsstPreferences.getFontFamily();

        final String newFontFamily = getNewFontFamily(oldFontFamily);

        if (newFontFamily != null
            && newFontFamily.compareToIgnoreCase(oldFontFamily) != 0)
            if (newFontFamily.length() > 0) {
                proofAsstPreferences.setFontFamily(newFontFamily);
                buildProofFont();
                proofTextArea.setFont(proofFont);
            }
    }

    private void doTMFFReformatAction(final boolean inputCursorStep,
        final boolean swapAlt)
    {
        if (swapAlt)
            proofAsstPreferences.getTMFFPreferences()
                .toggleAltFormatAndIndentParms();

        // note: pass boolean "changes" to reformat task
        // so that reformat alone does not count
        // as changes -- after the reformat we'll
        // set that proofTextChanged status back
        // to the way it was here!
        startRequestAction(new RequestTMFFReformat(inputCursorStep,
            proofTextChanged.getChanges()));
    }

    private int getNewFormatNbr(final int oldFormatNbr) {
        int newFormatNbr = -1;
        String s = Integer.toString(oldFormatNbr);

        final String formatListString = proofAsstPreferences
            .getTMFFPreferences().getFormatListString();

        final String origPromptString = formatListString + "\n"
            + PaConstants.PA_GUI_SET_FORMAT_NBR_PROMPT;

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                break; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            try {
                newFormatNbr = proofAsstPreferences.getTMFFPreferences()
                    .validateFormatNbrString(s);
                break;
            } catch (final TMFFException e) {
                promptString = origPromptString + "\n" + e.getMessage();
            }
        }

        return newFormatNbr;
    }

    private int getNewIndent(final int oldIndent) {
        int newIndent = -1;
        String s = Integer.toString(oldIndent);

        final String origPromptString = PaConstants.PA_GUI_SET_INDENT_PROMPT
            + Integer.toString(TMFFConstants.TMFF_MAX_INDENT);

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                break; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            try {
                newIndent = proofAsstPreferences.getTMFFPreferences()
                    .validateIndentString(s);
                break;
            } catch (final TMFFException e) {
                promptString = origPromptString + "\n" + e.getMessage();
            }
        }

        return newIndent;
    }

    private String getNewIncompleteStepCursorOption() {

        String s = proofAsstPreferences.getIncompleteStepCursorOptionNbr();

        final String incompleteStepCursorOptionListString = proofAsstPreferences
            .getIncompleteStepCursorOptionListString();

        final String origPromptString = PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_OPTION_LIST
            + "\n"
            + incompleteStepCursorOptionListString
            + "\n"
            + PaConstants.PA_GUI_SET_INCOMPLETE_STEP_CURSOR_OPTION_PROMPT;

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return s; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            try {
                return proofAsstPreferences
                    .validateIncompleteStepCursorOptionNbr(s);
            } catch (final ProofAsstException e) {
                promptString = origPromptString + "\n" + e.getMessage();
            }
        }
    }

    private String getNewSoftDjErrorOption() {

        String s = proofAsstPreferences.getDjVarsSoftErrorsOptionNbr();

        final String softDjErrorOptionListString = proofAsstPreferences
            .getSoftDjErrorOptionListString();

        final String origPromptString = PaConstants.PROOF_ASST_SOFT_DJ_ERROR_OPTION_LIST
            + "\n"
            + softDjErrorOptionListString
            + "\n"
            + PaConstants.PA_GUI_SET_SOFT_DJ_ERROR_OPTION_PROMPT;

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return s; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            try {
                return proofAsstPreferences
                    .validateDjVarsSoftErrorsOptionNbr(s);
            } catch (final ProofAsstException e) {
                promptString = origPromptString + "\n" + e.getMessage();
            }
        }
    }

    private String getNewFontFamily(final String oldFontFamily) {

        String s = oldFontFamily;

        final String fontListString = proofAsstPreferences.getFontListString();

        final String origPromptString = PaConstants.PROOF_ASST_FONT_FAMILY_LIST
            + "\n" + fontListString + "\n"
            + PaConstants.PA_GUI_SET_FONT_FAMILY_PROMPT;

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                break; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            try {
                s = proofAsstPreferences.validateFontFamily(s);
                break;
            } catch (final ProofAsstException e) {
                promptString = origPromptString + "\n" + e.getMessage();
            }
        }

        return s;
    }

    /**
     * =============== File menu stuff ===============
     */

    private void doFileExitAction() {
        if (saveIfAskedBeforeExit(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_EXIT) == JOptionPane.CANCEL_OPTION)
            return;
        System.exit(0);
    }

    private void doFileCloseAction() {
        if (saveIfAskedBeforeAction(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_CLOSE) == JOptionPane.CANCEL_OPTION)
            return;

        setProofTextAreaText("");

        startRequestAction(new RequestUpdateMainFrameTitle(null));

        clearUndoRedoCaches();
        proofTextChanged.setChanges(false);
        savedSinceNew = false;
        disposeOfOldSelectorDialog();
    }

    private void doFileNewAction() {
        if (saveIfAskedBeforeAction(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW) == JOptionPane.CANCEL_OPTION)
            return;

        final String newTheoremLabel = getNewTheoremLabel();

        startRequestAction(new RequestNewProof(newTheoremLabel));
    }

    private void doFileNewNextAction() {
        if (saveIfAskedBeforeAction(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW) == JOptionPane.CANCEL_OPTION)
            return;

        startRequestAction(new RequestNewNextProof(getCurrProofMaxSeq()));

    }

    private void doFileGetProofAction() {
        if (saveIfAskedBeforeAction(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW) == JOptionPane.CANCEL_OPTION)
            return;

        final Theorem oldTheorem = getTheorem();

        if (oldTheorem == null)
            return;

        startRequestAction(new RequestGetProof(oldTheorem, true, false));

    }

    private void doFileGetFwdProofAction() {
        if (saveIfAskedBeforeAction(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW) == JOptionPane.CANCEL_OPTION)
            return;

        startRequestAction(new RequestFwdProof(getCurrProofMaxSeq(), true, // proof
                                                                           // unified
            false)); // hyps Randomized
    }

    private void doFileGetBwdProofAction() {
        if (saveIfAskedBeforeAction(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW) == JOptionPane.CANCEL_OPTION)
            return;

        startRequestAction(new RequestBwdProof(getCurrProofMaxSeq(), true, // proof
                                                                           // unified
            false)); // hyps Randomized
    }

    private String getNewTheoremLabel() {
        String s;
        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(),
                PaConstants.PA_GUI_NEW_THEOREM_LABEL_PROMPT);
            if (s == null)
                return s;
            s = s.trim();
            if (!s.equals(""))
                return s;
        }
    }

    private Theorem getTheorem() {
        String s = "";

        String promptString = PaConstants.PA_GUI_GET_THEOREM_LABEL_PROMPT;

        Theorem theorem;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return null; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = PaConstants.PA_GUI_GET_THEOREM_LABEL_PROMPT;
                continue;
            }
            theorem = proofAsst.getTheorem(s);
            if (theorem != null)
                return theorem;
            promptString = LangException.format(
                PaConstants.PA_GUI_GET_THEOREM_LABEL_PROMPT_2, s);
        }
    }

    private void doFileOpenAction() {
        if (saveIfAskedBeforeAction(PaConstants.PA_GUI_ACTION_BEFORE_SAVE_OPEN) == JOptionPane.CANCEL_OPTION)
            return;

        int returnVal;
        File file;
        while (true) {
            returnVal = fileChooser.showOpenDialog(getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                if (file.exists())
                    startRequestAction(new RequestFileOpen(file));
                else if (getYesNoAnswer(PaConstants.ERRMSG_PA_GUI_FILE_NOTFND,
                    file.getAbsolutePath()) == JOptionPane.YES_OPTION)
                    continue;
            }
            break;
        }
    }

    private int saveIfAskedBeforeExit(final String actionCaption) {
        int answer = JOptionPane.NO_OPTION;
        if (proofTextChanged.getChanges()) {
            answer = getYesNoCancelAnswer(
                PaConstants.ERRMSG_PA_GUI_SAVE_BEFORE_ACTION, actionCaption);

            if (answer == JOptionPane.YES_OPTION)
                doFileSaveAction(true); // saving before exit...
        }
        return answer;
    }
    private int saveIfAskedBeforeAction(final String actionCaption) {
        int answer = JOptionPane.NO_OPTION;
        if (proofTextChanged.getChanges()) {
            answer = getYesNoCancelAnswer(
                PaConstants.ERRMSG_PA_GUI_SAVE_BEFORE_ACTION, actionCaption);

            if (answer == JOptionPane.YES_OPTION)
                doFileSaveAction(false);
        }
        return answer;
    }

    private int getYesNoCancelAnswer(final String messageAboutIt,
        final Object... args)
    {
        int answer = JOptionPane.YES_OPTION; // default
        try {
            answer = JOptionPane.showConfirmDialog(getMainFrame(),
                LangException.format(messageAboutIt, args),
                PaConstants.PA_GUI_YES_NO_CANCEL_TITLE,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        } catch (final HeadlessException e) {}

        // Per Norm, window close should default to cancel!
        if (answer == JOptionPane.CLOSED_OPTION)
            answer = JOptionPane.CANCEL_OPTION;
        return answer;
    }

    private String readProofTextFromFile(final File file) {
        String newProofText;

        final char[] cBuffer = new char[1024];

        final StringBuilder sb = new StringBuilder(cBuffer.length);

        int len = 0;
        try {
            final BufferedReader r = new BufferedReader(new FileReader(file));
            while ((len = r.read(cBuffer, 0, cBuffer.length)) != -1)
                sb.append(cBuffer, 0, len);
            r.close();
            newProofText = sb.toString();
        } catch (final IOException e) {
            newProofText = LangException.format(
                PaConstants.ERRMSG_PA_GUI_READ_PROOF_IO_ERR, e.getMessage());
        }

        return newProofText;
    }

    private void doFileSaveAction(final boolean exitingNow) {

        File file;
        if (savedSinceNew) {
            file = fileChooser.getSelectedFile();
            if (file.exists()) {
                saveOldProofTextFile(file);
                updateMainFrameTitleIfNecessary(exitingNow);
                return;
            }
            else
                savedSinceNew = false; // should not happen
        }

        final int returnVal = fileChooser.showSaveDialog(getMainFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            if (file.exists())
                saveOldProofTextFile(file);
            else
                saveNewProofTextFile(file);
            updateMainFrameTitleIfNecessary(exitingNow);
        }
    }

    private void doFileSaveAsAction() {
        File newFile;

        final File oldFile = fileChooser.getSelectedFile();

        final int returnVal = fileChooser.showSaveDialog(getMainFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            newFile = fileChooser.getSelectedFile();
            if (newFile.exists()) {
                if (getYesNoAnswer(PaConstants.ERRMSG_PA_GUI_FILE_EXISTS,
                    newFile.getAbsolutePath()) == JOptionPane.YES_OPTION)
                    saveOldProofTextFile(newFile);
                else
                    fileChooser.setSelectedFile(oldFile);
            }
            else
                saveNewProofTextFile(newFile);
        }
        updateMainFrameTitleIfNecessary(false);

        // this prevents a title and filename update if the
        // user changes the THEOREM= label now...because they
        // used SaveAs we are taking them at their word that
        // this is the file name to use regardless!!!
        proofTheoremLabel = null; // tricky - avoid title update

    }

    private void updateMainFrameTitleIfNecessary(final boolean exitingNow) {
        if (!exitingNow) {
            final String newScreenTitle = buildScreenTitle(fileChooser
                .getSelectedFile());
            if (screenTitle.compareTo(newScreenTitle) != 0)
                startRequestAction(new RequestUpdateMainFrameTitle());
        }
    }

    private int getYesNoAnswer(final String messageAboutIt,
        final Object... args)
    {
        int answer = JOptionPane.YES_OPTION; // default
        try {
            answer = JOptionPane.showConfirmDialog(getMainFrame(),
                LangException.format(messageAboutIt, args),
                PaConstants.PA_GUI_YES_NO_TITLE, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        } catch (final HeadlessException e) {}
        return answer;
    }

    private void saveNewProofTextFile(final File file) {
        try {
            final BufferedWriter w = new BufferedWriter(new FileWriter(file));
            final String s = getProofTextAreaText();
            w.write(s, 0, s.length());
            w.close();
        } catch (final Throwable e) {
            JOptionPane.showMessageDialog(
                getMainFrame(),
                LangException.format(PaConstants.ERRMSG_PA_GUI_SAVE_IO_ERROR,
                    e.getMessage()),
                PaConstants.PA_GUI_SAVE_NEW_PROOF_TEXT_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }

        proofTextChanged.setChanges(false);
        clearUndoRedoCaches();
        savedSinceNew = true;
    }

    private void saveOldProofTextFile(final File file) {
        try {
            final BufferedWriter w = new BufferedWriter(new FileWriter(file));
            final String s = getProofTextAreaText();
            w.write(s, 0, s.length());
            w.close();
        } catch (final Throwable e) {
            JOptionPane.showMessageDialog(
                getMainFrame(),
                LangException.format(PaConstants.ERRMSG_PA_GUI_SAVE_IO_ERROR,
                    e.getMessage()),
                PaConstants.PA_GUI_SAVE_OLD_PROOF_TEXT_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }

        proofTextChanged.setChanges(false);
        clearUndoRedoCaches();
        savedSinceNew = true;

    }

    private void doFileExportViaGMFFAction() {
        startRequestAction(new RequestExportViaGMFF());
    }

    // ------------------------------------------------------
    // | Unify menu stuff |
    // ------------------------------------------------------

    private void doSetShowSubstitutionsItemAction() {

        final boolean newShowSubstitutions = getNewShowSubstitutions();

        proofAsstPreferences
            .setStepSelectorShowSubstitutions(newShowSubstitutions);
    }

    private boolean getNewShowSubstitutions() {

        String s = Boolean.toString(proofAsstPreferences
            .getStepSelectorShowSubstitutions());

        final String origPromptString = PaConstants.PA_GUI_SET_SHOW_SUBST_OPTION_PROMPT;

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return proofAsstPreferences.getStepSelectorShowSubstitutions();
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            try {
                return proofAsstPreferences
                    .validateStepSelectorShowSubstitutions(s);
            } catch (final IllegalArgumentException e) {
                promptString = origPromptString + "\n" + e.getMessage();
            }
        }
    }

    private void doSetMaxResultsItemAction() {

        final int newMaxResults = getNewMaxResults();

        if (newMaxResults != -1)
            proofAsstPreferences.setStepSelectorMaxResults(newMaxResults);
    }

    private int getNewMaxResults() {

        String s = Integer.toString(proofAsstPreferences
            .getStepSelectorMaxResults());

        final String origPromptString = PaConstants.PA_GUI_SET_MAX_RESULTS_OPTION_PROMPT;

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return -1; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            try {
                return proofAsstPreferences.validateStepSelectorMaxResults(s);
            } catch (final IllegalArgumentException e) {
                promptString = origPromptString + "\n" + e.getMessage();
            }
        }
    }

    private void startUnificationAction(final boolean renumReq,
        final boolean noConvertWV, final PreprocessRequest preprocessRequest,
        final StepRequest stepRequest, final TLRequest tlRequest)
    {
        final RequestUnify request = new RequestUnify(
            proofTextChanged.getChanges(), renumReq, noConvertWV,
            preprocessRequest, stepRequest, tlRequest);
        startRequestAction(request);
    }

    private void reshowStepSelectorDialogAction() {
        if (stepSelectorDialog != null)
            stepSelectorDialog.setVisible(true);
    }

    /*
     * Get rid of this thing if it is still hanging around,
     * we may need the memory space.
     */
    private void disposeOfOldSelectorDialog() {
        if (stepSelectorDialog != null)
            stepSelectorDialog.dispose();
    }

    private void doSearchOptionsItemAction() {
        unifyWithSearchChoice(new StepRequest(
            PaConstants.STEP_REQUEST_SEARCH_OPTIONS));
    }

    private void doStepSearchItemAction() {
        unifyWithSearchChoice(new StepRequest(
            PaConstants.STEP_REQUEST_STEP_SEARCH));
    }

    private void doGeneralSearchItemAction() {
        unifyWithSearchChoice(new StepRequest(
            PaConstants.STEP_REQUEST_GENERAL_SEARCH));
    }

    private void doReshowSearchResultsItemAction() {
        startRequestAction(new RequestReshowSearchResults());
    }

    public void unifyWithSearchChoice(final StepRequest stepRequest) {
        startUnificationAction(false, false, null, stepRequest, null);
    }

    // ------------------------------------------------------
    // | TL menu stuff |
    // ------------------------------------------------------

    private void doUnifyPlusStoreInLogSysAndMMTFolderItemAction() {
        startUnificationAction(false, // no renum
            false, // convert work vars
            null, // no preprocess request
            null, // no step selector request
            new StoreInLogSysAndMMTFolderTLRequest());
    }

    private void doUnifyPlusStoreInMMTFolderItemAction() {
        startUnificationAction(false, // no renum
            false, // convert work vars
            null, // no preprocess request
            null, // no step selector request
            new StoreInMMTFolderTLRequest());
    }

    private void doLoadTheoremsFromMMTFolderItemAction() {
        startRequestAction(new RequestLoadTheoremsFromMMTFolder());
    }

    private void doExtractTheoremToMMTFolderItemAction() {
        final Theorem theorem = getTheorem();
        if (theorem != null)
            startRequestAction(new RequestExtractTheoremToMMTFolder(theorem));
    }

    private void doVerifyAllProofsItemAction() {
        startRequestAction(new RequestVerifyAllProofs());
    }

    private void doSetTLMMTFolderItemAction() {
        getNewMMTFolder();
    }

    private MMTFolder getNewMMTFolder() {
        String title = "";
        File file = tlPreferences.getMMTFolder().getFolderFile();
        if (file != null)
            title = file.getAbsolutePath();
        mmtFolderChooser.setDialogTitle(title);

        int returnVal;
        String errMsg;
        while (true) {
            returnVal = mmtFolderChooser.showDialog(getMainFrame(),
                PaConstants.PA_GUI_SET_TL_MMT_FOLDER_OPTION_PROMPT);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = mmtFolderChooser.getSelectedFile();
                errMsg = tlPreferences.setMMTFolder(file);
                if (errMsg == null)
                    break;
                if (getYesNoAnswer(errMsg
                    + PaConstants.PA_GUI_SET_TL_MMT_FOLDER_OPTION_PROMPT_2) == JOptionPane.YES_OPTION)
                    continue;
            }
            break;
        }
        return tlPreferences.getMMTFolder();
    }

    private void doSetTLDjVarsOptionItemAction() {
        getNewTLDjVarsOption();
    }

    private String getNewTLDjVarsOption() {
        String s = tlPreferences.getDjVarsOption();
        final String origPromptString = PaConstants.PA_GUI_SET_TL_DJ_VARS_OPTION_PROMPT;
        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return tlPreferences.getDjVarsOption();
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            if (tlPreferences.setDjVarsOption(s))
                return tlPreferences.getDjVarsOption();
            promptString = origPromptString + "\n"
                + TlConstants.ERRMSG_INVALID_DJ_VARS_OPTION_1 + s
                + TlConstants.ERRMSG_INVALID_DJ_VARS_OPTION_2;
        }
    }

    private void doSetProofCompressionItemAction() {
        final String newProofCompression = getNewProofCompression();

        if (newProofCompression != null)
            proofAsstPreferences.setProofFormatOption(newProofCompression);
    }

    private String getNewProofCompression() {
        String s = Integer
            .toString(proofAsstPreferences.getProofFormatNumber());
        final String origPromptString = PaConstants.PROOF_ASST_COMPRESSION_LIST;
        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return s; // cancelled input
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            if (s.equals("1"))
                return PaConstants.PROOF_ASST_PROOF_NORMAL;
            if (s.equals("2"))
                return PaConstants.PROOF_ASST_PROOF_PACKED;
            if (s.equals("3"))
                return PaConstants.PROOF_ASST_PROOF_COMPRESSED;
            promptString = origPromptString + "\n"
                + "Not a valid option; try again";
        }
    }

    private void doSetTLStoreMMIndentAmtItemAction() {
        getNewTLStoreMMIndentAmt();
    }

    private int getNewTLStoreMMIndentAmt() {
        String s = Integer.toString(tlPreferences.getStoreMMIndentAmt());
        final String origPromptString = PaConstants.PA_GUI_SET_TL_STORE_MM_INDENT_AMT_OPTION_PROMPT;
        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return tlPreferences.getStoreMMIndentAmt();
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            if (tlPreferences.setStoreMMIndentAmt(s))
                return tlPreferences.getStoreMMIndentAmt();
            promptString = origPromptString + "\n"
                + TlConstants.ERRMSG_INVALID_STORE_MM_INDENT_AMT_1 + s
                + TlConstants.ERRMSG_INVALID_STORE_MM_INDENT_AMT_2;
        }
    }

    private void doSetTLStoreMMRightColItemAction() {
        getNewTLStoreMMRightCol();
    }

    private int getNewTLStoreMMRightCol() {
        String s = Integer.toString(tlPreferences.getStoreMMRightCol());
        final String origPromptString = PaConstants.PA_GUI_SET_TL_STORE_MM_RIGHT_COL_OPTION_PROMPT;
        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return tlPreferences.getStoreMMRightCol();
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            if (tlPreferences.setStoreMMRightCol(s))
                return tlPreferences.getStoreMMRightCol();
            promptString = origPromptString + "\n"
                + TlConstants.ERRMSG_INVALID_STORE_MM_RIGHT_COL_1 + s
                + TlConstants.ERRMSG_INVALID_STORE_MM_RIGHT_COL_2;
        }
    }

    private void doSetTLStoreFormulasAsIsItemAction() {
        getNewTLStoreFormulasAsIs();
    }

    private boolean getNewTLStoreFormulasAsIs() {
        String s = Boolean.toString(tlPreferences.getStoreFormulasAsIs());
        final String origPromptString = PaConstants.PA_GUI_SET_TL_STORE_FORMULAS_AS_IS_OPTION_PROMPT;
        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return tlPreferences.getStoreFormulasAsIs();
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            if (tlPreferences.setStoreFormulasAsIs(s))
                return tlPreferences.getStoreFormulasAsIs();
            promptString = origPromptString + "\n"
                + TlConstants.ERRMSG_INVALID_STORE_FORMULAS_ASIS_1 + s
                + TlConstants.ERRMSG_INVALID_STORE_FORMULAS_ASIS_2;
        }
    }

    private void doSetTLAuditMessagesItemAction() {
        getNewTLAuditMessages();
    }

    private boolean getNewTLAuditMessages() {

        String s = Boolean.toString(tlPreferences.getAuditMessages());

        final String origPromptString = PaConstants.PA_GUI_SET_TL_AUDIT_MESSAGES_OPTION_PROMPT;

        String promptString = origPromptString;

        while (true) {
            s = JOptionPane.showInputDialog(getMainFrame(), promptString, s);
            if (s == null)
                return tlPreferences.getAuditMessages();
            s = s.trim();
            if (s.equals("")) {
                promptString = origPromptString;
                continue;
            }
            if (tlPreferences.setAuditMessages(s))
                return tlPreferences.getAuditMessages();
            promptString = origPromptString + "\n"
                + TlConstants.ERRMSG_INVALID_AUDIT_MESSAGES_1 + s
                + TlConstants.ERRMSG_INVALID_AUDIT_MESSAGES_2;

        }
    }

    // ------------------------------------------------------
    // | Inner classes to make requests for processing |
    // | that occur on separate threads off of the event |
    // | loop. |
    // ------------------------------------------------------

    abstract class Request {
        ProofWorksheet w;

        Request() {}
        abstract void send();
        abstract void receive();

    }

    class RequestExtractTheoremToMMTFolder extends Request {
        Messages messages;
        Theorem theorem;

        RequestExtractTheoremToMMTFolder(final Theorem theorem) {
            this.theorem = theorem;
        }
        @Override
        void send() {
            messages = proofAsst.extractTheoremToMMTFolder(theorem);
        }
        @Override
        void receive() {
            String s = ProofWorksheet.getOutputMessageText(messages);
            if (s == null)
                s = PaConstants.ERRMSG_PA_GUI_EXTRACT_THEOREMS_TO_MMT_FOLDER_NO_MSGS;
            displayRequestMessages(s);
        }
    }

    class RequestLoadTheoremsFromMMTFolder extends Request {
        Messages messages;

        @Override
        void send() {
            messages = proofAsst.loadTheoremsFromMMTFolder();
        }
        @Override
        void receive() {
            String s = ProofWorksheet.getOutputMessageText(messages);
            if (s == null)
                s = PaConstants.ERRMSG_PA_GUI_LOAD_THEOREMS_FROM_MMT_FOLDER_NO_MSGS;
            displayRequestMessages(s);
        }
    }

    class RequestExportViaGMFF extends Request {
        Messages messages;

        @Override
        void send() {
            messages = proofAsst.exportViaGMFF(getProofTextAreaText());
        }
        @Override
        void receive() {
            String s = ProofWorksheet.getOutputMessageText(messages);
            if (s == null)
                s = PaConstants.ERRMSG_PA_GUI_EXPORT_VIA_GMFF_NO_MSGS;
            displayRequestMessages(s);
        }
    }

    class RequestVerifyAllProofs extends Request {
        Messages messages;

        @Override
        void send() {
            messages = proofAsst.verifyAllProofs();
        }
        @Override
        void receive() {
            String s = ProofWorksheet.getOutputMessageText(messages);
            if (s == null)
                s = PaConstants.ERRMSG_PA_GUI_VERIFY_ALL_PROOFS_NO_MSGS;
            displayRequestMessages(s);
        }
    }

    class RequestUpdateMainFrameTitle extends Request {
        File newFile;

        RequestUpdateMainFrameTitle() {
            newFile = fileChooser.getSelectedFile();
        }
        RequestUpdateMainFrameTitle(final File f) {
            newFile = f;
        }
        @Override
        void send() {}
        @Override
        void receive() {
            updateScreenTitle(newFile);
            updateMainFrameTitle();
        }
    }

    class RequestEditUndo extends Request {
        @Override
        void send() {}
        @Override
        void receive() {
            try {
                undoManager.undo();
                updateUndoRedoItems();
            } catch (final CannotUndoException e) {
                displayRequestMessages(e.getMessage());
            }
        }
    }

    class RequestEditRedo extends Request {
        @Override
        void send() {}
        @Override
        void receive() {
            try {
                undoManager.redo();
                updateUndoRedoItems();
            } catch (final CannotRedoException e) {
                displayRequestMessages(e.getMessage());
            }
        }
    }

    class RequestUnify extends Request {
        private final boolean renumReq;
        private final PreprocessRequest preprocessRequest;
        private final StepRequest stepRequest;
        private final TLRequest tlRequest;
        private final boolean textChangedBeforeUnify;
        private final boolean noConvertWV;

        RequestUnify(final boolean textChangedBeforeUnify,
            final boolean renumReq, final boolean noConvertWV,
            final PreprocessRequest preprocessRequest,
            final StepRequest stepRequest, final TLRequest tlRequest)
        {
            this.textChangedBeforeUnify = textChangedBeforeUnify;
            this.renumReq = renumReq;
            this.noConvertWV = noConvertWV;
            this.preprocessRequest = preprocessRequest;
            this.stepRequest = stepRequest;
            this.tlRequest = tlRequest;
        }
        @Override
        void send() {
            w = proofAsst.unify(renumReq, noConvertWV, getProofTextAreaText(),
                preprocessRequest, stepRequest, tlRequest,
                proofTextArea.getCaretPosition() + 1);

        }
        @Override
        void receive() {
            if (stepRequest != null
                && (stepRequest.request == PaConstants.STEP_REQUEST_GENERAL_SEARCH || stepRequest.request == PaConstants.STEP_REQUEST_SEARCH_OPTIONS))
                proofAsstPreferences.getSearchMgr().execShowSearchOptions(w);
            else if (stepRequest != null
                && stepRequest.request == PaConstants.STEP_REQUEST_STEP_SEARCH
                && w.searchOutput != null)
            {
                final String s = ProofWorksheet
                    .getOutputMessageTextAbbrev(proofAsst.getMessages());
                if (s != null)
                    displayRequestMessages(s);
                proofAsstPreferences.getSearchMgr().execShowSearchResults();
            }
            else if (w.stepSelectorResults != null) {
                disposeOfOldSelectorDialog();
                stepSelectorDialog = new StepSelectorDialog(mainFrame,
                    w.stepSelectorResults, proofAsstGUI, proofAsstPreferences,
                    proofFont);
            }
            else {
                displayProofWorksheet(w);
                if (stepRequest != null
                    && stepRequest.request == PaConstants.STEP_REQUEST_SELECTOR_CHOICE)
                    getMainFrame().setVisible(true);
            }
            proofTextChanged.setChanges(textChangedBeforeUnify);
        }
    }

    class RequestNewProof extends Request {
        String newTheoremLabel;

        RequestNewProof(final String newTheoremLabel) {
            this.newTheoremLabel = newTheoremLabel;
        }
        @Override
        void send() {
            w = proofAsst.startNewProof(newTheoremLabel);
        }
        @Override
        void receive() {
            proofTheoremLabel = ""; // tricky - force title update
            displayProofWorksheet(w);

            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            savedSinceNew = false;
            disposeOfOldSelectorDialog();
        }
    }

    class RequestNewNextProof extends Request {
        int currProofMaxSeq;

        RequestNewNextProof(final int currProofMaxSeq) {
            this.currProofMaxSeq = currProofMaxSeq;
        }
        @Override
        void send() {
            w = proofAsst.startNewNextProof(currProofMaxSeq);
        }
        @Override
        void receive() {
            proofTheoremLabel = ""; // tricky - force title update
            displayProofWorksheet(w);

            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            savedSinceNew = false;
            disposeOfOldSelectorDialog();
        }
    }

    class RequestFileOpen extends Request {
        File selectedFile;
        String s;

        RequestFileOpen(final File selectedFile) {
            this.selectedFile = selectedFile;
        }
        @Override
        void send() {
            s = readProofTextFromFile(selectedFile);
        }
        @Override
        void receive() {
            setProofTextAreaText(s);

            proofTheoremLabel = null; // tricky - avoid title update
            updateScreenTitle(fileChooser.getSelectedFile());
            updateMainFrameTitle();

            clearUndoRedoCaches();

            setProofTextAreaCursorPos(ProofAsstCursor.makeProofStartCursor(),
                s.length());

            proofTextChanged.setChanges(false);
            savedSinceNew = true;
            disposeOfOldSelectorDialog();
        }
    }

    class RequestGetProof extends Request {
        Theorem oldTheorem;
        boolean proofUnified;
        boolean hypsRandomized;

        RequestGetProof(final Theorem oldTheorem, final boolean proofUnified,
            final boolean hypsRandomized)
        {
            this.oldTheorem = oldTheorem;
            this.proofUnified = proofUnified;
            this.hypsRandomized = hypsRandomized;
        }
        @Override
        void send() {
            w = proofAsst.getExistingProof(oldTheorem, proofUnified,
                hypsRandomized);
        }
        @Override
        void receive() {
            proofTheoremLabel = ""; // tricky - force title update
            displayProofWorksheet(w);
            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            savedSinceNew = false;
            disposeOfOldSelectorDialog();
        }
    }

    class RequestFwdProof extends Request {
        int currProofMaxSeq;
        boolean proofUnified;
        boolean hypsRandomized;

        RequestFwdProof(final int currProofMaxSeq, final boolean proofUnified,
            final boolean hypsRandomized)
        {
            this.currProofMaxSeq = currProofMaxSeq;
            this.proofUnified = proofUnified;
            this.hypsRandomized = hypsRandomized;
        }
        @Override
        void send() {
            w = proofAsst.getNextProof(currProofMaxSeq, proofUnified,
                hypsRandomized);
        }
        @Override
        void receive() {
            proofTheoremLabel = ""; // tricky - force title update
            displayProofWorksheet(w);
            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            savedSinceNew = false;
            disposeOfOldSelectorDialog();
        }
    }

    class RequestBwdProof extends Request {
        int currProofMaxSeq;
        boolean proofUnified;
        boolean hypsRandomized;

        RequestBwdProof(final int currProofMaxSeq, final boolean proofUnified,
            final boolean hypsRandomized)
        {
            this.currProofMaxSeq = currProofMaxSeq;
            this.proofUnified = proofUnified;
            this.hypsRandomized = hypsRandomized;
        }
        @Override
        void send() {
            w = proofAsst.getPreviousProof(getCurrProofMaxSeq(),//
                true, // proof unified
                false); // hyps Randomized
        }
        @Override
        void receive() {
            proofTheoremLabel = ""; // tricky - force title update
            displayProofWorksheet(w);
            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            savedSinceNew = false;
            disposeOfOldSelectorDialog();
        }
    }

    class RequestTMFFReformat extends Request {
        boolean inputCursorStep;
        boolean textChangedBeforeReformat;

        RequestTMFFReformat(final boolean inputCursorStep,
            final boolean textChangedBeforeReformat)
        {
            this.inputCursorStep = inputCursorStep;
            this.textChangedBeforeReformat = textChangedBeforeReformat;
        }
        @Override
        void send() {
            w = proofAsst.tmffReformat(inputCursorStep, getProofTextAreaText(),
                proofTextArea.getCaretPosition() + 1);
        }

        @Override
        void receive() {
            displayProofWorksheet(w);
            proofTextChanged.setChanges(textChangedBeforeReformat);
        }
    }

    // ------------------------------------------------------
    // | Inner classes to manage thread requests |
    // | that occur on separate threads off of the event |
    // | loop. |
    // ------------------------------------------------------

    public class RequestThreadStuff {
        Request request;
        Runnable displayRequestResults;
        Runnable sendRequest;

        /**
         * Thread used for Unification of proof.
         */
        public Thread requestThread;

        /**
         * Set the Thread value in RequestThreadStuff object.
         * 
         * @param t Thread used in RequestThreadStuff
         */
        public synchronized void setRequestThread(final Thread t) {
            requestThread = t;
        }

        /**
         * Get the Thread value in RequestThreadStuff object.
         * 
         * @return Thread used in RequestThreadStuff
         */
        public synchronized Thread getRequestThread() {
            return requestThread;
        }

        /**
         * Start the Thread used in the RequestThreadStuff object.
         */
        public void startRequestThread() {
            getRequestThread().start();
        }

        /**
         * Cancel the Thread used in the RequestThreadStuff object if it exists
         * (not null).
         */
        public void cancelRequestThread() {
            final Thread requestThread = getRequestThread();
            if (requestThread != null)
                requestThread.interrupt();
        }

        /**
         * Constructor.
         * <p>
         * Builds object for sending processing request and receiving the
         * finished results.
         * 
         * @param r Request object reference
         */
        public RequestThreadStuff(final Request r) {

            request = r;

            displayRequestResults = new Runnable() {
                public void run() {
                    try {
                        request.receive();
                    } finally {
                        tidyUpRequestStuff();
                    }
                }
            };

            sendRequest = new Runnable() {
                public void run() {
                    try {
                        request.send();
                        EventQueue.invokeLater(displayRequestResults);
                    } finally {
                        setRequestThread(null);
                    }
                }
            };

            setRequestThread(new Thread(sendRequest));
        }
    }

    class RequestSearchResultsMinusButton extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr()
                .execSearchResultsDecreaseFontSize();
        }
    }

    class RequestSearchResultsPlusButton extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr()
                .execSearchResultsIncreaseFontSize();
        }
    }

    class RequestSearchOptionsMinusButton extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr()
                .execSearchOptionsDecreaseFontSize();
        }
    }

    class RequestSearchOptionsPlusButton extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr()
                .execSearchOptionsIncreaseFontSize();
        }
    }

    class RequestReshowProofAsstGUI extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr().execReshowProofAsstGUI();
        }
    }

    class RequestReshowSearchResults extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr().execReshowSearchResults();
        }
    }

    class RequestReshowSearchOptions extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr().execReshowSearchOptions();
        }
    }

    class RequestRefineAndShowResults extends Request {

        @Override
        void send() {
            proofAsstPreferences.getSearchMgr().execRefineSearch();
        }

        @Override
        void receive() {
            final String s = ProofWorksheet
                .getOutputMessageTextAbbrev(proofAsst.getMessages());
            if (s != null)
                displayRequestMessages(s);
            proofAsstPreferences.getSearchMgr().execShowSearchResults();
        }
    }

    class RequestSearchAndShowResults extends Request {

        @Override
        void send() {
            proofAsstPreferences.getSearchMgr().execSearch();
        }

        @Override
        void receive() {
            final String s = ProofWorksheet
                .getOutputMessageTextAbbrev(proofAsst.getMessages());
            if (s != null)
                displayRequestMessages(s);
            proofAsstPreferences.getSearchMgr().execShowSearchResults();
        }
    }

    class RequestNewGeneralSearch extends Request {

        @Override
        void send() {}

        @Override
        void receive() {
            proofAsstPreferences.getSearchMgr()
                .execSearchOptionsNewGeneralSearch(
                    proofAsst.getStmt(newStmtLabel));
        }

        String newStmtLabel;

        RequestNewGeneralSearch(final String s) {
            newStmtLabel = s;
        }
    }

    private synchronized RequestThreadStuff getRequestThreadStuff() {
        return requestThreadStuff;
    }

    private synchronized void setRequestThreadStuff(final RequestThreadStuff x)
    {
        requestThreadStuff = x;
    }

    public synchronized boolean startRequestAction(final Request r) {
        if (getRequestThreadStuff() == null) {

            setRequestThreadStuff(new RequestThreadStuff(r));

            getRequestThreadStuff().startRequestThread();

            cancelRequestItem.setEnabled(true);
            getMainFrame().setCursor(
                Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            proofTextArea.setCursor(Cursor
                .getPredefinedCursor(Cursor.WAIT_CURSOR));
            return true;
        }
        return false;
    }

    public synchronized boolean cancelRequestAction() {
        RequestThreadStuff k;
        if ((k = getRequestThreadStuff()) != null) {
            k.cancelRequestThread();
            tidyUpRequestStuff();
            return true;
        }
        return false;
    }

    private void tidyUpRequestStuff() {
        setRequestThreadStuff(null);
        cancelRequestItem.setEnabled(false);
        getMainFrame().setCursor(null);
        proofTextArea.setCursor(null);
    }

    private void displayProofWorksheet(final ProofWorksheet w) {

        // keep this number for browsing forward and back!
        setCurrProofMaxSeq(w.getMaxSeq());

        String s = w.getOutputProofText();
        int proofTextLength = Integer.MAX_VALUE;
        if (s != null) { // no structural errors...
            setProofTextAreaText(s);
            proofTextLength = s.length();
        }

        s = w.getTheoremLabel();
        if (s != null && proofTheoremLabel != null)
            if (s.compareToIgnoreCase(proofTheoremLabel) != 0) {
                updateFileChooserFileForProofLabel(s);
                updateScreenTitle(fileChooser.getSelectedFile());
                updateMainFrameTitle();
                proofTheoremLabel = s;
                savedSinceNew = false;
            }

        setProofTextAreaCursorPos(w, proofTextLength);
        displayRequestMessages(w.getOutputMessageText());
    }

    private void displayRequestMessages(final String s) {
        proofMessageArea.setText(s);
        setCursorToStartOfMessageArea();

        displayRequestMessagesGUI(s != null ? s
            : PaConstants.ERRMSG_NO_MESSAGES_MSG_1);
    }

    private void displayRequestMessagesGUI(final String messages) {
        if (!PaConstants.REQUEST_MESSAGES_GUI_ENABLED)
            return;

        RequestMessagesGUI u = getRequestMessagesGUI();
        if (u == null) {
            if (messages == null)
                return;

            u = new RequestMessagesGUI(messages, proofAsstPreferences);
            setRequestMessagesGUI(u);
            u.showFrame(u.buildFrame());
        }
        else {
            u.changeFrameText(messages);
            u.setCursorToStartOfMessageArea();
        }
    }

    /**
     * Positions the input caret to start of message text area and scrolls
     * viewport to ensure that the start of the message text area is visible.
     * <p>
     * This is called only after updates to an existing AuxFrameGUI screen. It
     * is automatically invoked during the initial display sequence of events..
     */
    public void setCursorToStartOfMessageArea() {

        try {

            proofMessageArea.setCaretPosition(0);

            final JViewport v = proofMessageScrollPane.getViewport();

            v.scrollRectToVisible(new Rectangle(0, // x
                0, // y
                1, // width
                1)); // height
        } catch (final Exception e) {
            // ignore, don't care, did our best.
        }
    }

    private synchronized void setRequestMessagesGUI(final RequestMessagesGUI u)
    {

        requestMessagesGUI = u;
    }
    private synchronized RequestMessagesGUI getRequestMessagesGUI() {

        return requestMessagesGUI;
    }

    /**
     * ============================== GUI Frame Infrastructure stuff
     * ==============================
     */

    private static class FrameShower implements Runnable {
        JFrame f;
        private final boolean max;

        public FrameShower(final JFrame frame, final boolean maximize) {
            f = frame;
            max = maximize;
        }
        public void run() {
            f.pack();
            f.setVisible(true);
            if (max)
                f.setExtendedState(f.getExtendedState() | Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Show the GUI's main frame (screen).
     */
    public void showMainFrame() {
        showFrame(getMainFrame(), proofAsstPreferences.getMaximized());
    }

    private void showFrame(final JFrame jFrame, final boolean maximize) {
        final Runnable runner = new FrameShower(jFrame, maximize);

        EventQueue.invokeLater(runner);
    }

    /**
     * Test code to invoke GUI from command line.
     * 
     * @param args String array holding command line parms
     */
    public static void main(final String[] args) {
        final ProofAsstGUI proofAsstGUI = new ProofAsstGUI();
        proofAsstGUI.showMainFrame();
    }
}
