//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ProofAsst.java  0.11 11/01/2011
 *
 * Version 0.03
 *     - fix vol test bug 3/27 (no qed step null pointer)
 *     - update misc. comments
 *     - add Proof Assistant 'Derive' Feature
 *
 * Sep-09-2006 - Version 0.04 - TMFF project
 *     - modified to support ProofAsstGUI File/GetProof
 *
 * Jun-01-2007 - Version 0.05 -
 *     - various changes to get rid of ProofWorkStmt.status
 *     - add call to proofWorksheet.addGeneratedDjVarsStmts()
 *
 * Aug-01-2007 - Version 0.06 -
 *     - Added asciiRetest option to importFrom*
 *
 * Nov-01-2007 - Version 0.07 -
 *     - Position cursor to *last* incomplete statement
 *       instead of first.
 *     - Call proofWorksheet.posCursorAtLastIncompleteOrQedStmt()
 *       when RPN proof generated successfully, and output an
 *       info message about the success. The old code positioned
 *       the cursor (intentionally) to the end of the RPN proof.
 *       And, output I-PA-0119, ERRMSG_PA_RPN_PROOF_GENERATED_1/2.
 *
 * Feb-01-2008 - Version 0.08 -
 *     - modify tmffReformat() to accept boolean argument,
 *       "inputCursorStep" which requests reformatting of
 *       just one proof step, the step underneath the cursor
 *       when the request was made.
 *     - Use new proofAsstPreferences.getIncompleteStmtCursorFirst()
 *       and     proofAsstPreferences.getIncompleteStmtCursorLast()
 *       to control cursor positioning when there are no
 *       unification errors.
 *     - "re-added back" posCursorAtFirstIncompleteOrQedStmt();
 *
 * Mar-01-2008 - Version 0.09 -
 *     - Misc. tidy up.
 *     - Add StepSelector Dialog/Search
 *     - Add PreprocessRequest option on Unify to
 *       implement Unify menu item Unify+Rederive Formulas
 *     - Remove Hints feature
 *
 * Aug-01-2008 - Version 0.10 -
 *     - Add TheoremLoader to constructor and various other
 *       items related to TheoremLoader.
 *
 * Version 0.11 - Nov-01-2011:
 *     - Added:
 *         exportViaGMFF() for use by ProofAsstGUI
 *       and
 *         getSortedSkipSeqTheoremIterable(String startTheoremLabel)
 *         getSortedTheoremIterable(int lowestMObjSeq)
 *         exportOneTheorem(String theoremLabel)
 *         exportOneTheorem(Theorem theorem)
 *       for use by GMFFManager.exportTheorem
 *     - Modified volumeTestOutputRoutine to use theorem to
 *       get label, not proof worksheet (because in some
 *       cases the proof worksheet may be invalid.)
 *     - Rewrote incompleteStepCursorPositioning() to
 *       fix "AsIs" cursor positioning bug.
 */

package mmj.pa;

import java.io.*;
import java.util.*;

import mmj.gmff.GMFFException;
import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;
import mmj.mmio.MMIOError;
import mmj.tl.*;
import mmj.util.OutputBoss;
import mmj.verify.*;

/**
 * The {@code ProofAsst}, along with the rest of the {@code mmj.pa} package
 * provides a graphical user interface (GUI) facility for developing Metamath
 * proofs. {@code ProofAsst} is the go-to guy, essentially a control module that
 * knows where to go to get things done. It is invoked by
 * mmj.util.ProofAsstBoss.java, and invokes mmj.pa.ProofAsstGUI, among others.
 * Nomenclature: a proof-in-progress is implemented by the
 * mmj.pa.ProofWorksheet.java class. A large quantity of information and useful
 * stuff is contained in mmj.pa.PaConstants.java.
 */
public class ProofAsst implements TheoremLoaderCommitListener {

    private boolean initializedOK;

    // global variables stored here for convenience
    private ProofAsstGUI proofAsstGUI;
    private final ProofAsstPreferences proofAsstPreferences;
    private final ProofUnifier proofUnifier;
    private final LogicalSystem logicalSystem;
    private final Grammar grammar;
    private final VerifyProofs verifyProofs;
    private Messages messages;
    private final TheoremLoader theoremLoader;

    // for volume testing
    private int nbrTestTheoremsProcessed;
    private int nbrTestNotProvedPerfectly;
    private int nbrTestProvedDifferently;
    private List<Theorem> sortedTheoremList;

    /**
     * Constructor.
     * 
     * @param proofAsstPreferences variable settings
     * @param logicalSystem the loaded Metamath data
     * @param grammar the mmj.verify.Grammar object
     * @param verifyProofs the mmj.verify.VerifyProofs object
     * @param theoremLoader the mmj.tl.TheoremLoader object
     */
    public ProofAsst(final ProofAsstPreferences proofAsstPreferences,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final VerifyProofs verifyProofs, final TheoremLoader theoremLoader)
    {

        this.proofAsstPreferences = proofAsstPreferences;
        this.logicalSystem = logicalSystem;
        this.grammar = grammar;
        this.verifyProofs = verifyProofs;
        this.theoremLoader = theoremLoader;

        proofUnifier = new ProofUnifier(proofAsstPreferences, logicalSystem,
            grammar, verifyProofs);

        messages = null;

        initializedOK = false;

    }

    /**
     * Triggers the Proof Assistant GUI.
     * 
     * @param m the mmj.lang.Messages object used to store error and
     *            informational messages.
     */
    public void doGUI(final Messages m) {
        messages = m;

        if (!getInitializedOK())
            initializeLookupTables(m);

        proofAsstGUI = new ProofAsstGUI(this, proofAsstPreferences,
            theoremLoader);

        proofAsstGUI.showMainFrame();
    }

    /**
     * @return initializedOK flag.
     */
    public boolean getInitializedOK() {
        return initializedOK;
    }

    public LogicalSystem getLogicalSystem() {
        return logicalSystem;
    }

    public Messages getMessages() {
        return messages;
    }

    public ProofAsstGUI getProofAsstGUI() {
        return proofAsstGUI;
    }

    public List<Assrt> sortAssrtListForSearch(final List<Assrt> list) {
        final List<Assrt> sorted = new ArrayList<Assrt>(list);
        Collections.sort(sorted, Assrt.NBR_LOG_HYP_SEQ);
        return sorted;
    }

    /**
     * Initialized Unification lookup tables, etc. for Unification.
     * 
     * @param messages the mmj.lang.Messages object used to store error and
     *            informational messages.
     * @return initializedOK flag.
     */
    public boolean initializeLookupTables(final Messages messages) {
        this.messages = messages;
        initializedOK = proofUnifier.initializeLookupTables(messages);
        proofAsstPreferences.getSearchMgr().initOtherEnvAreas(this,
            logicalSystem, grammar, verifyProofs, messages);
        logicalSystem.getBookManager().getDirectSectionDependencies(
            logicalSystem);
        return initializedOK;
    }

    public List<Assrt> getSortedAssrtSearchList() {
        return proofUnifier.getSortedAssrtSearchList();
    }

    /**
     * Applies a set of updates from the TheoremLoader as specified in the
     * mmtTheoremSet object to the ProofAsst local caches of data.
     * 
     * @param mmtTheoremSet MMTTheoremSet object containing the adds and updates
     *            already made to theorems in the LogicalSystem.
     */
    public void commit(final MMTTheoremSet mmtTheoremSet) {
        if (!getInitializedOK())
            return; // the stmtTbl data has not been stored yet

        final List<Theorem> listOfAssrtAddsSortedBySeq = mmtTheoremSet
            .buildSortedAssrtListOfAdds(MObj.SEQ);

        if (listOfAssrtAddsSortedBySeq.isEmpty())
            return;

        proofUnifier
            .mergeListOfAssrtAddsSortedBySeq(listOfAssrtAddsSortedBySeq);
    }

    /**
     * Verifies all proofs in the Logical System
     * <p>
     * This is here because the Proof Assistant GUI doesn't know how to do
     * anything...
     * 
     * @return Messages object.
     */
    public Messages verifyAllProofs() {
        verifyProofs.verifyAllProofs(messages, logicalSystem.getStmtTbl());
        return messages;
    }

    /**
     * Invokes TheoremLoader to load all theorems in the MMT Folder.
     * <p>
     * This is here because the Proof Assistant GUI doesn't know how to do
     * anything...
     * 
     * @return Messages object.
     */
    public Messages loadTheoremsFromMMTFolder() {
        try {
            theoremLoader.loadTheoremsFromMMTFolder(logicalSystem, messages);
        } catch (final TheoremLoaderException e) {
            messages.accumErrorMessage(e.getMessage());
        }
        return messages;
    }

    /**
     * Invokes GMFF to export the current Proof Worksheet.
     * 
     * @param proofText Proof Worksheet text string.
     * @return Messages object.
     */
    public Messages exportViaGMFF(final String proofText) {

        try {
            final String confirmationMessage = logicalSystem.getGMFFManager()
                .exportProofWorksheet(proofText, null); // use default file name
                                                        // for export
            if (confirmationMessage != null)
                messages.accumInfoMessage(confirmationMessage);
        } catch (final GMFFException e) {
            messages
                .accumErrorMessage(PaConstants.ERRMSG_PA_GUI_EXPORT_VIA_GMFF_FAILED);
            messages.accumErrorMessage(e.getMessage());
        }
        return messages;
    }

    /**
     * Invokes TheoremLoader to extract a theorem to the MMT Folder.
     * <p>
     * This is here because the Proof Assistant GUI doesn't know how to do
     * anything...
     * 
     * @param theorem the theorem to extract
     * @return Messages object.
     */
    public Messages extractTheoremToMMTFolder(final Theorem theorem) {
        try {
            theoremLoader.extractTheoremToMMTFolder(theorem, logicalSystem,
                messages);
        } catch (final TheoremLoaderException e) {
            messages.accumErrorMessage(e.getMessage());
        }
        return messages;
    }

    /**
     * Builds new ProofWorksheet for a theorem.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * Note that the output ProofWorksheet is skeletal and is destined for a
     * straight-shot, output to the GUI screen. The ProofAsst and its components
     * retain no memory of a ProofWorksheet between screen actions. Each time
     * the user requests a new action the text is scraped off the screen and
     * built into a new ProofWorksheet object!
     * <p>
     * Notice also that this function just invokes a ProofWorksheet constructor.
     * Why? Because the ProofAsstGUI.java program has no access to or knowledge
     * of LogicalSystem, Grammar, etc. The only external knowledge it has is
     * ProofAsstPreferences.
     * 
     * @param newTheoremLabel the name of the new proof
     * @return ProofWorksheet initialized skeleton proof
     */
    public ProofWorksheet startNewProof(final String newTheoremLabel) {

        final ProofWorksheet w = new ProofWorksheet(newTheoremLabel,
            proofAsstPreferences, logicalSystem, grammar, messages);
        w.outputCursorInstrumentationIfEnabled();
        return w;
    }

    /**
     * Builds new ProofWorksheet for the next theorem after the current theorem
     * sequence number on the ProofAsstGUI.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * This function is provided for students who wish to work their way through
     * Metamath databases such as set.mm and prove each theorem. It is like
     * Forward-GetProof except that it returns a skeletal Proof Worksheet, and
     * thus helps the student by not revealing the contents of the existing
     * proof in the Metamath database.
     * <p>
     * Note that the output ProofWorksheet is skeletal and is destined for a
     * straight-shot, output to the GUI screen. The ProofAsst and its components
     * retain no memory of a ProofWorksheet between screen actions. Each time
     * the user requests a new action the text is scraped off the screen and
     * built into a new ProofWorksheet object!
     * <p>
     * Notice also that this function just invokes a ProofWorksheet constructor.
     * Why? Because the ProofAsstGUI.java program has no access to or knowledge
     * of LogicalSystem, Grammar, etc. The only external knowledge it has is
     * ProofAsstPreferences.
     * 
     * @param currProofMaxSeq sequence number of current proof on ProofAsstGUI
     *            screen.
     * @return ProofWorksheet initialized skeleton proof
     */
    public ProofWorksheet startNewNextProof(final int currProofMaxSeq) {

        ProofWorksheet w;

        final Theorem theorem = getTheoremForward(currProofMaxSeq, false); // not
                                                                           // a
                                                                           // retry
                                                                           // :)

        if (theorem == null) {
            messages.accumErrorMessage(
                PaConstants.ERRMSG_PA_FWD_BACK_SEARCH_NOTFND, "forward");

            w = new ProofWorksheet(proofAsstPreferences, messages, // oh yeah,
                                                                   // we got 'em
                true, // structuralErrors
                ProofAsstCursor.makeProofStartCursor());
        }
        else
            w = new ProofWorksheet(theorem.getLabel(), proofAsstPreferences,
                logicalSystem, grammar, messages);

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Fetches a Theorem using an input Label String.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * 
     * @param theoremLabel label of Theorem to retrieve from statement table.
     * @return Theorem or null if Label not found or is not a Theorem Stmt
     *         label.
     */
    public Theorem getTheorem(final String theoremLabel) {

        final Stmt stmt = logicalSystem.getStmtTbl().get(theoremLabel.trim());
        if (stmt != null && stmt instanceof Theorem)
            return (Theorem)stmt;
        return null;
    }

    public Stmt getStmt(final String s) {
        return logicalSystem.getStmtTbl().get(s.trim());
    }

    /**
     * Builds ProofWorksheet for an existing theorem.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * 
     * @param oldTheorem theorem to get
     * @param exportFormatUnified true means include step Ref labels
     * @param hypsRandomized true means step Hyps randomized on ProofWorksheet.
     * @return ProofWorksheet initialized.
     */
    public ProofWorksheet getExistingProof(final Theorem oldTheorem,
        final boolean exportFormatUnified, final boolean hypsRandomized)
    {

        ProofWorksheet w = getExportedProofWorksheet(oldTheorem,
            exportFormatUnified, hypsRandomized, false); // deriveFormulas

        final ProofAsstCursor cursor = ProofAsstCursor.makeProofStartCursor();

        if (w == null)
            w = new ProofWorksheet(oldTheorem.getLabel(), proofAsstPreferences,
                logicalSystem, grammar, messages);
        else
            w.setProofCursor(cursor);

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Builds ProofWorksheet for the next theorem after a given MObj sequence
     * number.
     * <p>
     * The search wraps to the start if the end is reached.
     * <p>
     * Note: The search list excludes any Theorems excluded by the user from
     * Proof Unification (see RunParm ProofAsstUnifySearchExclude). The
     * exclusion is made for technical reasons (expediency) -- if you don't like
     * it a whole lot we can change it.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * 
     * @param currProofMaxSeq sequence number of ProofWorksheet from
     *            ProofAsstGUI currProofMaxSeq field.
     * @param exportFormatUnified true means include step Ref labels
     * @param hypsRandomized true means step Hyps randomized on ProofWorksheet.
     * @return ProofWorksheet initialized.
     */
    public ProofWorksheet getNextProof(final int currProofMaxSeq,
        final boolean exportFormatUnified, final boolean hypsRandomized)
    {

        ProofWorksheet w;

        final ProofAsstCursor cursor = ProofAsstCursor.makeProofStartCursor();

        // not a retry :)
        final Theorem theorem = getTheoremForward(currProofMaxSeq, false);
        if (theorem == null) {
            messages.accumErrorMessage(
                PaConstants.ERRMSG_PA_FWD_BACK_SEARCH_NOTFND, "forward");
            w = new ProofWorksheet(proofAsstPreferences,
            /* oh yeah, we got 'em */messages, /* structuralErrors=*/true,
                cursor);
        }
        else {
            w = getExportedProofWorksheet(theorem, exportFormatUnified,
                hypsRandomized, /* deriveFormulas=*/false);

            if (w == null)
                w = new ProofWorksheet(theorem.getLabel(),
                    proofAsstPreferences, logicalSystem, grammar, messages);
            else
                w.setProofCursor(cursor);
        }

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Builds ProofWorksheet for the first theorem before a given MObj sequence
     * number.
     * <p>
     * The search wraps to the end if the start is reached.
     * <p>
     * Note: The search list excludes any Theorems excluded by the user from
     * Proof Unification (see RunParm ProofAsstUnifySearchExclude). The
     * exclusion is made for technical reasons (expediency) -- if you don't like
     * it a whole lot we can change it.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * 
     * @param currProofMaxSeq sequence number of ProofWorksheet from
     *            ProofAsstGUI currProofMaxSeq field.
     * @param exportFormatUnified true means include step Ref labels
     * @param hypsRandomized true means step Hyps randomized on ProofWorksheet.
     * @return ProofWorksheet initialized.
     */
    public ProofWorksheet getPreviousProof(final int currProofMaxSeq,
        final boolean exportFormatUnified, final boolean hypsRandomized)
    {

        ProofWorksheet w;

        final ProofAsstCursor cursor = ProofAsstCursor.makeProofStartCursor();

        final Theorem theorem = getTheoremBackward(currProofMaxSeq, false); // not
                                                                            // a
                                                                            // retry
                                                                            // :)

        if (theorem == null) {
            messages.accumErrorMessage(
                PaConstants.ERRMSG_PA_FWD_BACK_SEARCH_NOTFND, "backward");
            w = new ProofWorksheet(proofAsstPreferences, messages, // oh yeah,
                                                                   // we got 'em
                true, // structuralErrors
                cursor);
        }
        else {
            w = getExportedProofWorksheet(theorem, exportFormatUnified,
                hypsRandomized, false); // deriveFormulas

            if (w == null)
                w = new ProofWorksheet(theorem.getLabel(),
                    proofAsstPreferences, logicalSystem, grammar, messages);
            else
                w.setProofCursor(cursor);
        }

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Attempts Unification for a proof contained in a String proof text area.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * The ProofWorksheetParser class is used to parse the input proof text. The
     * reason for using this intermediary is that the system is designed to be
     * able to read a file of proof texts (which is a feature designed for
     * testing purposes, but still available to a user via the BatchMMJ2
     * facility.)
     * 
     * @param proofText proof text from ProofAsstGUI screen, or any String
     *            conforming to the formatting rules of ProofAsst.
     * @param renumReq renumbering of proof steps requested
     * @param noConvertWV true if we should not replace work vars with dummy
     *            vars in derivation steps
     * @param preprocessRequest if not null specifies an editing operation to be
     *            applied to the proof text before other processing.
     * @param inputCursorPos caret offset plus one of input or -1 if caret pos
     *            unavailable to caller.
     * @param stepRequest may be null, or StepSelector Search or Choice request
     *            and will be loaded into the ProofWorksheet.
     * @param tlRequest may be null or a TLRequest.
     * @return ProofWorksheet unified.
     */
    public ProofWorksheet unify(final boolean renumReq,
        final boolean noConvertWV, final String proofText,
        final PreprocessRequest preprocessRequest,
        final StepRequest stepRequest, final TLRequest tlRequest,
        final int inputCursorPos)
    {

        String proofTextEdited;
        if (preprocessRequest == null)
            proofTextEdited = proofText;
        else
            try {
                proofTextEdited = preprocessRequest.doIt(proofText);
            } catch (final ProofAsstException e) {
                messages.accumErrorMessage(e.getMessage());
                return updateWorksheetWithException(null, -1, -1, -1);
            }

        final boolean[] errorFound = new boolean[1];
        final ProofWorksheet proofWorksheet = getParsedProofWorksheet(
            proofTextEdited, errorFound, inputCursorPos, stepRequest);

        if (errorFound[0] == false) {

            if (renumReq)
                proofWorksheet.renumberProofSteps(
                    PaConstants.PROOF_STEP_RENUMBER_START,
                    PaConstants.PROOF_STEP_RENUMBER_INTERVAL);

            unifyProofWorksheet(proofWorksheet, noConvertWV);
        }

        if (tlRequest != null && proofWorksheet.getGeneratedProofStmt() != null)
            try {
                tlRequest.doIt(theoremLoader, proofWorksheet, logicalSystem,
                    messages, this);
            } catch (final TheoremLoaderException e) {
                messages.accumErrorMessage(e.getMessage());
            }

        proofWorksheet.outputCursorInstrumentationIfEnabled();

        return proofWorksheet;

    }

    /**
     * Reformats a ProofWorksheet using TMFF.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * Reformatting is not attempted if the ProofWorksheet has structural errors
     * (returned from the parser).
     * 
     * @param inputCursorStep set to true to reformat just the proof step
     *            underneath the cursor.
     * @param proofText proof text from ProofAsstGUI screen, or any String
     *            conforming to the formatting rules of ProofAsst.
     * @param inputCursorPos caret offset plus one of input or -1 if caret pos
     *            unavailable to caller.
     * @return ProofWorksheet reformatted, or not, if errors.
     */
    public ProofWorksheet tmffReformat(final boolean inputCursorStep,
        final String proofText, final int inputCursorPos)
    {

        final boolean[] errorFound = new boolean[1];

        final ProofWorksheet proofWorksheet = getParsedProofWorksheet(
            proofText, errorFound, inputCursorPos, null);

        if (errorFound[0] == false) {
            proofWorksheet.setProofCursor(proofWorksheet.proofInputCursor);
            proofWorksheet.tmffReformat(inputCursorStep);
        }

        proofWorksheet.outputCursorInstrumentationIfEnabled();

        return proofWorksheet;

    }

    /**
     * Import Theorem proofs from memory and unifies.
     * <p>
     * This is a simulation routine for testing purposes.
     * 
     * @param messages Messages object for output messages.
     * @param selectorAll true if process all theorems, false or null, ignore
     *            param.
     * @param selectorCount use if not null to restrict the number of theorems
     *            present.
     * @param selectorTheorem just process one theorem, ignore selectorCount and
     *            selectorAll.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param asciiRetest instructs program to re-unify the output Proof
     *            Worksheet text after unification.
     */
    public void importFromMemoryAndUnify(final Messages messages,
        final Boolean selectorAll, final Integer selectorCount,
        final Theorem selectorTheorem, final OutputBoss outputBoss,
        final boolean asciiRetest)
    {
        this.messages = messages;

        final boolean unifiedFormat = proofAsstPreferences
            .getExportFormatUnified();
        final boolean hypsRandomized = proofAsstPreferences
            .getExportHypsRandomized();
        final boolean deriveFormulas = proofAsstPreferences
            .getExportDeriveFormulas();
        final boolean verifierRecheck = proofAsstPreferences
            .getRecheckProofAsstUsingProofVerifier();

        String proofText;
        ProofWorksheet proofWorksheet;
        String updatedProofText;
        if (selectorTheorem != null) {
            proofText = exportOneTheorem(null, // to memory not writer
                selectorTheorem, unifiedFormat, hypsRandomized, deriveFormulas);
            if (proofText != null) {
                if (asciiRetest)
                    proofAsstPreferences
                        .setRecheckProofAsstUsingProofVerifier(false);
                proofWorksheet = unify(false, // no renum
                    true, // don't convert work vars
                    proofText, null, // no preprocess
                    null, // no step request
                    null, // no TL request
                    -1); // inputCursorPos
                if (asciiRetest)
                    proofAsstPreferences
                        .setRecheckProofAsstUsingProofVerifier(verifierRecheck);
                updatedProofText = proofWorksheet.getOutputProofText();

                // retest
                if (updatedProofText != null && asciiRetest)
                    unify(false, // no renum
                        true, // don't convert work vars
                        updatedProofText, null, // no preprocess request
                        null, // no step request
                        null, // no TL request
                        -1); // inputCursorPos

                if (updatedProofText != null) {
                    printProof(outputBoss,
                        getErrorLabelIfPossible(proofWorksheet),
                        updatedProofText);
                    checkAndCompareUpdateDJs(proofWorksheet);
                }
            }
            return;
        }

        initVolumeTestStats();

        int numberToProcess = 0;
        int numberProcessed = 0;
        if (selectorAll != null) {
            if (selectorAll.booleanValue())
                numberToProcess = Integer.MAX_VALUE;
        }
        else if (selectorCount != null)
            numberToProcess = selectorCount.intValue();

        for (final Theorem theorem : getSortedTheoremIterable(0)) {
            if (numberProcessed >= numberToProcess
                || messages.maxErrorMessagesReached())
                return;
            nbrTestTheoremsProcessed++;
            proofText = exportOneTheorem(null, theorem, unifiedFormat,
                hypsRandomized, deriveFormulas);
            if (proofText != null) {
                if (asciiRetest)
                    proofAsstPreferences
                        .setRecheckProofAsstUsingProofVerifier(false);
                // for Volume Testing
                final long startNanoTime = System.nanoTime();
                proofWorksheet = unify(false, // no renum
                    true, // don't convert work vars
                    proofText, null, // no preprocess
                    null, // no step request
                    null, // no TL request
                    -1); // inputCursorPos
                final long endNanoTime = System.nanoTime();

                if (asciiRetest)
                    proofAsstPreferences
                        .setRecheckProofAsstUsingProofVerifier(verifierRecheck);

                volumeTestOutputRoutine(startNanoTime, endNanoTime,
                    proofWorksheet, theorem);
                updatedProofText = proofWorksheet.getOutputProofText();

                // retest
                if (updatedProofText != null && asciiRetest)
                    unify(false, // no renum
                        true, // don't convert work vars
                        updatedProofText, null, // no preprocess request
                        null, // no step request
                        null, // no TL request
                        -1); // inputCursorPos

                if (updatedProofText != null) {
                    printProof(outputBoss,
                        getErrorLabelIfPossible(proofWorksheet),
                        updatedProofText);
                    checkAndCompareUpdateDJs(proofWorksheet);
                }
            }
            numberProcessed++;
        }
        printVolumeTestStats();
    }

    /**
     * Import Theorem proofs from a given Reader.
     * 
     * @param importReader source of proofs
     * @param messages Messages object for output messages.
     * @param selectorAll true if process all theorems, false or null, ignore
     *            param.
     * @param selectorCount use if not null to restrict the number of theorems
     *            present.
     * @param selectorTheorem just process one theorem, ignore selectorCount and
     *            selectorAll.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param asciiRetest instructs program to re-unify the output Proof
     *            Worksheet text after unification.
     */
    public void importFromFileAndUnify(
        final Reader importReader, // already open
        final Messages messages, final Boolean selectorAll,
        final Integer selectorCount, final Theorem selectorTheorem,
        final OutputBoss outputBoss, final boolean asciiRetest)
    {

        this.messages = messages;

        final boolean verifierRecheck = proofAsstPreferences
            .getRecheckProofAsstUsingProofVerifier();

        ProofWorksheetParser proofWorksheetParser = null;
        ProofWorksheet proofWorksheet = null;
        String updatedProofText;

        String theoremLabel;

        int numberToProcess = 0;
        int numberProcessed = 0;

        if (selectorAll != null) {
            if (selectorAll.booleanValue())
                numberToProcess = Integer.MAX_VALUE;
        }
        else if (selectorCount != null)
            numberToProcess = selectorCount.intValue();

        try {
            proofWorksheetParser = new ProofWorksheetParser(importReader,
                PaConstants.PROOF_TEXT_READER_CAPTION, proofAsstPreferences,
                logicalSystem, grammar, messages);

            while (true) {

                proofWorksheet = proofWorksheetParser.next();

                theoremLabel = proofWorksheet.getTheoremLabel();

                if (proofWorksheet.hasStructuralErrors()) {
                    messages.accumErrorMessage(
                        PaConstants.ERRMSG_PA_IMPORT_STRUCT_ERROR,
                        getErrorLabelIfPossible(proofWorksheet));
                    break;
                }

                if (messages.maxErrorMessagesReached())
                    break;

                if (selectorTheorem != null)
                    if (selectorTheorem.getLabel().equals(theoremLabel)) {

                        if (asciiRetest)
                            proofAsstPreferences
                                .setRecheckProofAsstUsingProofVerifier(false);

                        unifyProofWorksheet(proofWorksheet, false);

                        if (asciiRetest)
                            proofAsstPreferences
                                .setRecheckProofAsstUsingProofVerifier(verifierRecheck);

                        updatedProofText = proofWorksheet.getOutputProofText();

                        // retest
                        if (updatedProofText != null && asciiRetest)
                            unify(false, // no renum
                                false, // convert work vars
                                updatedProofText, null, // no preprocess request
                                null, // no step request
                                null, // no TL request
                                -1); // inputCursorPos

                        if (updatedProofText != null) {
                            printProof(outputBoss,
                                getErrorLabelIfPossible(proofWorksheet),
                                updatedProofText);
                            checkAndCompareUpdateDJs(proofWorksheet);
                        }
                        break;
                    }
                    else {
                        if (!proofWorksheetParser.hasNext())
                            break;
                        continue;
                    }

                if (numberProcessed >= numberToProcess)
                    break;

                if (asciiRetest)
                    proofAsstPreferences
                        .setRecheckProofAsstUsingProofVerifier(false);

                unifyProofWorksheet(proofWorksheet, false);

                if (asciiRetest)
                    proofAsstPreferences
                        .setRecheckProofAsstUsingProofVerifier(verifierRecheck);

                updatedProofText = proofWorksheet.getOutputProofText();

                // retest
                if (updatedProofText != null && asciiRetest)
                    unify(false, // no renum
                        false, // convert work vars
                        updatedProofText, null, // no preprocess request
                        null, // no step request
                        null, // no TL request
                        -1); // inputCursorPos

                if (updatedProofText != null) {
                    printProof(outputBoss,
                        getErrorLabelIfPossible(proofWorksheet),
                        updatedProofText);
                    checkAndCompareUpdateDJs(proofWorksheet);
                }
                numberProcessed++;
                if (!proofWorksheetParser.hasNext())
                    break;
            }
        }

        catch (final ProofAsstException e) {
            messages.accumErrorMessage(PaConstants.ERRMSG_PA_IMPORT_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet,
                e.lineNbr, e.columnNbr, e.charNbr);
        } catch (final MMIOError e) {
            messages.accumErrorMessage(PaConstants.ERRMSG_PA_IMPORT_IO_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet,
                e.lineNbr, e.columnNbr, e.charNbr);
        } catch (final Exception e) {
            e.printStackTrace();
            messages.accumErrorMessage(
                PaConstants.ERRMSG_PA_IMPORT_SEVERE_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet, -1,
                -1, -1);
        } finally {
            if (proofWorksheetParser != null)
                proofWorksheetParser.closeReader();
        }
    }

    /**
     * Exercises the PreprocessRequest code for one proof.
     * 
     * @param proofText one Proof Text Area in a String.
     * @param messages Messages object for output messages.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param preprocessRequest to apply before unification
     */
    public void preprocessRequestBatchTest(final String proofText,
        final Messages messages, final OutputBoss outputBoss,
        final PreprocessRequest preprocessRequest)
    {

        this.messages = messages;

        String updatedProofText = null;

        if (proofText != null)
            printProof(outputBoss, " ", proofText);

        final ProofWorksheet proofWorksheet = unify(false, // no renum
            false, // convert work vars
            proofText, preprocessRequest, null, // no step request
            null, // no TL request
            -1); // inputCursorPos

        updatedProofText = proofWorksheet.getOutputProofText();

        if (updatedProofText != null)
            printProof(outputBoss, getErrorLabelIfPossible(proofWorksheet),
                updatedProofText);

    }

    /**
     * Exercises the StepSelectorSearch for one proof.
     * 
     * @param importReader source of proofs
     * @param messages Messages object for output messages.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param cursorPos offset of input cursor.
     * @param selectionNumber choice from StepSelectorResults
     */
    public void stepSelectorBatchTest(
        final Reader importReader, // already open
        final Messages messages, final OutputBoss outputBoss,
        final int cursorPos, final int selectionNumber)
    {

        this.messages = messages;

        ProofWorksheet proofWorksheet = null;
        ProofWorksheetParser proofWorksheetParser = null;

        String origProofText = null;
        String updatedProofText = null;
        try {
            proofWorksheetParser = new ProofWorksheetParser(importReader,
                PaConstants.PROOF_TEXT_READER_CAPTION, proofAsstPreferences,
                logicalSystem, grammar, messages);

            proofWorksheet = proofWorksheetParser.next(cursorPos + 1,
                new StepRequest(PaConstants.STEP_REQUEST_SELECTOR_SEARCH));

            final String theoremLabel = proofWorksheet.getTheoremLabel();

            if (!proofWorksheet.hasStructuralErrors()) {
                origProofText = proofWorksheet.getOutputProofText();
                unifyProofWorksheet(proofWorksheet, false);
            }
            if (proofWorksheet.hasStructuralErrors()) {
                messages.accumErrorMessage(
                    PaConstants.ERRMSG_PA_IMPORT_STRUCT_ERROR,
                    getErrorLabelIfPossible(proofWorksheet));
                closeProofWorksheetParser(proofWorksheetParser);
                return;
            }
            if (proofWorksheet.stepSelectorResults == null) {
                messages.accumErrorMessage(
                    PaConstants.ERRMSG_STEP_SELECTOR_BATCH_TEST_NO_RESULTS,
                    getErrorLabelIfPossible(proofWorksheet));
                closeProofWorksheetParser(proofWorksheetParser);
                return;
            }

            final StepSelectorResults results = proofWorksheet.stepSelectorResults;

            printStepSelectorResults(outputBoss, theoremLabel, results);

            if (selectionNumber < 0
                || selectionNumber >= results.selectionArray.length)
            {
                messages.accumErrorMessage(
                    PaConstants.ERRMSG_STEP_SELECTOR_BATCH_TEST_INV_CHOICE,
                    getErrorLabelIfPossible(proofWorksheet), selectionNumber,
                    results.selectionArray.length - 1);
                closeProofWorksheetParser(proofWorksheetParser);
                return;
            }

            final String step = results.step;
            final Assrt assrt = results.refArray[selectionNumber];
            final String selection = results.selectionArray[selectionNumber];
            messages.accumInfoMessage(
                PaConstants.ERRMSG_STEP_SELECTOR_BATCH_TEST_CHOICE,
                getErrorLabelIfPossible(proofWorksheet), step, selectionNumber,
                assrt.getLabel(), selection);

            final StepRequest stepRequestChoice = new StepRequest(
                PaConstants.STEP_REQUEST_SELECTOR_CHOICE, results.step,
                results.refArray[selectionNumber]);

            proofWorksheet = unify(false, // no renumReq,
                false, // convert work vars
                origProofText, null, // no preprocessRequest,
                stepRequestChoice, null, // no TL request
                cursorPos + 1);

            updatedProofText = proofWorksheet.getOutputProofText();
            if (updatedProofText != null)
                printProof(outputBoss, getErrorLabelIfPossible(proofWorksheet),
                    updatedProofText);
        }

        catch (final ProofAsstException e) {
            messages.accumErrorMessage(PaConstants.ERRMSG_PA_IMPORT_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet,
                e.lineNbr, e.columnNbr, e.charNbr);
        } catch (final MMIOError e) {
            messages.accumErrorMessage(PaConstants.ERRMSG_PA_IMPORT_IO_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet,
                e.lineNbr, e.columnNbr, e.charNbr);
        } catch (final Exception e) {
            e.printStackTrace();
            messages.accumErrorMessage(
                PaConstants.ERRMSG_PA_IMPORT_SEVERE_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet, -1,
                -1, -1);
        } finally {
            closeProofWorksheetParser(proofWorksheetParser);
        }
    }

    /**
     * Export Theorem proofs to a given Writer.
     * <p>
     * Uses ProofAsstPreferences.getExportFormatUnified() to determine whether
     * output proof derivation steps contain Ref statement labels (if "unified"
     * then yes, add labels.)
     * <p>
     * An incomplete input proof generates an incomplete output proof as well as
     * an error message.
     * 
     * @param exportWriter destination for output proofs.
     * @param messages Messages object for output messages.
     * @param selectorAll true if process all theorems, false or null, ignore
     *            param.
     * @param selectorCount use if not null to restrict the number of theorems
     *            present.
     * @param selectorTheorem just process one theorem, ignore selectorCount and
     *            selectorAll.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     */
    public void exportToFile(
        final Writer exportWriter, // already open
        final Messages messages, final Boolean selectorAll,
        final Integer selectorCount, final Theorem selectorTheorem,
        final OutputBoss outputBoss)
    {

        final boolean exportFormatUnified = proofAsstPreferences
            .getExportFormatUnified();

        final boolean hypsRandomized = proofAsstPreferences
            .getExportHypsRandomized();

        final boolean deriveFormulas = proofAsstPreferences
            .getExportDeriveFormulas();

        this.messages = messages;

        if (selectorTheorem != null) {
            final String proofText = exportOneTheorem(exportWriter,
                selectorTheorem, exportFormatUnified, hypsRandomized,
                deriveFormulas);
            if (proofText != null)
                printProof(outputBoss, selectorTheorem.getLabel(), proofText);
            return;
        }

        int numberToExport = 0;
        int numberExported = 0;
        if (selectorAll != null) {
            if (selectorAll.booleanValue())
                numberToExport = Integer.MAX_VALUE;
        }
        else if (selectorCount != null)
            numberToExport = selectorCount.intValue();

        for (final Theorem theorem : getSortedTheoremIterable(0)) {
            if (numberExported < numberToExport)
                break;
            final String proofText = exportOneTheorem(exportWriter, theorem,
                exportFormatUnified, hypsRandomized, deriveFormulas);
            if (proofText != null)
                printProof(outputBoss, theorem.getLabel(), proofText);
            numberExported++;
        }
    }
    // Note: could do binary lookup for sequence number
    // within ArrayList which happens to be sorted.
    private Theorem getTheoremBackward(final int currProofMaxSeq,
        final boolean isRetry)
    {
        final List<Assrt> searchList = proofUnifier
            .getUnifySearchListByMObjSeq();

        for (final ListIterator<Assrt> li = searchList.listIterator(searchList
            .size()); li.hasPrevious();)
        {
            final Assrt assrt = li.previous();
            if (assrt.getSeq() < currProofMaxSeq && assrt instanceof Theorem)
                return (Theorem)assrt;
        }
        if (isRetry)
            return null;
        return getTheoremBackward(Integer.MAX_VALUE, true);
    }

    // Note: could do binary lookup for sequence number
    // within ArrayList which happens to be sorted.
    private Theorem getTheoremForward(final int currProofMaxSeq,
        final boolean isRetry)
    {

        if (currProofMaxSeq != Integer.MAX_VALUE) {

            final List<Assrt> searchList = proofUnifier
                .getUnifySearchListByMObjSeq();

            for (final Assrt assrt : searchList)
                if (assrt.getSeq() > currProofMaxSeq
                    && assrt instanceof Theorem)
                    return (Theorem)assrt;
        }
        if (isRetry)
            return null;

        return getTheoremForward(Integer.MIN_VALUE, true);
    }

    /**
     * Parses a Proof Worksheet text area and returns a Proof Worksheet plus an
     * error flag.
     * <p>
     * Note that the ProofWorksheetParser invokes the logic to perform parsing
     * and "structural" edits so that other logic such as ProofUnifier have a
     * clean ProofWorksheet. A "structural error" means an error like a formula
     * with a syntax error, or a Hyp referring to a non-existent step, etc.
     * 
     * @param proofText proof text from ProofAsstGUI screen, or any String
     *            conforming to the formatting rules of ProofAsst.
     * @param errorFound boolean array of 1 element that is output as true if an
     *            error was found.
     * @param inputCursorPos caret offset plus one of input or -1 if caret pos
     *            unavailable to caller.
     * @param stepRequest may be null, or StepSelector Search or Choice request
     *            and will be loaded into the ProofWorksheet.
     * @return ProofWorksheet unified.
     */
    private ProofWorksheet getParsedProofWorksheet(final String proofText,
        final boolean[] errorFound, final int inputCursorPos,
        final StepRequest stepRequest)
    {

        ProofWorksheetParser proofWorksheetParser = null;
        ProofWorksheet proofWorksheet = null;
        errorFound[0] = true;
        try {
            proofWorksheetParser = new ProofWorksheetParser(proofText,
                "Proof Text", proofAsstPreferences, logicalSystem, grammar,
                messages);

            proofWorksheet = proofWorksheetParser.next(inputCursorPos,
                stepRequest);

            errorFound[0] = false;

        } catch (final ProofAsstException e) {
            messages.accumErrorMessage(e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet,
                e.lineNbr, e.columnNbr, e.charNbr);
        } catch (final MMIOError e) {
            messages.accumErrorMessage(PaConstants.ERRMSG_PA_UNIFY_IO_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet,
                e.lineNbr, e.columnNbr, e.charNbr);
        } catch (final IOException e) {
            e.printStackTrace();
            messages.accumErrorMessage(
                PaConstants.ERRMSG_PA_UNIFY_SEVERE_ERROR,
                getErrorLabelIfPossible(proofWorksheet), e.getMessage());
            proofWorksheet = updateWorksheetWithException(proofWorksheet, -1,
                -1, -1);
        } finally {
            if (proofWorksheetParser != null)
                proofWorksheetParser.closeReader();
        }
        return proofWorksheet;
    }

    private void initVolumeTestStats() {
        nbrTestTheoremsProcessed = 0;
        nbrTestNotProvedPerfectly = 0;
        nbrTestProvedDifferently = 0;
    }

    private void printVolumeTestStats() {
        messages.accumInfoMessage(PaConstants.ERRMSG_PA_TESTMSG_03,
            nbrTestTheoremsProcessed, nbrTestNotProvedPerfectly,
            nbrTestProvedDifferently);
    }

    private void volumeTestOutputRoutine(final long startNanoTime,
        final long endNanoTime, final ProofWorksheet proofWorksheet,
        final Theorem theorem)
    {

        final DerivationStep q = proofWorksheet.getQedStep();

        if (q == null) {
            nbrTestNotProvedPerfectly++;
            messages.accumInfoMessage(PaConstants.ERRMSG_PA_TESTMSG_01,
                theorem.getLabel(), (startNanoTime + 50000000) / endNanoTime,
                9999999, "No qed step found!");
            return;
        }

        // this 's' is for batch testing. the numbers
        // are for backward compatibility to the old
        // ProofWorkStmt.status values.
        int s;
        if (q.proofTree == null)
            s = 4; // arbitrary
        else if (q.djVarsErrorStatus == PaConstants.DJ_VARS_ERROR_STATUS_NO_ERRORS)
        {
            if (!q.verifyProofError)
                s = 8; // proved perfectly
            else {
                s = 10; // verify proof err
                nbrTestNotProvedPerfectly++;
            }
        }
        else {
            s = 9; // dj vars error
            nbrTestNotProvedPerfectly++;
        }

        messages.accumInfoMessage(PaConstants.ERRMSG_PA_TESTMSG_01,
            theorem.getLabel(), (startNanoTime + 50000000) / endNanoTime, s,
            PaConstants.STATUS_DESC[s]);

        if (q != null) {
            RPNStep[] newProof;
            if (q.proofTree == null)
                newProof = new RPNStep[0];
            else
                newProof = q.proofTree.convertToRPNExpanded();
            final RPNStep[] oldProof = new ParseTree(proofWorksheet
                .getTheorem().getProof()).convertToRPNExpanded();

            boolean differenceFound = false;
            String oldStmtDiff = "";
            String newStmtDiff = "";

            if (oldProof == null) {
                if (newProof.length == 0) {
                    // equal
                }
                else {
                    differenceFound = true;
                    newStmtDiff = newProof[0].stmt.getLabel();
                }
            }
            else {
                int i = 0;
                while (true) {
                    if (i < oldProof.length && i < newProof.length) {
                        if (newProof[i].stmt == oldProof[i].stmt) {
                            i++;
                            continue;
                        }
                        oldStmtDiff = oldProof[i].stmt.getLabel();
                        newStmtDiff = newProof[i].stmt.getLabel();
                    }
                    else if (i < oldProof.length)
                        oldStmtDiff = oldProof[i].stmt.getLabel();
                    else if (i < newProof.length)
                        newStmtDiff = newProof[i].stmt.getLabel();
                    else
                        break; // no differences
                    differenceFound = true;
                    break;
                }
            }

            if (differenceFound) {
                nbrTestProvedDifferently++;
                messages.accumInfoMessage(PaConstants.ERRMSG_PA_TESTMSG_02,
                    theorem.getLabel(), oldStmtDiff, newStmtDiff);
            }

        }

        // end Volume Testing code.
    }

    private void printStepSelectorResults(final OutputBoss outputBoss,
        final String theoremLabel, final StepSelectorResults results)
    {

        if (outputBoss == null || results == null)
            return;

        try {
            outputBoss.sysOutPrint(LangException.format(
                PaConstants.ERRMSG_STEP_SELECTOR_RESULTS_PRINT, theoremLabel,
                results.step));
            String label;
            for (int i = 0; i < results.refArray.length; i++) {
                if (results.refArray[i] == null)
                    label = " ";
                else
                    label = results.refArray[i].getLabel();
                outputBoss.sysOutPrint(" " + i + " " + label + " "
                    + results.selectionArray[i]
                    + "\n");
            }
        } catch (final IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(LangException.format(
                PaConstants.ERRMSG_PA_PRINT_IO_ERROR, theoremLabel,
                e.getMessage()));
        }

    }
    private void closeProofWorksheetParser(
        final ProofWorksheetParser proofWorksheetParser)
    {
        if (proofWorksheetParser != null)
            proofWorksheetParser.closeReader();
    }

    public String exportOneTheorem(final String theoremLabel) {

        final Theorem theorem = getTheorem(theoremLabel);
        if (theorem == null)
            throw new IllegalArgumentException(LangException.format(
                PaConstants.ERRMSG_PA_GET_THEOREM_NOT_FOUND, theoremLabel));
        return exportOneTheorem(theorem);
    }

    public String exportOneTheorem(final Theorem theorem) {
        return exportOneTheorem(null, // no export writer
            theorem, true, // exportFormatUnified
            false, // hypsRandomized
            false); // deriveFormulas
    }

    private String exportOneTheorem(
        final Writer exportWriter, // already open
        final Theorem theorem, final boolean exportFormatUnified,
        final boolean hypsRandomized, final boolean deriveFormulas)
    {

        final ProofWorksheet proofWorksheet = getExportedProofWorksheet(
            theorem, exportFormatUnified, hypsRandomized, deriveFormulas);

        if (proofWorksheet == null)
            return null;

        final String proofText = proofWorksheet.getOutputProofText();

        if (proofText == null)
            throw new IllegalArgumentException(LangException.format(
                PaConstants.ERRMSG_PA_EXPORT_STRUCT_ERROR,
                getErrorLabelIfPossible(proofWorksheet)));

        if (exportWriter != null)
            try {
                exportWriter.write(proofText);
                exportWriter.write('\n');
            } catch (final IOException e) {
                throw new IllegalArgumentException(LangException.format(
                    PaConstants.ERRMSG_PA_EXPORT_IO_ERROR,
                    getErrorLabelIfPossible(proofWorksheet), e.getMessage()));
            }
        return proofText;
    }

    private ProofWorksheet getExportedProofWorksheet(final Theorem theorem,
        final boolean exportFormatUnified, final boolean hypsRandomized,
        final boolean deriveFormulas)
    {

        ProofWorksheet proofWorksheet = null;
        List<ProofDerivationStepEntry> proofDerivationStepList;

        try {
            proofDerivationStepList = verifyProofs.getProofDerivationSteps(
                theorem, exportFormatUnified, hypsRandomized,
                getProvableLogicStmtTyp());

            proofWorksheet = new ProofWorksheet(theorem,
                proofDerivationStepList, deriveFormulas, proofAsstPreferences,
                logicalSystem, grammar, messages);
        } catch (final VerifyException e) {
            messages.accumErrorMessage(PaConstants.ERRMSG_PA_EXPORT_PV_ERROR,
                theorem.getLabel(), e.getMessage());
        }

        return proofWorksheet;
    }

    private void unifyProofWorksheet(final ProofWorksheet proofWorksheet,
        final boolean noConvertWV)
    {

        if (proofWorksheet.getNbrDerivStepsReadyForUnify() > 0
            || proofWorksheet.stepRequest != null
            && (proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SELECTOR_SEARCH
                || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_STEP_SEARCH
                || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_SEARCH_OPTIONS || proofWorksheet.stepRequest.request == PaConstants.STEP_REQUEST_GENERAL_SEARCH))
        {

            try {
                proofUnifier.unifyAllProofDerivationSteps(proofWorksheet,
                    messages, noConvertWV);
            } catch (final VerifyException e) {
                // this is a particularly severe situation
                // caused by a shortage of allocatable
                // work variables -- the user will need
                // to restart mmj2 after updating RunParm.txt
                // with more Work Variables (assuming there
                // is no bug...)
                messages.accumErrorMessage(e.getMessage());
                proofWorksheet.setStructuralErrors(true);
                return;
            }

            final RPNStep[] rpnProof = proofAsstPreferences
                .getProofFormatPacked() ? proofWorksheet
                .getQedStepSquishedRPN() : proofWorksheet.getQedStepProofRPN();

            if (rpnProof == null) {
                /*
                 * NOTE: no msg needed, we get a message on each
                 * invalid step from the unify process.
                 */
            }
            else {
                if (proofAsstPreferences.getProofFormatCompressed()) {
                    final StringBuilder letters = new StringBuilder();

                    final List<Hyp> mandHypList = new ArrayList<Hyp>();
                    final List<VarHyp> optHypList = new ArrayList<VarHyp>();
                    ProofUnifier.separateMandAndOptFrame(proofWorksheet,
                        proofWorksheet.getQedStep(), mandHypList, optHypList,
                        true);

                    final int width = proofWorksheet.proofAsstPreferences
                        .getRPNProofRightCol()
                        - proofWorksheet.proofAsstPreferences
                            .getRPNProofLeftCol() + 1;
                    final List<Stmt> parenList = logicalSystem
                        .getProofCompression().compress(
                            proofWorksheet.getTheoremLabel(), width,
                            mandHypList, optHypList, rpnProof, letters);

                    proofWorksheet.addGeneratedProofStmt(parenList,
                        letters.toString());
                }
                else
                    proofWorksheet.addGeneratedProofStmt(rpnProof);
                if (proofAsstPreferences.getDjVarsSoftErrorsGenerate()
                    && proofWorksheet.proofSoftDjVarsErrorList != null
                    && !proofWorksheet.proofSoftDjVarsErrorList.isEmpty())
                    proofWorksheet.generateAndAddDjVarsStmts();
                messages.accumInfoMessage(
                    PaConstants.ERRMSG_PA_RPN_PROOF_GENERATED,
                    getErrorLabelIfPossible(proofWorksheet));
            }
        }
        else
            messages.accumInfoMessage(PaConstants.ERRMSG_PA_NOTHING_TO_UNIFY,
                getErrorLabelIfPossible(proofWorksheet));
        incompleteStepCursorPositioning(proofWorksheet);
    }
    private void incompleteStepCursorPositioning(
        final ProofWorksheet proofWorksheet)
    {

        if (proofWorksheet.getQedStepProofRPN() != null) {
            proofWorksheet.proofCursor.setDontScroll(false);
            proofWorksheet.posCursorAtQedStmt();
            return;
        }

        proofWorksheet.proofCursor.setDontScroll(true);

        if (!proofWorksheet.proofCursor.cursorIsSet) {

            if (proofAsstPreferences.getIncompleteStepCursorLast()) {
                proofWorksheet.posCursorAtLastIncompleteOrQedStmt();
                return;
            }

            if (proofAsstPreferences.getIncompleteStepCursorFirst()) {
                proofWorksheet.posCursorAtFirstIncompleteOrQedStmt();
                return;
            }

            // if (proofWorksheet.hasIncompleteStmt()) {
            proofWorksheet.proofInputCursor.setDontScroll(true);
            // }
            // else {
            // proofWorksheet.proofInputCursor.setDontScroll(false);
            // }

            proofWorksheet.setProofCursor(proofWorksheet.proofInputCursor);
        }
    }

    private ProofWorksheet updateWorksheetWithException(final ProofWorksheet w,
        final long eLineNbr, final long eColumnNbr, final long eCharNbr)
    {
        ProofWorksheet out;

        final ProofAsstCursor proofCursor = new ProofAsstCursor((int)eCharNbr,
            (int)eLineNbr, (int)eColumnNbr);
        if (w == null)
            out = new ProofWorksheet(proofAsstPreferences, messages, true, // structural
                                                                           // err!
                proofCursor);
        else {
            out = w;
            out.setProofCursor(proofCursor);
        }

        return out;
    }

    private void printProof(final OutputBoss outputBoss,
        final String theoremLabel, final String proofText)
    {
        if (outputBoss == null || proofText == null)
            return;

        try {
            outputBoss.sysOutPrint(proofText);
            outputBoss.sysOutPrint("\n");
        } catch (final IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(LangException.format(
                PaConstants.ERRMSG_PA_PRINT_IO_ERROR, theoremLabel,
                e.getMessage()));
        }
    }

    private String getErrorLabelIfPossible(final ProofWorksheet proofWorksheet)
    {
        String label = "unknownLabel";
        if (proofWorksheet != null && proofWorksheet.getTheoremLabel() != null)
            label = proofWorksheet.getTheoremLabel();
        return label;
    }

    public Iterable<Theorem> getSortedSkipSeqTheoremIterable(
        final String startTheoremLabel) throws ProofAsstException
    {
        int lowestMObjSeq = 0;
        if (startTheoremLabel != null) {
            final Theorem theorem = getTheorem(startTheoremLabel);
            if (theorem == null)
                throw new ProofAsstException(
                    PaConstants.ERRMSG_PA_START_THEOREM_NOT_FOUND,
                    startTheoremLabel);
            lowestMObjSeq = theorem.getSeq();
        }

        return getSortedTheoremIterable(lowestMObjSeq);
    }

    private Iterable<Theorem> getSortedTheoremIterable(final int lowestMObjSeq)
    {

        sortedTheoremList = new ArrayList<Theorem>(logicalSystem.getStmtTbl()
            .size());

        for (final Stmt stmt : logicalSystem.getStmtTbl().values())
            if (stmt.getSeq() >= lowestMObjSeq && stmt instanceof Theorem
                && stmt.getFormula().getTyp() == getProvableLogicStmtTyp() &&
                // don't process these during batch testing.
                // for one thing, "dummylink" generates an
                // exception because its proof is invalid
                // for the mmj2 Proof Assistant.
                !proofAsstPreferences.checkUnifySearchExclude((Assrt)stmt))
                sortedTheoremList.add((Theorem)stmt);

        Collections.sort(sortedTheoremList, MObj.SEQ);

        return sortedTheoremList;
    }

    private Cnst getProvableLogicStmtTyp() {
        return grammar.getProvableLogicStmtTypArray()[0];
    }

    private void checkAndCompareUpdateDJs(final ProofWorksheet proofWorksheet) {
        if (proofWorksheet.getQedStepProofRPN() == null
            || proofWorksheet.newTheorem
            || !proofAsstPreferences.getDjVarsSoftErrorsGenerate()
            || proofWorksheet.proofSoftDjVarsErrorList == null
            || proofWorksheet.proofSoftDjVarsErrorList.isEmpty())
            return;

        if (proofAsstPreferences.getImportCompareDJs())
            importCompareDJs(proofWorksheet);

        if (proofAsstPreferences.getImportUpdateDJs())
            importUpdateDJs(proofWorksheet);

    }

    /**
     * OK, at this point the proof is complete and the Proof Worksheet's
     * "comboFrame" has been updated with newly computed DjVars (if
     * GenerateDifferences was used, the differences have been merged with the
     * original, so in every case, comboFrame has a complete set of DjVars.)
     * <p>
     * In addition, this code is only executed *if* DjVars were generated during
     * processing of the ProofWorksheet (if GenerateDifferences or
     * GenerateReplacements were used then there must have been at least one
     * "soft" DjVars error.)
     * <p>
     * In theory we could report Superfluous and Omitted DjVars for the
     * theorem's Mandatory and Optional Frames. However, according to Norm, the
     * interesting thing to learn about is Superfluous Mandatory DjVars
     * restrictions in the theorem.
     * <p>
     * This code does not need to be efficient, as it is used only for testing
     * purposes, so finding Superfluous Mandatory DjVars just means looking up
     * each mandFrame.djVarsArray[i] in the ProofWorksheet's
     * comboFrame.djVarsArray (which happens to be sorted); if not found, then
     * the mandFrame.djVarsArray[i] element is Superflous...
     * 
     * @param proofWorksheet the owner ProofWorksheet
     */
    private void importCompareDJs(final ProofWorksheet proofWorksheet) {
        final List<DjVars> superfluous = new ArrayList<DjVars>();
        final ScopeFrame mandFrame = proofWorksheet.theorem.getMandFrame();
        int compare;
        loopI: for (int i = 0; i < mandFrame.djVarsArray.length; i++) {
            for (int j = 0; j < proofWorksheet.comboFrame.djVarsArray.length; j++)
            {
                compare = mandFrame.djVarsArray[i]
                    .compareTo(proofWorksheet.comboFrame.djVarsArray[j]);
                if (compare > 0)
                    continue;
                if (compare == 0)
                    continue loopI;
                superfluous.add(mandFrame.djVarsArray[i]);
                continue loopI; // not found
            }

            superfluous.add(mandFrame.djVarsArray[i]);
            continue;
        }

        if (!superfluous.isEmpty())
            messages.accumInfoMessage(
                PaConstants.ERRMSG_SUPERFLUOUS_MANDFRAME_DJVARS,
                proofWorksheet.theorem.getLabel(), superfluous);
    }

    private void importUpdateDJs(final ProofWorksheet proofWorksheet) {
        final List<DjVars> list = new ArrayList<DjVars>();

        final ScopeFrame mandFrame = proofWorksheet.theorem.getMandFrame();

        for (final DjVars element : proofWorksheet.comboFrame.djVarsArray)
            if (mandFrame.areBothDjVarsInHypArray(element))
                list.add(element);
        final DjVars[] djArray = new DjVars[list.size()];
        for (int i = 0; i < djArray.length; i++)
            djArray[i] = list.get(i);
        mandFrame.djVarsArray = djArray;
    }
}
