//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * PaConstants.java  0.10 11/01/2011
 *
 * Version 0.02
 * -- added new constants for Proof Assistant "Derive" Feature,
 *    including:
 *        - GREATEST_STEP_NBR_INCREMENT_AMT
 *        - MAX_UNIFY_ALTERNATES
 *        - DUMMY_VAR_PREFIX
 * -- new messages, including
 *        ProofWorksheet:
 *            -- ERRMSG_DERIVE_FEATURE_STEP_NOTFND_1
 * -- new items for ProofAsstGUI renumber feature:
 *        -- PA_GUI_UNIFY_MENU_START_UR_ITEM_TEXT
 * -- new for ProofAsst:
 *        -- PROOF_STEP_RENUMBER_INTERVAL
 *
 * Nov-01-2006 - Version 0.03
 *     - comment update for TMFF project messages.
 *     - Update Proof Text defaults - formula left col = 15,
 *       formula right col = 79.
 *     - removed unnecessary message ERRMSG_FRAME_ERR_1,
 *       E-PA-0336.
 *     - other misc. stuff added to Proof Assistant, like color.
 *     - rewrite Help text.
 *
 * Jun-01-2007 - Version 0.04
 *     - replace generic ProofWorkStmt.status
 *     - etc.
 *
 * Aug-01-2007 - Version 0.05
 *     - Misc Work Variable Enhancements
 *
 * Nov-01-2007 Version 0.06
 *           - Add FIELD_ID_* constants for use in cursor
 *             positioning.
 *           - Changed E-PA-0403 to info message I-PA-0403.
 *           - Added stuff for new "Set Indent" and
 *             "Reformat Proof: Swap Alt" menu items.
 *           - Add new constant, LOCAL_REF_ESCAPE_CHAR
 *             for the new "Local Ref" escape character
 *           - Add ERRMSG_HYP_HAS_LOCAL_REF_1, "E-PA-0377"
 *           - Add ERRMSG_QED_HAS_LOCAL_REF_1, "E-PA-0378"
 *           - Add ERRMSG_BAD_LOCAL_REF_1,     "E-PA-0379"
 *           - Update GeneralHelp screen text for new Edit
 *             menu items and release date (1-Nov-2007).
 *           - Add "ProofAsstErrorMessageRows"    RunParm
 *           - Add "ProofAsstErrorMessageColumns" RunParm
 *           - Add "ProofAsstTextAtTop"           RunParm
 *           - Add I-PA-0119, ERRMSG_PA_RPN_PROOF_GENERATED_1
 *             for ProofAsst.java (previously, no message
 *             resulted in this situation.)
 *
 * Feb-01-2008 Version 0.07
 *           - add PROOF_ASST_FONT_SIZE_CHG_AMT (=2) to replace
 *             program hardcoding.
 *           - add PA_GUI_POPUP_MENU_REFORMAT_STEP_TEXT and
 *                 PA_GUI_POPUP_MENU_REFORMAT_SWAP_ALT_STEP_TEXT
 *             for new single step reformatting option on popup
 *             menu.
 *           - add PROOF_ASST_GUI_STARTUP_MSG to replace hardcoded
 *             "Greetings, friend." message in ProofAsstGUI.
 *           - add PROOF_ASST_INCOMPLETE_STEP_CURSOR_DEFAULT,
 *                 PROOF_ASST_INCOMPLETE_STEP_CURSOR_FIRST
 *                 PROOF_ASST_INCOMPLETE_STEP_CURSOR_LAST
 *             and PROOF_ASST_INCOMPLETE_STEP_CURSOR_ASIS.
 *           - add OUTPUT_CURSOR_INSTRUMENTATION_DEFAULT
 *             and ERRMSG_PA_CURSOR_INSTRUMENTATION message
 *           - add PROOF_ASST_AUTO_REFORMAT_DEFAULT
 *           - Modify HELP_ABOUT_TEXT_1 for copyright dates
 *             and GENERAL_HELP_INFO_TEXT for new/chgd options.
 *
 * Mar-01-2008 Version 0.08
 *           - Various constants for StepSelector classes.
 *           - Remove Hints feature
 *
 * Aug-01-2008 Version 0.09
 *           - Add new ERRMSG_DV_VAR_SCOPE_ERR_1 error message
 *             for $d variables not active in the current scope
 *             (which means that new theorem variables must be
 *             active globally in proof assistant, while existing
 *             theorem variables must be defined in the theorem's
 *             extended frame (mandatory frame + optional frame).
 *           - New TheoremLoader stuff in the ProofAsstGUI.
 *
 * Version 0.10 - Nov-01-2011:  comment update.
 *           - Modify for copyright dates/release information.
 *           - Added PA_GUI_FILE_MENU_EXPORT_VIA_GMFF_ITEM_TEXT
 *           _ Added ERRMSG_PA_GUI_EXPORT_VIA_GMFF_NO_MSGS
 *              ("I-PA-0210 OK! Export Via GMFF complete."
 *             + " No error or info messages were generated.";)
 *           _ Added ERRMSG_PA_GUI_EXPORT_VIA_GMFF_FAILED
 *             ("E-PA-0124 Oops! Export Via GMFF failed."
 *             + " One or more error messages were generated."
 *             + " See following messages";)
 *           - set PROOF_ASST_FORMULA_RIGHT_COL_DEFAULT = 70
 *             (was 79. now matches RunParmsComplete.txt);
 *           - set PA_GUI_DEFAULT_FILE_NAME_SUFFIX to
 *             .mmp (PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP2);
 *             was .txt (PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT2);
 */

package mmj.pa;

import java.awt.Color;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.util.UtilConstants;
import mmj.verify.GrammarConstants;
import mmj.verify.ProofConstants;

/**
 * (Most) Constants used in mmj.pa classes
 * <p>
 * There are two primary types of constants: parameters that are "hardcoded"
 * which affect/control processing, and error/info messages.
 * <p>
 * Each mmj message begins with a code, such as this:
 * <p>
 * <code>E-LA-0007</code>
 * <p>
 * where the format of the code is {@code X-YY-9999}<br>
 * <p>
 * <b>{@code X}</b> : error level
 * <ul>
 * <li>{@code E} = Error
 * <li>{@code I} = Information
 * <li>{@code A} = Abort (processing terminates, usually a bug).
 * </ul>
 * <p>
 * <b>{@code YY}</b> : source code
 * <ul>
 * <li>{@code GM} = mmj.gmff package (see {@link GMFFConstants})
 * <li>{@code GR} = mmj.verify.Grammar and related code (see
 * {@link GrammarConstants})
 * <li>{@code IO} = mmj.mmio package (see {@link MMIOConstants})
 * <li>{@code LA} = mmj.lang package (see {@link GMFFConstants})
 * <li>{@code PA} = mmj.pa package (proof assistant) (see {@link PaConstants})
 * <li>{@code PR} = mmj.verify.VerifyProof and related code (see
 * {@link ProofConstants})
 * <li>{@code TL} = mmj.tl package (Theorem Loader).
 * <li>{@code TM} = mmj.tmff.AlignColumn and related code
 * <li>{@code UT} = mmj.util package. (see {@link UtilConstants})
 * </ul>
 * <p>
 * <b>{@code 9999}</b> : sequential number within the source code, 0001 through
 * 9999.
 */
public class PaConstants {

    public static final String SYNONYM_TRUE_1 = "true";
    public static final String SYNONYM_TRUE_2 = "on";
    public static final String SYNONYM_TRUE_3 = "yes";

    public static final String SYNONYM_FALSE_1 = "false";
    public static final String SYNONYM_FALSE_2 = "off";
    public static final String SYNONYM_FALSE_3 = "no";

    // ----------------------------------------------------------
    // Constants for ProofAsst.java
    // ----------------------------------------------------------

    /**
     * PROOF_STEP_RENUMBER_START = 50
     * <p>
     * Renumber starting point: 1, 2, 3, ... vs 3, 4, 5, ... This is hardcoded,
     * but a RunParm could be added.
     */
    public static final int PROOF_STEP_RENUMBER_START = 50;

    /**
     * PROOF_STEP_RENUMBER_INTERVAL = 1
     * <p>
     * Renumber by interval: 1, 2, ...n or 10, 20, ... etc. This is hardcoded,
     * but a RunParm could be added.
     */
    public static final int PROOF_STEP_RENUMBER_INTERVAL = 1;

    /**
     * PROOF_ASST_INCOMPLETE_STEP_CURSOR_LAST = "Last".
     * <p>
     * Controls how cursor positioned after Unification if there are no errors
     * and at least one "incomplete" proof step: set cursor to Last incomplete
     * proof step.
     */
    public static final String PROOF_ASST_INCOMPLETE_STEP_CURSOR_LAST = "Last";

    /**
     * PROOF_ASST_INCOMPLETE_STEP_CURSOR_FIRST = "First".
     * <p>
     * Controls how cursor positioned after Unification if there are no errors
     * and at least one "incomplete" proof step: set cursor to First incomplete
     * proof step.
     */
    public static final String PROOF_ASST_INCOMPLETE_STEP_CURSOR_FIRST = "First";

    /**
     * PROOF_ASST_INCOMPLETE_STEP_CURSOR_ASIS = "AsIs".
     * <p>
     * Controls how cursor positioned after Unification if there are no errors
     * and at least one "incomplete" proof step: set cursor where it was when
     * Ctrl-U was presed (same step but on Ref field.)
     */
    public static final String PROOF_ASST_INCOMPLETE_STEP_CURSOR_ASIS = "AsIs";

    /**
     * PROOF_ASST_INCOMPLETE_STEP_CURSOR_DEFAULT = "AsIs".
     * <p>
     * Controls how cursor positioned after Unification if there are no errors
     * and at least one "incomplete" proof step.
     */
    public static final String PROOF_ASST_INCOMPLETE_STEP_CURSOR_DEFAULT = PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_ASIS; // "AsIs";

    /**
     * PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE
     * <p>
     * Used in ProofAsstGUI to display the choices.
     */
    public static final String[] PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE = {
            PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_ASIS,
            PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_FIRST,
            PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_LAST};

    /**
     * Caption of list of options for Incomplete Step Cursor positioning.
     * <p>
     * Used in ProofAsstGUI to display the choices.
     */
    public static final String PROOF_ASST_INCOMPLETE_STEP_CURSOR_OPTION_LIST = "Valid Incomplete Step Cursor Options: ";

    /**
     * Prompt for Set Incomplete Step Cursor option Action dialog.
     */
    public static final String PA_GUI_SET_INCOMPLETE_STEP_CURSOR_OPTION_PROMPT = "Enter Incomplete Step Cursor Option Number";

    // ----------------------------------------------------------
    // Constants for ProofAsstCursor.java
    // ----------------------------------------------------------

    /**
     * FIELD_ID_NONE = -1
     * <p>
     * Field Id not present.
     */
    public static final int FIELD_ID_NONE = -1;

    /**
     * FIELD_ID_REF = 1
     * <p>
     * Field Id for Ref sub-field of ProofStepStmt in ProofWorksheet.
     */
    public static final int FIELD_ID_REF = 1;

    // ----------------------------------------------------------
    // Constants for ProofUnifier.java
    // ----------------------------------------------------------

    /**
     * UNIFIER_NODE_STACK_SIZE = 1000
     * <p>
     * Stacks used for parse node unification and comparisons are held in fixed
     * length arrays for the duration, across multiple executions of the GUI.
     * The size is a function of the depth of the deepest parse tree and the
     * number of child nodes at each level on the longest path. Ergo, 1000
     * should be, five or ten times as big as needed. This could be made into a
     * RunParm should be, probably...
     */
    public static final int UNIFIER_NODE_STACK_SIZE = 1000;

    /**
     * UNIFIER_MAX_LOG_HYPS = 100
     * <p>
     * Fixed size arrays are used for "cleanup" of LogHyp substitutions (backing
     * out partial unification results). The necessary size of these arrays is
     * basically just the maximum number of LogHyp's for any Theorem.
     * <p>
     * Set.mm has one Theorem with 19 LogHyp's...
     * <p>
     * This could be made into a RunParm... should be, probably...
     */
    public static final int UNIFIER_MAX_LOG_HYPS = 100;

    public static final int UNIFIER_MAX_VAR_HYPS = 500;

    /**
     * DOT_STEP_CAPTION = ".Step"
     * <p>
     * This is appended to the Theorem label to create a simulated theorem label
     * when a proof step's proof is rechecked using VerifyProofs.java.
     */
    public static final String DOT_STEP_CAPTION = ".Step ";

    /**
     * MAX_UNIFY_ALTERNATES = 10
     * <p>
     * Alternate assertion Ref's are presented in an error message if the input
     * Ref fails to unify. However, to prevent an out-of-control situation a
     * "governor" is used. This is important now that the Proof Assistant
     * "Derive" Feature is in place.
     * <p>
     */
    public static final int MAX_UNIFY_ALTERNATES = 10;

    /**
     * DUMMY_VAR_PREFIX = $
     * <p>
     * Prefix string for "dummy" (temp) variables generated in the Proof
     * Assistant "Derive" feature for un-determined and out-of-scope variable
     * substitutions.
     * <p>
     * In normal practice dummy variables will be composed of the dummy variable
     * prefix string and a number -- e.g. "$1", "$2", etc.
     * <p>
     */
    public static final String DUMMY_VAR_PREFIX = "$";

    /**
     * ASSRT_LIST_FREESPACE_DEFAULT = 5
     * <p>
     * Padding percentage for ProofUnifier's Assrt lists 10 percent causes the
     * ArrayList to be allocated as 1100 if the size of the input list is 1000.
     * <p>
     * Padding is provided for efficiency with the new TheoremLoader so that new
     * theorems added to the unifySearchList don't force a complete recopy.
     * <p>
     */
    public static final int ASSRT_LIST_FREESPACE_DEFAULT = 5;

    /**
     * ASSRT_LIST_FREESPACE_MAX = 1000
     * <p>
     * Maximum padding percentage for ProofUnifier's Assrt lists.
     */
    public static final int ASSRT_LIST_FREESPACE_MAX = 1000;

    // ----------------------------------------------------------
    // Constants for ProofAsstBoss.java
    // ----------------------------------------------------------

    /**
     * PROOF_ASST_FONT_SIZE_MIN = 8.
     */
    public static final int PROOF_ASST_FONT_SIZE_MIN = 8;

    /**
     * PROOF_ASST_FONT_SIZE_MAX = 72.
     */
    public static final int PROOF_ASST_FONT_SIZE_MAX = 72;

    /**
     * PROOF_ASST_FONT_SIZE_CHG_AMT = 2.
     */
    public static final int PROOF_ASST_FONT_SIZE_CHG_AMT = 2;

    /**
     * PROOF_ASST_TEXT_COLUMNS_MIN = 40
     */
    public static final int PROOF_ASST_TEXT_COLUMNS_MIN = 40;

    /**
     * PROOF_ASST_TEXT_COLUMNS_MAX = 999
     */
    public static final int PROOF_ASST_TEXT_COLUMNS_MAX = 999;

    /**
     * PROOF_ASST_TEXT_ROWS_MIN = 2
     */
    public static final int PROOF_ASST_TEXT_ROWS_MIN = 2;

    /**
     * PROOF_ASST_TEXT_ROWS_MAX = 99
     */
    public static final int PROOF_ASST_TEXT_ROWS_MAX = 99;

    /**
     * PROOF_ASST_ERROR_MESSAGE_ROWS_MIN = 2
     */
    public static final int PROOF_ASST_ERROR_MESSAGE_ROWS_MIN = 2;

    /**
     * PROOF_ASST_ERROR_MESSAGE_ROWS_MAX = 99
     */
    public static final int PROOF_ASST_ERROR_MESSAGE_ROWS_MAX = 99;

    /**
     * PROOF_ASST_ERROR_MESSAGE_COLUMNS_MIN = 40
     */
    public static final int PROOF_ASST_ERROR_MESSAGE_COLUMNS_MIN = 40;

    /**
     * PROOF_ASST_ERROR_MESSAGE_COLUMNS_MAX = 999
     */
    public static final int PROOF_ASST_ERROR_MESSAGE_COLUMNS_MAX = 999;

    /**
     * PROOF_ASST_FORMULA_LEFT_COL_MIN
     */
    public static final int PROOF_ASST_FORMULA_LEFT_COL_MIN = 2;

    /**
     * PROOF_ASST_FORMULA_RIGHT_COL_MAX
     */
    public static final int PROOF_ASST_FORMULA_RIGHT_COL_MAX = Integer.MAX_VALUE;

    /**
     * PROOF_ASST_RPN_PROOF_LEFT_COL_MIN
     */
    public static final int PROOF_ASST_RPN_PROOF_LEFT_COL_MIN = 4;

    /**
     * PROOF_ASST_RPN_PROOF_RIGHT_COL_MAX
     */
    public static final int PROOF_ASST_RPN_PROOF_RIGHT_COL_MAX = Integer.MAX_VALUE;

    /**
     * ProofAsstDjVarsSoftErrors Ignore option
     */
    public static final String PROOF_ASST_DJ_VARS_SOFT_ERRORS_IGNORE = "Ignore";

    /**
     * ProofAsstDjVarsSoftErrors Report option
     */
    public static final String PROOF_ASST_DJ_VARS_SOFT_ERRORS_REPORT = "Report";

    /**
     * ProofAsstDjVarsSoftErrors GenerateNew option
     */
    public static final String PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_NEW = "GenerateNew";

    /**
     * ProofAsstDjVarsSoftErrors GenerateReplacements option
     */
    public static final String PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_REPLACEMENTS = "GenerateReplacements";

    /**
     * ProofAsstDjVarsSoftErrors GenerateDifferences option
     */
    public static final String PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_DIFFERENCES = "GenerateDifferences";

    /**
     * ProofAsstDjVarsSoftErrors Default value
     */
    public static final String PROOF_ASST_DJ_VARS_SOFT_ERRORS_DEFAULT = PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_REPLACEMENTS;

    public static final String[] PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE = {
            PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_IGNORE,
            PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_REPORT,
            PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_NEW,
            PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_REPLACEMENTS,
            PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_DIFFERENCES};

    /**
     * ProofAsstProofFormat Normal option
     */
    public static final String PROOF_ASST_PROOF_NORMAL = "Normal";

    /**
     * ProofAsstProofFormat Packed option
     */
    public static final String PROOF_ASST_PROOF_PACKED = "Packed";

    /**
     * ProofAsstProofFormat Compressed option
     */
    public static final String PROOF_ASST_PROOF_COMPRESSED = "Compressed";

    // ----------------------------------------------------------
    // Constants for ProofAsstPreferences.java
    // ----------------------------------------------------------

    /**
     * PROOF_ASST_FONT_SIZE_DEFAULT = 14
     */
    public static final int PROOF_ASST_FONT_SIZE_DEFAULT = 14;

    /**
     * PROOF_ASST_FONT_FAMILY_DEFAULT = Monospaced
     */
    public static final String PROOF_ASST_FONT_FAMILY_DEFAULT = "Monospaced";

    /**
     * PROOF_ASST_FONT_BOLD_DEFAULT = yes
     */
    public static final boolean PROOF_ASST_FONT_BOLD_DEFAULT = true;

    /**
     * PROOF_ASST_ERROR_MESSAGE_ROWS_DEFAULT = 4
     */
    public static final int PROOF_ASST_ERROR_MESSAGE_ROWS_DEFAULT = 4;

    /**
     * PROOF_ASST_ERROR_MESSAGE_COLUMNS_DEFAULT = 80
     */
    public static final int PROOF_ASST_ERROR_MESSAGE_COLUMNS_DEFAULT = 80;

    /**
     * PROOF_ASST_MAXIMIZED_DEFAULT = no
     */
    public static final boolean PROOF_ASST_MAXIMIZED_DEFAULT = false;

    /**
     * PROOF_ASST_TEXT_AT_TOP_DEFAULT = yes
     */
    public static final boolean PROOF_ASST_TEXT_AT_TOP_DEFAULT = true;

    /**
     * PROOF_ASST_FORMULA_LEFT_COL_DEFAULT
     */
    public static final int PROOF_ASST_FORMULA_LEFT_COL_DEFAULT = 20;

    /**
     * PROOF_ASST_FORMULA_RIGHT_COL_DEFAULT
     */
    public static final int PROOF_ASST_FORMULA_RIGHT_COL_DEFAULT = 79;

    /**
     * PROOF_ASST_RPN_PROOF_LEFT_COL_DEFAULT
     */
    public static final int PROOF_ASST_RPN_PROOF_LEFT_COL_DEFAULT = 5;

    /**
     * PROOF_ASST_RPN_PROOF_RIGHT_COL_DEFAULT
     */
    public static final int PROOF_ASST_RPN_PROOF_RIGHT_COL_DEFAULT = 70;

    public static final String PROOF_ASST_SOFT_DJ_ERROR_OPTION_LIST = "Valid Soft Dj Vars Error Options: ";

    public static final String PROOF_ASST_COMPRESSION_LIST = "Valid Proof Compression Options:\n\n"
        + "1 - Normal\n2 - Packed\n3 - Compressed (Default)\n\nEnter Proof Compression Number";

    public static final String PROOF_ASST_FONT_FAMILY_LIST = "Valid font family names defined in your system: ";

    public static final int FONT_LIST_MAX_LINES = 18;
    public static final int FONT_LIST_STARTING_LINE_LENGTH = 70;

    /**
     * Number of columns in window. Used for calculating how many spaces must be
     * output to effect a (visual) line break without actually outputting a
     * NewLine.
     * <p>
     */
    public static final int PROOF_ASST_TEXT_COLUMNS_DEFAULT = 80;

    /**
     * LineWrap mode default, set to off (false).
     */
    public static final boolean PROOF_ASST_LINE_WRAP_DEFAULT = false;

    /**
     * Default Option Value 4 "un-unified" for ProofAsstExportToFile RunParm and
     * Option Value 3 for ProofAsstBatchTest (when no input file specified).
     * <p>
     * Means that Ref (statement labels) should NOT be included on exported
     * derivation proof steps. This is the default.
     */
    public static final boolean PROOF_ASST_EXPORT_FORMAT_UNIFIED_DEFAULT = false;

    /**
     * Default Option Value 5 "NotRandomized" for ProofAsstExportToFile RunParm
     * and Option Value 4 for ProofAsstBatchTest (when no input file specified).
     * <p>
     * Means that Ref (statement labels) should NOT be included on exported
     * derivation proof steps. This is the default.
     */
    public static final boolean PROOF_ASST_EXPORT_HYPS_RANDOMIZED_DEFAULT = false;

    /**
     * Default Option Value 6 "NoPrint" for ProofAsstExportToFile RunParm and
     * Option Value 5 for ProofAsstBatchTest (when no input file specified).
     * <p>
     * Means that an extra print copy of each Proof Worksheet should not be sent
     * to the SystemOutputFile (or System.out).
     */
    public static final boolean PROOF_ASST_PRINT_DEFAULT = false;

    /**
     * Default Option Value 7 "NoDeriveFormulas" for ProofAsstExportToFile
     * RunParm and Option Value 6 for ProofAsstBatchTest.
     */
    public static final boolean PROOF_ASST_EXPORT_DERIVE_FORMULAS_DEFAULT = false;

    /**
     * Default Option Value 7 "NoCompareDJs" for for ProofAsstBatchTest.
     */
    public static final boolean PROOF_ASST_IMPORT_COMPARE_DJS_DEFAULT = false;

    /**
     * Default Option Value 8 "NoCompareDJs" for for ProofAsstBatchTest.
     */
    public static final boolean PROOF_ASST_IMPORT_UPDATE_DJS_DEFAULT = false;

    /**
     * Default Option Value 9 "NoAsciiRetest" for for ProofAsstBatchTest.
     */
    public static final boolean PROOF_ASST_ASCII_RETEST_DEFAULT = false;

    /**
     * RECHECK_PROOF_ASST_USING_PROOF_VERIFIER_DEFAULT
     */
    public static final boolean RECHECK_PROOF_ASST_USING_PROOF_VERIFIER_DEFAULT = false;

    /**
     * Default value for StepSelectorMaxResults RunParm.
     * <p>
     * Specifies the maximum number of results to be displayed by the
     * StepSelectorDialog.
     * <p>
     * STEP_SELECTOR_MAX_RESULTS_DEFAULT = 50
     */
    public static final int STEP_SELECTOR_MAX_RESULTS_DEFAULT = 50;

    /**
     * Maximum value for StepSelectorMaxResults RunParm.
     * <p>
     * Specifies the maximum number of results to be displayed by the
     * StepSelectorDialog.
     * <p>
     * STEP_SELECTOR_MAX_RESULTS_MAXIMUM = 9999
     */
    public static final int STEP_SELECTOR_MAX_RESULTS_MAXIMUM = 9999;

    /**
     * Default value for StepSelectorShowSubstitutions RunParm.
     * <p>
     * Specifies whether or not substitutions resulting from unification are
     * made into assertion formulas shown on the StepSelectorDialog.
     * <p>
     * STEP_SELECTOR_SHOW_SUBSTITUTIONS_DEFAULT = true
     */
    public static final boolean STEP_SELECTOR_SHOW_SUBSTITUTIONS_DEFAULT = true;

    /**
     * Default value for StepSelectorDialogPaneWidth RunParm.
     * <p>
     * STEP_SELECTOR_DIALOG_PANE_WIDTH_DEFAULT = 720
     */
    public static final int STEP_SELECTOR_DIALOG_PANE_WIDTH_DEFAULT = 720;

    /**
     * Minimum value for StepSelectorDialogPaneWidth RunParm.
     * <p>
     * STEP_SELECTOR_DIALOG_PANE_WIDTH_MIN = 100
     */
    public static final int STEP_SELECTOR_DIALOG_PANE_WIDTH_MIN = 100;

    /**
     * Maximum value for StepSelectorDialogPaneWidth RunParm.
     * <p>
     * STEP_SELECTOR_DIALOG_PANE_WIDTH_MAX = 9999
     */
    public static final int STEP_SELECTOR_DIALOG_PANE_WIDTH_MAX = 9999;

    /**
     * Default value for StepSelectorDialogPaneHeight RunParm.
     * <p>
     * STEP_SELECTOR_DIALOG_PANE_HEIGHT_DEFAULT = 720
     */
    public static final int STEP_SELECTOR_DIALOG_PANE_HEIGHT_DEFAULT = 440;

    /**
     * Minimum value for StepSelectorDialogPaneHeight RunParm.
     * <p>
     * STEP_SELECTOR_DIALOG_PANE_HEIGHT_MIN = 100
     */
    public static final int STEP_SELECTOR_DIALOG_PANE_HEIGHT_MIN = 100;

    /**
     * Maximum value for StepSelectorDialogPaneHeight RunParm.
     * <p>
     * STEP_SELECTOR_DIALOG_PANE_HEIGHT_MAX = 9999
     */
    public static final int STEP_SELECTOR_DIALOG_PANE_HEIGHT_MAX = 9999;

    /**
     * Default value for ProofAsstOutputCursorInstrumentation RunParm.
     * <p>
     * Controls whether or not "instrumentation" info messages are output by the
     * ProofAsst for regression test purposes.
     * <p>
     * OUTPUT_CURSOR_INSTRUMENTATION_DEFAULT = false
     */
    public static final boolean OUTPUT_CURSOR_INSTRUMENTATION_DEFAULT = false;

    /**
     * Default value for ProofAsstAutoReformat RunParm.
     * <p>
     * Controls whether or not proof step formulas are automatically reformatted
     * after Work Variables are resolved.
     * <p>
     * AUTO_REFORMAT_DEFAULT = true
     */
    public static final boolean AUTO_REFORMAT_DEFAULT = true;

    /**
     * Default value for UndoRedoEnabled RunParm.
     * <p>
     * Controls whether or not the Proof Assistant GUI provides Undo/Redo
     * support.
     * <p>
     * Normally this is turned on, but if desired, say for performance reasons,
     * the user can disable Undo/Redo at start-up time via RunParm.
     * <p>
     * UNDO_REDO_ENABLED_DEFAULT = true
     */
    public static final boolean UNDO_REDO_ENABLED_DEFAULT = true;

    /**
     * Default Foreground Color.
     * <p>
     * DEFAULT_FOREGROUND_COLOR = Color.BLACK
     */
    public static final Color DEFAULT_FOREGROUND_COLOR = Color.BLACK;

    /**
     * Default Background Color.
     * <p>
     * DEFAULT_BACKGROUND_COLOR = Color.WHITE
     */
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    // ----------------------------------------------------------
    // Constants for AuxFrameGUI.java
    // ----------------------------------------------------------

    /**
     * AUX_FRAME_NBR_ROWS_DEFAULT = 25
     */
    public static final int AUX_FRAME_NBR_ROWS_DEFAULT = 25;

    /**
     * AUX_FRAME_NBR_COLUMNS_DEFAULT = 80
     */
    public static final int AUX_FRAME_NBR_COLUMNS_DEFAULT = 80;

    /**
     * AUX_FRAME_TITLE_DEFAULT
     */
    public static final String AUX_FRAME_TITLE_DEFAULT = "AuxFrameGUI default title";

    /**
     * AUX_FRAME_TEXT_DEFAULT
     */
    public static final String AUX_FRAME_TEXT_DEFAULT = "AuxFrameGUI default text";

    /**
     * AUX_FRAME_FONT_FAMILY = Monospaced
     */
    public static final String AUX_FRAME_FONT_FAMILY = "Monospaced";

    // ----------------------------------------------------------
    // Constants for UnificationErrorsGUI.java
    // ----------------------------------------------------------

    public static final boolean REQUEST_MESSAGES_GUI_ENABLED = false;

    /**
     * REQUEST_MESSAGES_GUI_TITLE_DEFAULT
     */
    public static final String REQUEST_MESSAGES_GUI_TITLE_DEFAULT = "Request Messages";

    /**
     * REQUEST_MESSAGES_GUI_TEXT_DEFAULT
     */
    public static final String REQUEST_MESSAGES_GUI_TEXT_DEFAULT = "No errors!";

    // ----------------------------------------------------------
    // Constants for ProofAsstGUI.java
    // ----------------------------------------------------------

    /**
     * PROOF_ASST_GUI_STARTUP_MSG
     */
    public static final String PROOF_ASST_GUI_STARTUP_MSG = "Hi! I am mmj2 Release R20121225 as of 20-Sep-2012 20:54.\n"
        + "Send mmj2 support? Paypal to siskiyousis at gmail.com\n"
        + "Need mmj2 help? Email x178g243 at yahoo.com.";

    /**
     * PROOF_TEXT_FONT_FAMILY = Monospaced
     */
    public static final String PROOF_TEXT_FONT_FAMILY = "Monospaced";

    /**
     * PROOF_ASST_TEXT_ROWS_DEFAULT = 21
     */
    public static final int PROOF_ASST_TEXT_ROWS_DEFAULT = 21;

    /**
     * This is set to 1 to effectively disable the tab character. The reason is
     * that the GUI tries to keep track of column numbers and tab characters
     * complicate things unacceptably...
     */
    public static final int PROOF_TEXT_TAB_LENGTH = 1;

    /**
     * SAMPLE_PROOF_LABEL
     */
    public static final String SAMPLE_PROOF_LABEL = "syllogism";

    /**
     * SAMPLE_PROOF_TEXT
     */
    public static final String SAMPLE_PROOF_TEXT = "$( <MM> <PROOF_ASST> THEOREM="
        + PaConstants.SAMPLE_PROOF_LABEL
        + " LOC_AFTER=\n"
        + "\n"
        + "h1::           |- ( ph -> ps ) \n"
        + "h2::           |- ( ps -> ch ) \n"
        + "3:2:           |- ( ph -> ( ps -> ch ) ) \n"
        + "4:3:           |- ( ( ph -> ps ) -> ( ph -> ch ) ) \n"
        + "qed:1,4:       |- ( ph -> ch ) \n" + "\n" + "$)\n";

    /**
     * PROOF_ASST_FRAME_TITLE
     */
    public static final String PROOF_ASST_FRAME_TITLE = "ProofAsstGUI";

//  /**
//   * This is the initial filename value on the File Chooser dialog.
//   */
//  public static final String PA_GUI_FILE_CHOOSER_DEFAULT
//                                = "tempProofLabel.mmp";

    /**
     * Description displayed on File Chooser dialog screen.
     */
    public static final String PA_GUI_FILE_CHOOSER_DESCRIPTION = "Text and mmj2 Proof Asst files";

    /**
     * ProofAsstGUI File Chooser valid file name suffix ".TXT".
     */
    public static final String PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT = ".TXT";

    /**
     * ProofAsstGUI File Chooser valid file name suffix ".txt"
     */
    public static final String PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT2 = ".txt";

    /**
     * ProofAsstGUI File Chooser valid file name suffix ".MMP".
     */
    public static final String PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP = ".MMP";

    /**
     * ProofAsstGUI File Chooser valid file name suffix ".mmp"
     */
    public static final String PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP2 = ".mmp";

    /**
     * ProofAsstGUI Default file name suffix
     */
    public static final String PA_GUI_DEFAULT_FILE_NAME_SUFFIX = PaConstants.PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP2; // .txt

    /**
     * ProofAsstGUI Save before window closes question
     */
    public static final String PA_GUI_SAVE_BEFORE_CLOSE_QUESTION = "Save changes before Window Closes?";

    /**
     * ProofAsstGUI File Menu Title
     */
    public static final String PA_GUI_FILE_MENU_TITLE = "File";

    /**
     * ProofAsstGUI File Menu New Item Text
     */
    public static final String PA_GUI_FILE_MENU_NEW_ITEM_TEXT = "New Proof";

    /**
     * ProofAsstGUI File Menu New Next Item Text
     */
    public static final String PA_GUI_FILE_MENU_NEW_NEXT_ITEM_TEXT = "New-Next Proof";

    /**
     * ProofAsstGUI File Menu Open Item Text
     */
    public static final String PA_GUI_FILE_MENU_OPEN_ITEM_TEXT = "Open Proof File";

    /**
     * ProofAsstGUI File Menu Get Proof Item Text
     */
    public static final String PA_GUI_FILE_MENU_GET_PROOF_ITEM_TEXT = "Get Proof";

    /**
     * ProofAsstGUI File Menu Get Forward Proof Item Text
     */
    public static final String PA_GUI_FILE_MENU_GET_FWD_PROOF_ITEM_TEXT = "Forward-Get Proof";

    /**
     * ProofAsstGUI File Menu Get Backward Proof Item Text
     */
    public static final String PA_GUI_FILE_MENU_GET_BWD_PROOF_ITEM_TEXT = "Backward-Get Proof";

    /**
     * ProofAsstGUI File Menu Close Item Text
     */
    public static final String PA_GUI_FILE_MENU_CLOSE_ITEM_TEXT = "Close Proof File";

    /**
     * ProofAsstGUI File Menu Save Item Text
     */
    public static final String PA_GUI_FILE_MENU_SAVE_ITEM_TEXT = "Save Proof File";

    /**
     * ProofAsstGUI File Menu Save As Item Text
     */
    public static final String PA_GUI_FILE_MENU_SAVE_AS_ITEM_TEXT = "SaveAs...";

    /**
     * ProofAsstGUI File Menu Export Via GMFF Item Text
     */
    public static final String PA_GUI_FILE_MENU_EXPORT_VIA_GMFF_ITEM_TEXT = "Export Via GMFF";

    /**
     * ProofAsstGUI File Menu Exit Item Text
     */
    public static final String PA_GUI_FILE_MENU_EXIT_ITEM_TEXT = "Exit/Quit";

    /**
     * ProofAsstGUI Edit Menu Title
     */
    public static final String PA_GUI_EDIT_MENU_TITLE = "Edit";

    /**
     * ProofAsstGUI Edit Menu Undo Item Text
     */
    public static final String PA_GUI_EDIT_MENU_UNDO_ITEM_TEXT = "Undo";

    /**
     * ProofAsstGUI Edit Menu Redo Item Text
     */
    public static final String PA_GUI_EDIT_MENU_REDO_ITEM_TEXT = "Redo";

    /**
     * ProofAsstGUI Edit Menu Cut Item Text
     */
    public static final String PA_GUI_EDIT_MENU_CUT_ITEM_TEXT = "Cut";

    /**
     * ProofAsstGUI Edit Menu Copy Item Text
     */
    public static final String PA_GUI_EDIT_MENU_COPY_ITEM_TEXT = "Copy";

    /**
     * ProofAsstGUI Edit Menu Paste Item Text
     */
    public static final String PA_GUI_EDIT_MENU_PASTE_ITEM_TEXT = "Paste";

    /**
     * ProofAsstGUI Edit Menu Set Foreground Color Item Text
     */
    public static final String PA_GUI_EDIT_MENU_SET_FOREGROUND_ITEM_TEXT = "Set Foreground Color";

    /**
     * ProofAsstGUI Edit Menu Set Background Color Item Text
     */
    public static final String PA_GUI_EDIT_MENU_SET_BACKGROUND_ITEM_TEXT = "Set Background Color";

    /**
     * ProofAsstGUI Literal Used in Color Chooser Dialog Title
     */
    public static final String COLOR_CHOOSE_TITLE_2 = " -- presently = ";

    /**
     * ProofAsstGUI Literal Used in Color Chooser Dialog Title
     */
    public static final String COLOR_CHOOSE_TITLE_SEPARATOR = ",";

    /**
     * ProofAsstGUI Edit Menu Set Format Nbr Item Text
     */
    public static final String PA_GUI_EDIT_MENU_SET_FORMAT_NBR_ITEM_TEXT = "Set Format Number";

    /**
     * ProofAsstGUI Edit Menu Set Indent Item Text
     */
    public static final String PA_GUI_EDIT_MENU_SET_INDENT_ITEM_TEXT = "Set Indent";

    /**
     * ProofAsstGUI Edit Menu Set Incomplete Step Cursor Item Text
     */
    public static final String PA_GUI_EDIT_MENU_SET_INCOMPLETE_STEP_CURSOR_ITEM_TEXT = "Set Incomplete Step Cursor";

    /**
     * ProofAsstGUI Edit Menu Set Soft Dj Error Item Text
     */
    public static final String PA_GUI_EDIT_MENU_SET_SOFT_DJ_ERROR_ITEM_TEXT = "Set Soft Dj Vars Error Handling";

    /**
     * ProofAsstGUI Edit Menu Set Font Family Item Text
     */
    public static final String PA_GUI_EDIT_MENU_SET_FONT_FAMILY_ITEM_TEXT = "Set Font Family";

    /**
     * ProofAsstGUI Edit Menu Font Style Bold Item Text
     */
    public static final String PA_GUI_EDIT_MENU_FONT_STYLE_BOLD_ITEM_TEXT = "Set Font Style BOLD";

    /**
     * ProofAsstGUI Edit Menu Font Style Plain Item Text
     */
    public static final String PA_GUI_EDIT_MENU_FONT_STYLE_PLAIN_ITEM_TEXT = "Set Font Style PLAIN";

    /**
     * ProofAsstGUI Edit Menu Increase Font Size Item Text
     */
    public static final String PA_GUI_EDIT_MENU_INC_FONT_ITEM_TEXT = "Larger Font Size";

    /**
     * ProofAsstGUI Edit Menu Decrease Font Size Item Text
     */
    public static final String PA_GUI_EDIT_MENU_DEC_FONT_ITEM_TEXT = "Smaller Font Size";

    /**
     * ProofAsstGUI Edit Menu Reformat Item Text
     */
    public static final String PA_GUI_EDIT_MENU_REFORMAT_ITEM_TEXT = "Reformat Proof";

    /**
     * ProofAsstGUI Popup Menu Reformat Step Text
     */
    public static final String PA_GUI_POPUP_MENU_REFORMAT_STEP_TEXT = "Reformat Step";

    /**
     * ProofAsstGUI Edit Menu Reformat Swap Alt Item Text
     */
    public static final String PA_GUI_EDIT_MENU_REFORMAT_SWAP_ALT_ITEM_TEXT = "Reformat Proof: Swap Alt";

    /**
     * ProofAsstGUI Popup Menu Reformat Swap Alt Step Text
     */
    public static final String PA_GUI_POPUP_MENU_REFORMAT_SWAP_ALT_STEP_TEXT = "Reformat Step: Swap Alt";

    /**
     * ProofAsstGUI Cancel Menu Title
     */
    public static final String PA_GUI_CANCEL_MENU_TITLE = "Cancel";

    /**
     * ProofAsstGUI Unify Menu Kill Unification Item Text
     */
    public static final String PA_GUI_CANCEL_MENU_KILL_ITEM_TEXT = "Cancel (Looping? Kill it!!!)";

    /**
     * ProofAsstGUI Unify Menu Title
     */
    public static final String PA_GUI_UNIFY_MENU_TITLE = "Unify";

    /**
     * ProofAsstGUI Unify Menu Start Unification Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_START_ITEM_TEXT = "Unify (check proof)";

    /**
     * ProofAsstGUI Unify Menu Start Unification with Renum Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_START_UR_ITEM_TEXT = "Unify+Renumber";

    /**
     * ProofAsstGUI Unify Menu Unify with Rederive Formulas Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_REDERIVE_ITEM_TEXT = "Unify+Erase and Rederive Formulas";

    /**
     * ProofAsstGUI Unify Menu Start Unification without Converting Work Vars
     * Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_NO_WV_ITEM_TEXT = "Unify+Don't Convert WorkVars";

    /**
     * ProofAsstGUI Unify Menu Start Unification with Rederive Formulas without
     * Converting Work Vars Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_ERASE_NO_WV_ITEM_TEXT = "Unify+Erase+Don't Convert";

    /**
     * ProofAsstGUI Unify Menu Step Selector Search Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_STEP_SELECTOR_SEARCH_ITEM_TEXT = "Step Selector Search";

    /**
     * ProofAsstGUI Unify Menu Reshow Step Selector Dialog Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_RESHOW_STEP_SELECTOR_DIALOG_ITEM_TEXT = "Reshow Step Selector Dialog";

    /**
     * ProofAsstGUI Unify Menu Set Step Selector Max Results Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_SET_MAX_RESULTS_ITEM_TEXT = "Set Step Selector Max Results";

    /**
     * ProofAsstGUI Unify Menu Set Step Selector Show Substitutions Item Text
     */
    public static final String PA_GUI_UNIFY_MENU_SET_SHOW_SUBST_ITEM_TEXT = "Set Step Selector Show Substitutions";

    /**
     * Prompt for SetStepSelectorMaxResults Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_MAX_RESULTS_OPTION_PROMPT = "Enter Step Selector Max Results Number (1 thru 9999)";

    /**
     * Prompt for SetStepSelectorShowSubstitutions Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_SHOW_SUBST_OPTION_PROMPT = "Enter Step Select Show Substitutions option (true or false)";

    public static final String PA_GUI_SEARCH_MENU_TITLE = "Search";

    public static final String SEARCH_OPTIONS_ITEM_TEXT = "Search Options";

    public static final String GENERAL_SEARCH_ITEM_TEXT = "General Search";

    public static final String STEP_SEARCH_ITEM_TEXT = "Step Search";

    public static final String RESHOW_SEARCH_RESULTS_ITEM_TEXT = "Reshow Search Results";

    // ========== new TheoremLoader stuff ===========

    /**
     * ProofAsstGUI Theorem Loader Menu Title
     */
    public static final String PA_GUI_TL_MENU_TITLE = "TL";

    /**
     * ProofAsstGUI TL Menu DjVars Option Item Text
     */
    public static final String PA_GUI_TL_MENU_DJ_VARS_OPTION_TEXT = "Set Theorem Loader Dj Vars Option";

    /**
     * ProofAsstGUI TL Menu MMT Folder Item Text
     */
    public static final String PA_GUI_TL_MENU_MMT_FOLDER_TEXT = "Set Theorem Loader MMT Folder";

    /**
     * ProofAsstGUI TL Menu Audit Messages Item Text
     */
    public static final String PA_GUI_TL_MENU_AUDIT_MESSAGES_TEXT = "Set Theorem Loader Audit Messages";

    /**
     * ProofAsstGUI TL Menu Store MM Formulas AsIs Item Text
     */
    public static final String PA_GUI_TL_MENU_STORE_FORMULAS_AS_IS_TEXT = "Set Store MM Formulas AsIs";

    /**
     * ProofAsstGUI TL Menu Store MM Indent Amt Item Text
     */
    public static final String PA_GUI_TL_MENU_STORE_MM_INDENT_AMT_TEXT = "Set Store MM Indent Amt";

    /**
     * ProofAsstGUI TL Menu Proof Compression Item Text
     */
    public static final String PA_GUI_TL_MENU_COMPRESSION_TEXT = "Set Proof Compression";

    /**
     * ProofAsstGUI TL Menu Load Theorems From MMT Folder Item Text
     */
    public static final String PA_GUI_TL_MENU_LOAD_THEOREMS_FROM_MMT_FOLDER_TEXT = "Load Theorems From MMT Folder";

    /**
     * ProofAsstGUI TL Menu Extract Theorem To MMT Folder Item Text
     */
    public static final String PA_GUI_TL_MENU_EXTRACT_THEOREM_TO_MMT_FOLDER_TEXT = "Extract Theorem To MMT Folder";

    /**
     * ProofAsstGUI TL Menu Unify + Store In MMT Folder Item Text
     */
    public static final String PA_GUI_TL_MENU_UNIFY_PLUS_STORE_IN_MMT_FOLDER_TEXT = "Unify + Store In MMT Folder";

    /**
     * ProofAsstGUI TL Menu Unify + Store In LogSys And MMT Folder Item Text
     */
    public static final String PA_GUI_TL_MENU_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER_TEXT = "Unify + Store In LogSys and MMT Folder";

    /**
     * ProofAsstGUI TL Menu Verify All Proofs Item Text
     */
    public static final String PA_GUI_TL_MENU_VERIFY_ALL_PROOFS_TEXT = "Verify All Proofs";

    /**
     * ProofAsstGUI TL Menu Store MM Right Col Item Text
     */
    public static final String PA_GUI_TL_MENU_STORE_MM_RIGHT_COL_TEXT = "Set Store MM Right Col";

    /**
     * Prompt for Set Theorem Loader Audit Messages Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_TL_AUDIT_MESSAGES_OPTION_PROMPT = "Enter Theorem Loader Audit Messages option (True or False)";

    /**
     * Prompt for Set Theorem Loader Store Formulas AsIs Menu Item Action
     * dialog.
     */
    public static final String PA_GUI_SET_TL_STORE_FORMULAS_AS_IS_OPTION_PROMPT = "Enter Theorem Loader Store Formulas AsIs"
        + " option (True or False)";

    /**
     * Prompt for Set Theorem Loader Store MM Right Col Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_TL_STORE_MM_RIGHT_COL_OPTION_PROMPT = "Enter Theorem Loader Store MM Right (margin) Col"
        + " (70 thru 9999)";

    /**
     * Prompt for Set Theorem Loader Store MM Indent Amt Menu Item Action
     * dialog.
     */
    public static final String PA_GUI_SET_TL_STORE_MM_INDENT_AMT_OPTION_PROMPT = "Enter Theorem Loader Store MM Indent Amt"
        + " (0 thru 9)";

    /**
     * Prompt for Set Theorem Loader Dj Vars Option Item Action dialog.
     */
    public static final String PA_GUI_SET_TL_DJ_VARS_OPTION_PROMPT = "Enter Theorem Loader DjVars Option:\n"
        + "Merge, Replace or NoUpdate";

    /**
     * Prompt for Set Theorem Loader MMT Folder Item Action dialog.
     */
    public static final String PA_GUI_SET_TL_MMT_FOLDER_OPTION_PROMPT = "Select MMT Folder";

    /**
     * Prompt for Set Theorem Loader MMT Folder Item Action dialog.
     */
    public static final String PA_GUI_SET_TL_MMT_FOLDER_OPTION_PROMPT_2 = "\nTry another?";

    // ========== new GMFF stuff ===========

    /**
     * ProofAsstGUI GMFF Menu Title
     */
    public static final String PA_GUI_GMFF_MENU_TITLE = "GMFF";

    // ========== various ===========

    /**
     * ProofAsstGUI Help Menu Title
     */
    public static final String PA_GUI_HELP_MENU_TITLE = "Help";

    /**
     * ProofAsstGUI Help Menu General Help Information Item Text
     */
    public static final String PA_GUI_HELP_MENU_GENERAL_ITEM_TEXT = "General Help Info";

    /**
     * ProofAsstGUI Help About Item Text
     */
    public static final String PA_GUI_HELP_ABOUT_ITEM_TEXT = "About mmj2";

    /**
     * ProofAsstGUI Exit Action Before Save
     */
    public static final String PA_GUI_ACTION_BEFORE_SAVE_EXIT = "Exit";

    /**
     * ProofAsstGUI Close Action Before Save
     */
    public static final String PA_GUI_ACTION_BEFORE_SAVE_CLOSE = "Close";

    /**
     * ProofAsstGUI New Action Before Save
     */
    public static final String PA_GUI_ACTION_BEFORE_SAVE_NEW = "New";

    /**
     * ProofAsstGUI Open Action Before Save
     */
    public static final String PA_GUI_ACTION_BEFORE_SAVE_OPEN = "Open";

    /**
     * ProofAsstGUI New Theorem Label Prompt
     */
    public static final String PA_GUI_NEW_THEOREM_LABEL_PROMPT = "Theorem label?";

    /**
     * ProofAsstGUI Get Proof Theorem Label Prompt
     */
    public static final String PA_GUI_GET_THEOREM_LABEL_PROMPT = "Theorem label?";

    /**
     * ProofAsstGUI Get corrected Theorem Label Prompt.
     */
    public static final String PA_GUI_GET_THEOREM_LABEL_PROMPT_2 = "Label %s invalid: not found or not a Theorem. Theorem label?";

    /**
     * ProofAsstGUI Get Yes/No/Cancel Answer Dialog Title
     */
    public static final String PA_GUI_YES_NO_CANCEL_TITLE = "getYesNoCancelAnswer()";

    /**
     * ProofAsstGUI Get Yes/No Answer Dialog Title
     */
    public static final String PA_GUI_YES_NO_TITLE = "getYesNoAnswer()";

    /**
     * ProofAsstGUI Save New Proof Text File Dialog Title
     */
    public static final String PA_GUI_SAVE_NEW_PROOF_TEXT_TITLE = "SaveNewProofTextFile()";

    /**
     * ProofAsstGUI Save Old Proof Text File Dialog Title
     */
    public static final String PA_GUI_SAVE_OLD_PROOF_TEXT_TITLE = "SaveOldProofTextFile()";

    /**
     * Default Theorem Label For Error Messages
     */
    public static final String PA_UNKNOWN_THEOREM_LABEL = "UnknownTheoremLabel";

    /**
     * Prompt for SetFormatNbr Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_FORMAT_NBR_PROMPT = "Enter Format Number";

    /**
     * Prompt for SetIndent Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_INDENT_PROMPT = "Enter Indent amount, 0 through ";

    /**
     * Prompt for Set Font Family Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_FONT_FAMILY_PROMPT = "Enter Font Family Name";

    /**
     * Prompt for Set Dj Vars Error Option Menu Item Action dialog.
     */
    public static final String PA_GUI_SET_SOFT_DJ_ERROR_OPTION_PROMPT = "Enter Soft Dj Vars Error Option Number";

    /**
     * (With word wrap 'on' in JTextArea, newlines are ignored, so a spacer line
     * is inserted between messages to force separation.)
     */
    public static final String ERROR_TEXT_SPACER_LINE = " --------------------------------------------------------- ";

    // ----------------------------------------------------------
    // Constants for Help About
    // ----------------------------------------------------------

    /**
     * Proof Assistant GUI Help About Title
     */
    public static final String HELP_ABOUT_TITLE = "About mmj2";

    /**
     * Proof Assistant GUI Help About Part 1
     */
    public static final String HELP_ABOUT_TEXT = ""
        + "Copyright (C) 2005 thru 2011 MEL O'CAT via X178G243 (at) yahoo (dot) com \n"
        + "License terms: GNU General Public License Version 2 or any later version.\n"
        + "                                                                         \n"
        + "Note: The following copyright is included because ProofAsstGUI.java      \n"
        + "has several snippets of code that are very similar, if not identical     \n"
        + "to snippets of code in the Java Tutorial.                                \n"
        + "                                                                         \n"
        + "Copyright� 1995-2004 Sun Microsystems, Inc. All Rights Reserved.         \n"
        + "Redistribution and use in source and binary forms, with or without       \n"
        + "modification, are permitted provided that the following conditions are   \n"
        + "met:                                                                     \n"
        + "* Redistribution of source code must retain the above copyright          \n"
        + "  notice, this list of conditions and the following disclaimer.          \n"
        + "* Redistribution in binary form must reproduce the above copyright       \n"
        + "  notice, this list of conditions and the following disclaimer in the    \n"
        + "  documentation and/or other materials provided with the distribution.   \n"
        + "(See SunJavaTutorialLicense.html in the mmj2 distribution for the        \n"
        + "for the disclaimer.)                                                     \n\n"
        + "Garbage Collection Run (just now) Memory Totals follow:\n"
        + " Max Memory   = %s\n Free Memory  = %s\n Total Memory = %s\n";

    // ----------------------------------------------------------
    // Constants for HelpGeneralInfoGUI.java
    // ----------------------------------------------------------

    /**
     * Proof Assistant GUI General Help Information Frame title
     */
    public static final String GENERAL_HELP_FRAME_TITLE = "Proof Assistant Help: General Information,"
        + " Release 20111101 as of 15-Oct-2011";
    /**
     * Proof Assistant GUI General Help Information text
     */
    public static final String GENERAL_HELP_INFO_TEXT = ""
        + "The mmj2 Proof Assistant provides an easy-to-use system for creating\n"
        + "Metamath proofs, but it does not provide all of the features of the\n"
        + "Metamath.exe Proof Assistant. Specifically:\n\n"
        + "* mmj2 does not update Metamath .mm databases.\n\n"
        + "* mmj2 cannot import incomplete or invalid proofs from a Metamath database. \n\n"
        + "* mmj2 does not provide a text Search facility\n\n"
        + "Also, please do not be disappointed. In spite of the point-and-click user-\n"
        + "friendliness, the fact that the mmj2 Proof Assistant uses a GUI interface\n"
        + "does not eliminate the requirement that the user learn logic and math. Nor\n"
        + "does it eliminate the need for deep thought, hard work or perseverance. It\n"
        + "may be that you will need to work out your proofs by hand before entering\n"
        + "them into the system.\n\n"
        + "----------------------------------------------------------------------------\n\n"
        + "Here are a few important facts and concepts about the mmj2 Proof Assistant\n"
        + "GUI program:\n\n"
        + "* A 'proof' on the Proof Assistant GUI screen is just a big text area.\n\n"
        + "* The GUI program itself is just a rudimentary text editor that knows\n"
        + "nothing and retains no memory of previous interactions, except for the\n"
        + "current location in the database, which is used for browsing purposes.\n\n"
        + "* The text on the screen is divided into two window 'panes'. By default, the\n"
        + "upper pane is the Proof Text Area, which is called a 'Proof Worksheet' when\n"
        + "properly formatted according to the mmj2 rules. The lower window pane shows\n"
        + "error and informational messages, and its contents are duplicated in a\n"
        + "separate 'Request Messages' window (use Alt-Tab to switch back and forth.)\n\n"
        + "* The contents of the Proof Text Area can be saved to, or retrieved from an\n"
        + "ASCII text file.\n\n"
        + "* The Theorem Loader feature (menu item 'TL') can be used to update the\n"
        + "Metamath data presently loaded in the mmj2 Logical System, and to convert a\n"
        + "Proof Worksheet to a Metamath formatted file, which it stores in your\n"
        + "designated 'MMT Folder' with file type '.mmt'. A theorem in the MMT Folder\n"
        + "in a '.mmt' type file can be loaded into the Logical System even if its\n"
        + "proof is incomplete (contains a '?') or invalid, but the Proof Asst GUI\n"
        + "requires successful proof unification for exports to the MMT Folder (you\n"
        + "may manually edit '.mmt' theorems in the MMT Folder.)\n\n"
        + "* The Proof Worksheet format is also used by the Metamath 'eimm.exe' program\n"
        + "for import and export of proofs to and from a Metamath database. eimm.exe\n"
        + "provides the Export command to export proofs from a Metamath .mm database to\n"
        + "a Proof Worksheet file -- even incomplete or invalid proofs -- and the\n"
        + "Import command to read a Proof Worksheet file and store its proof into a\n"
        + "Metamath .mm database.\n\n"
        + "* At any point you can freely use Edit Cut, Copy and Paste to replace all or\n"
        + "part of a Proof Worksheet.\n\n"
        + "* The format of a Proof Worksheet satisfies the Metamath validation\n"
        + "requirements for a Metamath Comment statement, which begins with '$(' and\n"
        + "ends with '$)'. In fact, an mmj2 Proof Worksheet can be copied manually as a\n"
        + "large Metamath comment directly into the text of a Metamath .mm database\n"
        + "(which is ALSO an ASCII text file.)\n\n"
        + "* A Proof Worksheet begins with a 'Header' statement line, containing a '$('\n"
        + "token starting in column 1, and ends with a 'Trailer' statement line\n"
        + "containing a '$)' token starting in column 1.\n\n"
        + "* 'Statements' in a Proof Worksheet always begin in column 1 of a line on\n"
        + "the screen and continue up to the next Statement's start. In other words, a\n"
        + "line containing a blank character in column 1 is, by definition, a\n"
        + "continuation of the previous Proof Worksheet Statement.\n\n"
        + "* A Comment Statement is denoted by an '*' (asterisk) in column 1.\n\n"
        + "* A Proof Worksheet must contain at least one proof step Statement, the step\n"
        + "labelled 'qed', which must be the last proof step in the Proof Worksheet.\n\n"
        + "* Hypothesis proof step Statements are identified by an 'h' in column 1,\n"
        + "with the 'h' prefixing the Step Number.\n\n"
        + "* Every proof step Statment that is not an Hypothesis step is, by\n"
        + "definition, a Derivation proof step Statement.\n\n"
        + "* Proof step numbers, except for the 'qed' step 'number' are positive\n"
        + "integers, but the numbers need not be in ascending order. There cannot be\n"
        + "duplicate step numbers, however.\n\n"
        + "The rest of the mmj2 Proof Worksheet format requirements you can learn by\n"
        + "inspection, or by reviewing the additional documentation about mmj2 and its\n"
        + "Proof Assistant, which can be found on the mmj2.html page within the\n"
        + "mmj2.zip download file. Also, be sure to check out the interactive Proof\n"
        + "Assistant Tutorial!\n\n"
        + "----------------------------------------------------------------------------\n\n"
        + "Here is a brief summary of the mmj2 Proof Assistant Menu Options.\n\n\n"
        + "========= File Menu =========\n\n"
        + "* Save Proof File -- Saves the current Proof Text Area to a text file.\n\n"
        + "* New Proof -- Creates a 'skeleton' Proof Worksheet for a new or existing\n"
        + "Theorem.\n\n"
        + "* New-Next Proof -- Creates a 'skeleton' Proof Worksheet for the next\n"
        + "theorem in the input Metamath database after the current theorem's location.\n\n"
        + "* Open Proof File -- Reads and displays a Proof Worksheet (text) file.\n\n"
        + "* Get Proof -- Creates and displays a Proof Worksheet for an existing\n"
        + "theorem's proof in the input Metamath .mm database.\n\n"
        + "* Forward-Get Proof -- Creates and displays the Proof Worksheet for the\n"
        + "proof of the next theorem after the current location within the input\n"
        + "Metamath .mm database. Wraps around to the start of the database after the\n"
        + "end is reached.\n\n"
        + "* Backward-Get Proof -- Creates and displays the Proof Worksheet for the\n"
        + "proof of the theorem prior to the current location within the input Metamath\n"
        + ".mm database. Wraps around to the end of the database after the start is\n"
        + "reached.\n\n"
        + "* Close Proof File -- Closes the current Proof Worksheet after providing an\n"
        + "opportunity to save any unsaved changes.\n\n"
        + "* Save As -- Saves the current Proof Worksheet to a text file after\n"
        + "providing an opportunity to change the file name.\n\n"
        + "* Export Via GMFF -- GMFF (Graphics Mode Formula Formatting) exports the\n"
        + "Proof Worksheet as html file(s) in the \\mmj2jar\\gmff directory.\n"
        + "See \\mmj2\\doc\\GMFFDoc\\* \n\n"
        + "* Exit/Quit -- Exits the Proof Assistant GUI program after providing an\n"
        + "opportunity to save any unsaved changes.\n\n\n"
        + "========= Edit Menu =========\n\n"
        + "* Undo -- Undoes the last 'undoable edit' made to the Proof Worksheet. Note\n"
        + "that you must Undo twice in succession following Reformat, Unification and\n"
        + "perhaps other Menu functions to restore the Proof Worksheet to its previous\n"
        + "state. The first Undo in this scenario results in a blank text area, and the\n"
        + "second Undo restores the text. We at MMJ2 Laboratories, Inc. sincerely\n"
        + "apologize for this sadly deficient behavior. Be assured that we will be\n"
        + "complaining on your behalf to someone, somewhere, sometime about this shoddy\n"
        + "workmanship.\n\n"
        + "* Redo -- 'Undoes' an Undo.\n\n"
        + "* Cut -- Standard text 'cut' operation on selected text. The 'cut' text is\n"
        + "copied to the clipboard.\n\n"
        + "* Copy -- Standard text 'copy' operation which copies the selected text to\n"
        + "the clipboard.\n\n"
        + "* Paste -- Standard text 'paste' operation which 'pastes' the contents of the\n"
        + "text clipboard to the screen at the position of the text caret (cursor).\n\n"
        + "* Set Incomplete Step Cursor -- Controls cursor positioning after Unification\n"
        + "if there are no errors. 'First' and 'Last' mean position the cursor to the\n"
        + "first/last incomplete step, respectively, while 'AsIs' means do not move the\n"
        + "to a different proof step. If unification is successful, or if there are no\n"
        + "errors and no incomplete proof steps, the cursor is positioned to the 'qed'\n"
        + "step.\n\n"
        + "* Set Soft Dj Vars Error Handling -- Controls program responses to missing\n"
        + "$d statements on the theorem being proved. GenerateReplacements produces a\n"
        + "set of $d statements to replace the existing $d statements if there are any\n"
        + "soft Dj Vars errors. GenerateDifferences produces $d statements for just\n"
        + "the missing $d statements. GenerateNew is the similar as GenerateReplacements\n"
        + "except that new $d statements are produced even if there are no missing $d\n"
        + "statements on the theorem being proved. Also, GenerateNew does not use any of\n"
        + "the existing $d statements in the input .mm file (i.e. extraneous $d\n"
        + "statements are eliminated by GenerateNew -- a very handy feature!). Option\n"
        + "'Report' produces detailed error messages at the proof step level for any\n"
        + "soft Dj Vars errors. Option 'Ignore' turns off soft Dj Vars Error processing.\n\n"
        + "* Set Font Family -- Dialog Box for choosing a Font. Note that only fixed\n"
        + "width fonts display formulas with proper alignment on the screen. Also, the\n"
        + "Dialog box is rather primitive and requires the user to type in the name\n"
        + "of the desired Font Family. \n\n"
        + "* Set Font Style Bold -- Changes the Proof Worksheet text to Bold.\n\n"
        + "* Set Font Style Plain -- Changes the Proof Worksheet text to not-Bold.\n\n"
        + "* Larger Font Size -- Increases the Font Size.\n\n"
        + "* Smaller Font Size -- Decreases the Font Size.\n\n"
        + "* Set Foreground Color -- Dialog Box for selecting the foreground color of\n"
        + "proof text. Note that the Title in the Dialog Box displays the RGB color\n"
        + "numbers of the current color (e.g. black = '0,0,0'). RGB numbers are used in\n"
        + "the mmj2 RunParms file for setting color.\n\n"
        + "* Set Background Color -- Dialog Box for selecting the background color of\n"
        + "proof text. Note that the Title in the Dialog Box displays the RGB color\n"
        + "numbers of the current color (e.g. white = '0,0,0'). RGB numbers are used in\n"
        + "the mmj2 RunParms file for setting color.\n\n"
        + "* Set Format Number -- Dialog Box for choosing the TMFF Format Number to\n"
        + "use in formatting formulas. '3' is a good choice for use with set.mm. Note\n"
        + "that the proof is reformatted if you change the Format Number. \n\n"
        + "* Set Indent -- Dialog Box for choosing the number of columns to indent each\n"
        + "formula for each proof level. Note: indentation occurs only when the program\n"
        + "formats or reformats a proof step -- at present, reformatting occurs when a\n"
        + "Work Variable is resolved during Unification.\n\n"
        + "* Reformat Proof -- Reformats the proof using the TMFF Format Number \n"
        + "presently in use. This is handy for tidying up.\n\n"
        + "* Reformat Proof: Swap Alt -- Toggles back and forth between the current\n"
        + "Format Number and Indent and the Alternate Format Number and Indent (set\n"
        + "by RunParms TMFFAltFormat and TMFFAltIndent).\n\n\n"
        + "=========== Cancel Menu ===========\n\n"
        + "* Cancel (Looping? Kill it!!!) -- Cancels an ongoing Menu function, such as\n"
        + "Unification. Note: most functions, inluding Unification, are so fast that\n"
        + "there is no time to 'Cancel' before they finish. However, if something\n"
        + "appears to be taking a long time, hit 'Alt+Tab' to switch to the Command\n"
        + "Window used to start mmj2 and the Proof Assistant. In the event that a\n"
        + "program bug is encountered, a serious-looking error message is regurgitated\n"
        + "on the Command Window; thus, the program may not be looping, but halted! In\n"
        + "this case, 'Cancel' will unlock the Proof Assistant and you may be able to\n"
        + "continue working -- but please File/Save the Proof Worksheet and include it\n"
        + "with a problem report (MMJ2 Laboratories, Inc. apologizes in advance for\n"
        + "any inconveniences: We're sorry, our bad!)\n\n\n"
        + "===== Unify Menu =====\n\n"
        + "* Unify (check proof) -- Validates the Proof Worksheet and ensures that the\n"
        + "'qed' step is correctly derived via 'unifications' with existing assertions\n"
        + "in the input Metamath .mm database. FYI, the term 'unification' refers to\n"
        + "the process of making sure that a derivation step's formula and hypotheses\n"
        + "match the pattern of a given assertion and its hypotheses. By 'match' we\n"
        + "mean that there exists a consistent set of substitutions that, when\n"
        + "simultaneously substituted for the corresponding variables of the referenced\n"
        + "assertion, the derivation step's formulas and hypotheses are exactly\n"
        + "reproduced. The term 'consistent' means if variable 'x' occurs in more than\n"
        + "one place in the referenced assertion and its hypotheses, that the same\n"
        + "substitution is made to every occurrence of 'x'. If the 'qed' step is\n"
        + "successfully unified and no other errors are encountered, the Proof\n"
        + "Assistant generates a Metamath RPN-format proof which can be copied into the\n"
        + "input Metamath .mm database text file. Note: extraneous derivation steps are\n"
        + "allowed but are not included in the generated RPN proof.\n\n"
        + "* Unify+Renumber -- Same as 'Unify (check proof)', but in addition, the step\n"
        + "numbers are renumbered from 1, by 1.\n\n"
        + "* Unify+Erase and Rederive Formulas -- Same as 'Unify (check proof)', but\n"
        + "before the unification process begins the formula is erased on every\n"
        + "non-qed Derivation Step which has a Ref label; if there are no errors\n"
        + "the 'Derive' feature will rederive the formulas.\n\n"
        + "* Step Selector Search -- Displays Step Selector Dialog screen for the proof\n"
        + "step on which the input cursor is located. For maximum speed double-click\n"
        + "any Derivation proof step to invoke Step Selector Search. Provides list of\n"
        + "assertions which can be unified with the indicated step and its hypotheses.\n"
        + "Add a '?' to the step's hypotheses to indicate that zero or more additional\n"
        + "hypotheses are acceptable. When an assertion is selected on the dialog screen\n"
        + "the label of the assertion is text-edited by the program into the proof\n"
        + "step's Ref field, and then the standard Unification process is performed.\n\n"
        + "* Reshow Step Selector Dialog -- redisplays the dialog, just as it was. This\n"
        + "feature enables the user to Undo the last unification (Ctrl-Z twice), then\n"
        + "press Ctrl-9 to Reshow the Step Selector Dialog and make another selection.\n\n"
        + "* Set Step Selector Max Results -- limits the number of assertions returned\n"
        + "by the Step Selector Search.\n\n"
        + "* Set Step Selector Show Substitutions -- true/false option to display, or\n"
        + "not, substitutions from the proof into unifiable assertions (default = true).\n"
        + "If false, assertions are displayed as they appear in the input .mm\n"
        + "file.\n\n\n"
        + "===== TL Menu (Theorem Loader) =====\n\n"
        + "* Unify + Store In LogSys and MMT Folder -- Validates the Proof Worksheet,\n"
        + "and if proof unification is successful, writes the theorem to the MMT Folder\n"
        + "and stores it in the Logical System in memory.\n\n"
        + "* Unify + Store In MMT Folder -- Validates the Proof Worksheet, and if proof\n"
        + "unification is successful, writes the theorem to the MMT Folder.\n\n"
        + "* Load Theorems From MMT Folder -- Loads every theorem file suffixed with\n"
        + "'.mmt' in the MMT Folder into the Logical System in memory.\n\n"
        + "* Extract Theorem To MMT Folder -- Exports a theorem in the Logical System\n"
        + "to the MMT Folder. The output file name is the theorem label suffixed with\n"
        + "'.mmt'.\n\n"
        + "* Verify All Proofs -- Runs the Metamath Proof Verification algorithm against\n"
        + "every theorem in the Logical System in memory. This is particularly useful if\n"
        + "the Theorem Loader has been used to update $d restrictions for an existing\n"
        + "theorem.\n\n"
        + "* Set Theorem Loader MMT Folder -- Designates the folder used by the Theorem\n"
        + "Loader for input/output of '.mmt' theorem files.\n\n"
        + "* Set Theorem Loader Dj Vars Option -- Specifies whether the Theorem Loader\n"
        + "should 'Merge', 'Replace' or 'NoUpdate' $d restrictions in the Logical System\n"
        + "when loading theorems from the MMT Folder. 'NoUpdate' is the default but if\n"
        + "the Proof Asst GUI Edit menu 'Set Soft Dj Vars Error Handling' is set to\n"
        + "'GenerateNew', then the 'Theorem Loader Dj Vars' option 'Replace' is good!\n\n"
        + "* Set Theorem Loader Audit Messages -- Specifies whether or not the Theorem\n"
        + "Loader should generate 'audit messages'. The default is 'Yes'. The audit\n"
        + "messages are displayed in the message area on the Proof Asst GUI and are very\n"
        + "helpful.\n\n"
        + "* Set Store MM Indent Amt -- indentation amount used when writing theorems\n"
        + "to the MMT folder. Default is 2.\n\n"
        + "* Set Store MM Right Col -- right margin used when writing theorems to the\n"
        + "MMT folder. Default is 79.\n\n"
        + "* Set Store MM Formulas AsIs -- If 'yes', writes formulas to the MMT Folder\n"
        + "as they are in the Proof Worksheet. This parameter has no effect when\n"
        + "storing theorems which already exist in the Logical System (only proofs and\n"
        + "$d restrictions are updated for these theorems.)\n\n\n"
        + "==== 2nd/Right Mouse Button Pop-Up Menu ====\n\n"
        + "* Cut, Copy, Paste -- Same as Edit Menu functions describe above.\n\n"
        + "* Reformat Step -- Reformat the proof step where the cursor is located using\n"
        + "the current TMFF Format Number and Indent Amount.\n\n"
        + "* Reformat Step:Swap Alt -- Swap/toggle current TMFF Format Number and\n"
        + "Amount to or from the Alternate Format Number and Indent Amount, and then\n"
        + "reformat just the proof step where the cursor is located. Note that this\n"
        + "function toggles the current settings (the 'mode') for the entire proof,\n"
        + "and that the program retains no memory about the formatting of individual\n"
        + "proof steps.\n\n"
        + "* Step Selector Search -- Same as Unify Menu item above.\n\n"
        + "* Reshow Step Selector Dialog -- Same as Unify Menu item above.\n\n\n"
        + "========= GMFF Menu =========\n\n"
        + "* Export Via GMFF -- GMFF (Graphics Mode Formula Formatting) exports the\n"
        + "Proof Worksheet as html file(s) in the \\mmj2jar\\gmff directory.\n"
        + "See \\mmj2\\doc\\GMFFDoc\\* \n\n\n"
        + "==== Help Menu ====\n\n"
        + "* General Help Info -- This page.\n\n"
        + "* About mmj2 -- Some copyright information *plus* very interesting data\n"
        + "about your computer's memory: 'Max' =  the maximum amount of memory that the\n"
        + "Java Virtual Machine will attempt to hog; 'Free' = memory available for use,\n"
        + "which may increase after the JVM does its 'garbage collection' duties; and\n"
        + "'Total' = the total amount of memory in the JVM at the time (will be <=\n"
        + "Max.) Note: if your machine is memory constrained, or if you wish to\n"
        + "economize, there are mmj2 RunParms which may be altered to reduce the memory\n"
        + "requirements of mmj2 and the Proof Assistant -- and these may also shorten\n"
        + "the mmj2 start-up time!\n";

    // ----------------------------------------------------------
    // Constants for ProofAsstException.java
    // ----------------------------------------------------------
    public static final String ERRMSG_TXT_LINE = " Line: ";
    public static final String ERRMSG_TXT_COLUMN = " Column: ";

    // ----------------------------------------------------------
    // Constants for ProofWorksheet.java
    // ----------------------------------------------------------

    // unificationStatus meanings:
    // 0 = not unified: this is the default and will be the final result if
    // unification is not even attempted (perhaps due to hypFldIncomplete).
    // 1 = unification error: signifies that unification failed; either the
    // input Ref was wrong, or the unification search did not find a match.
    // 2 = attempt cancelled - unification could not be performed but we didn't
    // find out until derivStepFormula or derivStepHyp was performed for the
    // step or one of its hyps; incomplete formulas prevent unification
    // attempts!
    // 3 = unified but incomplete hyps: signifies that one or more hypotheses
    // used were not successfully unified or are missing
    // 4 = unified but work vars: signifies that work vars are present in the
    // step formula or the step hypotheses.
    // 5 = unified: good to go for proof building attempt.
    public static final int UNIFICATION_STATUS_NOT_UNIFIED = 0;
    public static final int UNIFICATION_STATUS_UNIFICATION_ERROR = 1;
    public static final int UNIFICATION_STATUS_ATTEMPT_CANCELLED = 2;
    public static final int UNIFICATION_STATUS_UNIFIED_W_INCOMPLETE_HYPS = 3;
    public static final int UNIFICATION_STATUS_UNIFIED_W_WORK_VARS = 4;
    public static final int UNIFICATION_STATUS_UNIFIED = 5;

    // djVarsErrorStatus meanings:
    // 0 = No Dj Vars Errors
    // 1 = Soft Dj Vars Errors; user must add $d statements to satisfy unifying
    // Ref.
    // 2 = Hard Dj Vars Errors; formula violates
    // $d restriction(s) of unifying Ref.
    public static final int DJ_VARS_ERROR_STATUS_NO_ERRORS = 0;
    public static final int DJ_VARS_ERROR_STATUS_SOFT_ERRORS = 1;
    public static final int DJ_VARS_ERROR_STATUS_HARD_ERRORS = 2;

    /**
     * Descriptions for ProofWorkStmt.status values.
     * <p>
     * This is used only for test output.
     */
    public static final String[] STATUS_DESC = {
            "PROOF_STMT_HAS_STRUCTURAL_ERRORS", "PROOF_STMT_VALIDATION_ERRORS",
            "PROOF_STMT_INCOMPLETE", "PROOF_STMT_INCOMPLETE_HYPS",
            "PROOF_STMT_UNIFICATION_FAILURE", "PROOF_STMT_VALID",
            "PROOF_STMT_UNIFIED_BUT_INCOMPLETE",
            "PROOF_STMT_UNIFIED_NEEDS_PROOF", "PROOF_STMT_PROVED",
            "PROOF_STMT_PROVED_BUT_DJ_VARS_ERROR",
            "PROOF_STMT_PROVED_W_VERIFY_PROOFS_ERROR"};

    /**
     * Proof Worksheet Header Line, Part 1.
     */
    public static final String PROOF_TEXT_HEADER_1 = "$( <MM> <PROOF_ASST> THEOREM=";

    /**
     * Proof Worksheet Header Line, Part 2.
     */
    public static final String PROOF_TEXT_HEADER_2 = "  LOC_AFTER=";

    /**
     * Proof Worksheet Statement Label Default (prompt).
     */
    public static final String DEFAULT_STMT_LABEL = "?";

    /**
     * Proof Worksheet Comment Statment IO Error.
     */
    public static final String PROOF_WORKSHEET_COMMENT_STMT_IO_ERROR = "IO error reading comment!";

    /**
     * Proof Worksheet Footer Line
     */
    public static final String PROOF_TEXT_FOOTER = "$)";

    /**
     * Header ProofWorkStmt token.
     */
    public static final String HEADER_STMT_TOKEN = "$(";

    /**
     * Header ProofWorkStmt <MM> token
     */
    public static final String HEADER_MM_TOKEN = "<MM>";

    /**
     * Header ProofWorkStmt <MM> token chunk index.
     * <p>
     * Chunk refers to tokens plus whitespace chunks.
     */
    public static final int HEADER_MM_TOKEN_CHUNK_INDEX = 2;

    /**
     * Header ProofWorkStmt <PROOF_ASST> token
     */
    public static final String HEADER_PROOF_ASST_TOKEN = "<PROOF_ASST>";

    /**
     * Header ProofWorkStmt <PROOF_ASST> token chunk index.
     * <p>
     * Chunk refers to tokens plus whitespace chunks.
     */
    public static final int HEADER_PROOF_ASST_TOKEN_CHUNK_INDEX = 4;

    /**
     * Header ProofWorkStmt THEOREM= prefix
     */
    public static final String HEADER_THEOREM_EQUAL_PREFIX = "THEOREM=";

    /**
     * Header ProofWorkStmt THEOREM= prefix chunk index.
     * <p>
     * Chunk refers to tokens plus whitespace chunks.
     */
    public static final int HEADER_THEOREM_EQUAL_PREFIX_CHUNK_INDEX = 6;

    /**
     * Header ProofWorkStmt LOC_AFTER= prefix
     */
    public static final String HEADER_LOC_AFTER_EQUAL_PREFIX = "LOC_AFTER=";

    /**
     * Header ProofWorkStmt LOC_AFTER= prefix chunk index.
     * <p>
     * Chunk refers to tokens plus whitespace chunks.
     */
    public static final int HEADER_LOC_AFTER_EQUAL_PREFIX_CHUNK_INDEX = 8;

    /**
     * Footer ProofWorkStmt token.
     */
    public static final String FOOTER_STMT_TOKEN = "$)";

    /**
     * Generated Proof ProofWorkStmt token.
     */
    public static final String GENERATED_PROOF_STMT_TOKEN = "$=";

    /**
     * End ProofWorkStmt token (generated proof and $d)
     */
    public static final String END_PROOF_STMT_TOKEN = "$.";

    /**
     * Distinct Variables ProofWorkStmt token.
     */
    public static final String DISTINCT_VARIABLES_STMT_TOKEN = "$d";

    /**
     * QED ProofStep ProofWorkStmt token prefix (CAPS)
     */
    public static final String QED_STEP_NBR_CAPS = "QED";

    /**
     * QED ProofStep ProofWorkStmt token prefix
     */
    public static final String QED_STEP_NBR = "qed";

    /**
     * Hyp ProofStep ProofWorkStmt token prefix
     */
    public static final String HYP_STEP_PREFIX = "h";

    /**
     * Comment ProofWorkStmt token prefix
     */
    public static final String COMMENT_STMT_TOKEN_PREFIX = "*";

    /**
     * Proof Step Step/HypRef Field Delimiter: Colon
     */
    public static final char FIELD_DELIMITER_COLON = ':';

    /**
     * Maximum colons in Proof Step Step/Hyp/Ref Field.
     */
    public static final int MAX_FIELD_DELIMITER_COLONS = 2;

    /**
     * Proof Step Hyp Field Delimiter: Comma
     */
    public static final char FIELD_DELIMITER_COMMA = ',';
    /**
     * Local Ref Escape Character: Number Sign
     */
    public static final char LOCAL_REF_ESCAPE_CHAR = '#';

    /**
     * Proof Text Reader caption.
     * <p>
     * This is just a caption field used when creating a mmj.mmio.Tokenizer
     * object
     */
    public static final String PROOF_TEXT_READER_CAPTION = "Proof Text Reader";

    /**
     * Prefix for Derive feature steps.
     * <p>
     * This is used on steps generated by the derive feature.
     */
    public static final String DERIVE_STEP_PREFIX = "d";

    /**
     * Greatest Step Number Increment Amount.
     * <p>
     * This is used to generate new greatestStepNbr values for the ProofUnifier
     * Derive feature.
     */
    public static final int GREATEST_STEP_NBR_INCREMENT_AMT = 1;

    // ----------------------------------------------------------
    // Constants for StepUnifier.java
    // ----------------------------------------------------------

    /**
     * Applied array init size.
     * <p>
     * Used for backout of unification updates.
     */
    public static final int STEP_UNIFIER_APPLIED_ARRAY_LEN_INIT = 1000;

    /**
     * Applied array max size.
     * <p>
     * Used for backout of unification updates.
     */
    public static final int STEP_UNIFIER_APPLIED_ARRAY_LEN_MAX = 8000;

    // ----------------------------------------------------------
    // Constants for StepRequest.java
    // ----------------------------------------------------------

    /**
     * STEP_REQUEST_SELECTOR_SEARCH = 81.
     */
    public static final int STEP_REQUEST_SELECTOR_SEARCH = 81;

    /**
     * STEP_REQUEST_SELECTOR_CHOICE = 82.
     */
    public static final int STEP_REQUEST_SELECTOR_CHOICE = 82;

    public static final int STEP_REQUEST_STEP_SEARCH = 91;

    public static final int STEP_REQUEST_GENERAL_SEARCH = 92;

    public static final int STEP_REQUEST_SEARCH_OPTIONS = 93;

    public static final int STEP_REQUEST_STEP_SEARCH_CHOICE = 94;

    // ----------------------------------------------------------
    // Constants for StepSelectorDialog.java
    // ----------------------------------------------------------

    /**
     * STEP_SELECTOR_DIALOG_TITLE = 'StepSelectorDialog'.
     */
    public static final String STEP_SELECTOR_DIALOG_TITLE = "StepSelectorDialog";

    /**
     * STEP_SELECTOR_DIALOG_HIDE_BUTTON_CAPTION = 'Hide Dialog'.
     */
    public static final String STEP_SELECTOR_DIALOG_HIDE_BUTTON_CAPTION = "Hide Dialog";

    /**
     * STEP_SELECTOR_DIALOG_SET_BUTTON_CAPTION = 'Apply Selection To Step And
     * Unify Proof'.
     */
    public static final String STEP_SELECTOR_DIALOG_SET_BUTTON_CAPTION = "Apply Selection To Step And Unify Proof";

    /**
     * STEP_SELECTOR_DIALOG_POPUP_SET_BUTTON_CAPTION = 'Apply Selection To Step
     * And Unify Proof?'.
     */
    public static final String STEP_SELECTOR_DIALOG_POPUP_SET_BUTTON_CAPTION = "Apply Selection To Step And Unify Proof?";

    /**
     * STEP_SELECTOR_DIALOG_LIST_CAPTION_PREFIX = 'Step '
     */
    public static final String STEP_SELECTOR_DIALOG_LIST_CAPTION_PREFIX = "Step ";

    /**
     * STEP_SELECTOR_DIALOG_LIST_CAPTION_SUFFIX = ' Unifiable Assertions'
     */
    public static final String STEP_SELECTOR_DIALOG_LIST_CAPTION_SUFFIX = " Unifiable Assertions";

    // ----------------------------------------------------------
    // Constants for StepSelectorSearch.java
    // ----------------------------------------------------------

    /**
     * STEP_SELECTOR_SEARCH_HYP_LOOKUP_MAX = '3'.
     * <p>
     * 95% of set.mm theorems have 0, 1 or 2 logical hypotheses and it makes
     * sense in StepSelectorSearch to use a binary search to establish the first
     * search index for these theorem, otherwise, just scan forward.
     */
    public static final int STEP_SELECTOR_SEARCH_HYP_LOOKUP_MAX = 3;

    /**
     * STEP_SELECTOR_SEARCH_FORMULA_INDENT = ' '.
     */
    public static final String STEP_SELECTOR_SEARCH_FORMULA_INDENT = "    ";

    /**
     * STEP_SELECTOR_FORMULA_LABEL_SEPARATOR = ' ::= '.
     */
    public static final String STEP_SELECTOR_FORMULA_LABEL_SEPARATOR = " ::= ";

    /**
     * STEP_SELECTOR_FORMULA_LOG_HYP_SEPARATOR = ' && '.
     */
    public static final String STEP_SELECTOR_FORMULA_LOG_HYP_SEPARATOR = " &&  ";

    /**
     * STEP_SELECTOR_FORMULA_YIELDS_SEPARATOR = ' ==> '.
     */
    public static final String STEP_SELECTOR_FORMULA_YIELDS_SEPARATOR = " ==> ";

    // ----------------------------------------------------------
    // Constants for StepSelectorStore.java
    // ----------------------------------------------------------

    /**
     * STEP_SELECTOR_LIST_MORE_LITERAL = ***MORE
     ***/
    public static final String STEP_SELECTOR_LIST_MORE_LITERAL = "***MORE***";

    /**
     * STEP_SELECTOR_LIST_END_LITERAL = ***END
     ***/
    public static final String STEP_SELECTOR_LIST_END_LITERAL = "***END***";

    // ----------------------------------------------------------
    // Messages from ProofAsst.java
    // ----------------------------------------------------------

    public static final String ERRMSG_THEOREM_CAPTION = " Theorem ";

    public static final String ERRMSG_PA_UNIFY_IO_ERROR = "E-PA-0102"
        + " Theorem %s: I/O Error encountered: %s";

    public static final String ERRMSG_PA_UNIFY_SEVERE_ERROR = "E-PA-0103"
        + " Theorem %s: Serious error message (technical) follows: %s";

    public static final String ERRMSG_PA_IMPORT_STRUCT_ERROR = "E-PA-0104"
        + " Theorem %s has structural error(s)! Abandoning importFromFile() process! ";

    public static final String ERRMSG_PA_IMPORT_ERROR = "E-PA-0105"
        + " Theorem %s: Error(s) encountered: %s\nAbandoning importFromFile() process! ";

    public static final String ERRMSG_PA_IMPORT_IO_ERROR = "E-PA-0106"
        + " Theorem %s: I/O Error encountered. Abandoning importFromFile() process! ";

    public static final String ERRMSG_PA_IMPORT_SEVERE_ERROR = "E-PA-0107"
        + " Theorem %s: Severe error encountered. Abandoning importFromFile()"
        + " process! Serious error message (technical) follows: %s";

    public static final String ERRMSG_PA_EXPORT_PV_ERROR = "E-PA-0108"
        + " Theorem %s: Unable to get (export) the full proof because the Proof"
        + " Verification engine reported an error in the proof itself."
        + " VerifyProofs message text follows: %s";

    public static final String ERRMSG_PA_EXPORT_STRUCT_ERROR = "A-PA-0109"
        + " Theorem %s: exportOneTheorem() is abandoning the attempt to export"
        + " the proof because the proof worksheet has a structural error of"
        + " some kind!";

    public static final String ERRMSG_PA_EXPORT_IO_ERROR = "A-PA-0110"
        + " Theorem %s: exportOneTheorem() is abandoning the attempt to export"
        + " the proof because an I/O error was encountered on the export file."
        + " Message follows: %s";

    public static final String ERRMSG_PA_NOTHING_TO_UNIFY = "I-PA-0111"
        + " Theorem %s: Unification process not started: no derivation"
        + " proof steps are ready for unification.";

    public static final String ERRMSG_PA_PRINT_IO_ERROR = "A-PA-0112"
        + " Theorem %s: ProofAsst.printProof() is abandoning the attempt to print."
        + " I/O error encountered! Message text follows: %s";

    public static final String ERRMSG_PA_TESTMSG_01 = "I-PA-0113 Theorem %s:"
        + " Unification Time Elapsed (tenths of sec) = %d, Status = %d: %s";

    public static final String ERRMSG_PA_TESTMSG_02 = "I-PA-0114 Theorem %s:"
        + " Unified and Proved in Import Batch Test but the new"
        + " proof is different from the proof in the Metamath"
        + " file that was loaded. 1st difference: old proof stmt = %s,"
        + " new proof stmt = %s (empty String means proof lengths differed.)";

    public static final String ERRMSG_PA_TESTMSG_03 = "I-PA-0115 TEST TOTALS:"
        + " nbrTestTheoremsProcessed = %d, nbrTestNotProvedPerfectly = %d,"
        + " nbrTestProvedDifferently = %d";

    public static final String ERRMSG_PA_FWD_BACK_SEARCH_NOTFND = "E-PA-0116"
        + " No (existing) theorems found in the %s direction. Forward/Backward"
        + " searches wrap around when the end is reached. A not found condition"
        + " after a Forward or Backward request indicates that there are no"
        + " existing theorems in the search list (empty!) Doublecheck the input"
        + " RunParms, including the 'Load', 'ProofAsstUnifySearchExclude',"
        + " 'LoadEndpointStmtNbr' and 'LoadEndpointStmtLabel' RunParms, if this"
        + " situation is a surprise to you.";

    public static final String ERRMSG_SUPERFLUOUS_MANDFRAME_DJVARS = "I-PA-0118"
        + " Superfluous MandFrame DjVars element(s) found for Theorem %s: %s";

    public static final String ERRMSG_PA_RPN_PROOF_GENERATED = "I-PA-0119"
        + " Theorem %s: RPN-format Metamath proof generated!";

    public static final String ERRMSG_STEP_SELECTOR_BATCH_TEST_NO_RESULTS = "E-PA-0120"
        + " Theorem %s: Null StepSelectorResults returned from StepRequest Search!";

    public static final String ERRMSG_STEP_SELECTOR_BATCH_TEST_INV_CHOICE = "E-PA-0121"
        + " Theorem %s: Selection Number %d out of range of StepSelectionResults."
        + " Must be 0 thru %d.";

    public static final String ERRMSG_STEP_SELECTOR_BATCH_TEST_CHOICE = "I-PA-0122"
        + " Theorem %s: StepSelectorResults choice for Step = %s"
        + " Selection Number = %d Ref = %s Selection = %s";

    public static final String ERRMSG_STEP_SELECTOR_RESULTS_PRINT = ""
        + "\nI-PA-0123 Theorem %s: Step %s\n";

    public static final String ERRMSG_PA_GUI_EXPORT_VIA_GMFF_FAILED = "E-PA-0124"
        + " Oops! Export Via GMFF failed. One or more error messages were"
        + " generated. See following messages.";

    public static final String ERRMSG_PA_GET_THEOREM_NOT_FOUND = "E-PA-0125"
        + " Theorem %s not found -- or input label is not a Metamath theorem.";

    public static final String ERRMSG_PA_START_THEOREM_NOT_FOUND = "E-PA-0126"
        + " Starting theorem %s for build of sorted list not found -- or input"
        + " label is not a Metamath theorem.";

    // ----------------------------------------------------------
    // Messages from ProofAsstGUI.java
    // ----------------------------------------------------------

    public static final String ERRMSG_PA_GUI_FILE_NOTFND = "E-PA-0201"
        + " File %s does not exist! Try again?";

    public static final String ERRMSG_PA_GUI_SAVE_BEFORE_ACTION = "I-PA-0202"
        + " Save changes before %s?";

    public static final String ERRMSG_PA_GUI_READ_PROOF_IO_ERR = "E-PA-0203"
        + " Oops. I/O Error encountered while attempting to\n"
        + "read your proof file. Perhaps the specified proof\n"
        + "file name is bogus or has been deleted?\n\n"
        + "Exact message from system follows:\n%s";

    public static final String ERRMSG_PA_GUI_FILE_EXISTS = "E-PA-0204"
        + " File %s already exists and will be overwritten!"
        + " Are you sure about the file name?";

    public static final String ERRMSG_PA_GUI_SAVE_IO_ERROR = "E-PA-0205"
        + " Oops(!). Error encountered while attempting to save your proof!"
        + " Suggest you copy the text to a text editor and save it manually?"
        + " For safety? I do not know the cause of this error. Very, very, ..."
        + " very sorry! The exact message from system follows: %s";

    public static final String ERRMSG_PA_GUI_VERIFY_ALL_PROOFS_NO_MSGS = ""
        + "I-PA-0207 OK! Verify All Proofs complete."
        + " No error or info messages were generated.";

    public static final String ERRMSG_PA_GUI_LOAD_THEOREMS_FROM_MMT_FOLDER_NO_MSGS = ""
        + "I-PA-0208 OK! Load Theorems From MMT Folder complete."
        + " No error or info messages were generated.";

    public static final String ERRMSG_PA_GUI_EXTRACT_THEOREMS_TO_MMT_FOLDER_NO_MSGS = ""
        + "I-PA-0209 OK! Extract Theorem To MMT Folder complete."
        + " No error or info messages were generated.";

    public static final String ERRMSG_PA_GUI_EXPORT_VIA_GMFF_NO_MSGS = "I-PA-0210"
        + " OK! Export Via GMFF complete."
        + " No error or info messages were generated.";

    public static final String ERRMSG_NO_MESSAGES_MSG_1 = "I-PA-0299 No Messages.";

    // ----------------------------------------------------------
    // Messages from ProofWorksheet.java
    // ----------------------------------------------------------

    public static final String ERRMSG_PROOF_EMPTY = "E-PA-0307"
        + " Proof text empty/end of file reached on first token!";

    public static final String ERRMSG_HDR_TOKEN_ERR = "E-PA-0308"
        + " Proof Text must begin with Header token '$(' starting in column 1."
        + " Found token = %s";

    public static final String ERRMSG_QED_MISSING = "E-PA-0309 Theorem %s:"
        + " Oops, sorry, the proof seems to be missing the 'qed' proof step!";

    public static final String ERRMSG_FOOTER_MISSING = "E-PA-0310 Theorem %s:"
        + " Oops, sorry, the proof seems to be missing the '$)' Footer statement.";

    public static final String ERRMSG_COL1_ERROR = "E-PA-0311 Theorem %s:"
        + " Proof text statements must begin in column 1."
        + " Line number %d is in error. Starting token = %s";

    public static final String ERRMSG_MULT_HDR_ERROR = "E-PA-0312 Theorem %s:"
        + " More than one Header statement in proof text!";

    public static final String ERRMSG_QED_MISSING2 = "E-PA-0313 Theorem %s:"
        + " Woops. Sorry to bother you, but the proof seems to be missing"
        + " the 'qed' proof step!";

    public static final String ERRMSG_SHR_BAD = "E-PA-0314 Theorem %s:"
        + " First token of ProofStep line has invalid format."
        + " Expecting valid StepHypRef token in line number %d."
        + " Found token = %s";

    public static final String ERRMSG_SHR_BAD2 = "E-PA-0315 Theorem %s"
        + " Step %s: First token of ProofStep line has invalid format."
        + " Expecting valid StepHypRef token in line number %d."
        + " Found token = %s";

    public static final String ERRMSG_HYP_HAS_HYP = "E-PA-0316 Theorem %s"
        + " Step %s: Hypothesis Proof Step line input with hyp references in"
        + " the Step/Hyp/Ref field. A Hypothesis should not refer to other"
        + " hypotheses.";

    public static final String ERRMSG_QED_NOT_END = "E-PA-0317 Theorem %s"
        + " Step %s: Oops. If it is not terribly inconvenient, would you please"
        + " put the 'qed' proof step statement after all other proof step"
        + " statements in the proof text area? My programmer is a bit of a"
        + " bore, and quite unimaginative. He insists upon this even though I"
        + " disagreed most vehemently. I told him I don't care where the 'qed'"
        + " statement is located but he is fixated upon this regimented,"
        + " by-the-books approach. So if you could please humor him in this"
        + " regard... Thank you!";

    public static final String ERRMSG_MULT_QED_ERROR = "E-PA-0318 Theorem %s"
        + " Step %s: More than one 'qed' proof step in proof text.";

    public static final String ERRMSG_QED_NOT_END2 = "E-PA-0319 Theorem %s"
        + " Step %s: The 'qed' proof step must be the final proof step.";

    public static final String ERRMSG_THRM_NBR_HYPS_ERROR = "E-PA-0320 Theorem %s:"
        + " Oops. The number of logical hypothesis proof steps input %d,"
        + " does not match the number defined for the theorem, which is %d."
        + " FYI, the File/New menu item provides a skeleton Proof Text area"
        + " that will show the expected hypotheses.";

    public static final String ERRMSG_STEP_NBR_MISSING = "E-PA-0321 Theorem %s"
        + " Step ?: Step number missing on Proof Step.";

    public static final String ERRMSG_QED_HYP_STEP = "E-PA-0322 Theorem %s"
        + " Step %s: Found 'h' prefix (signifying hypothesis proof step) on"
        + " 'qed' step number. A derivation step cannot also be a hypothesis"
        + " step.";

//    public static final String ERRMSG_STEP_LE_0_1 = "E-PA-0323 Theorem ";
//    public static final String ERRMSG_STEP_LE_0_2 = " Step ";
//    public static final String ERRMSG_STEP_LE_0_3 = ": step number is less than or equal to zero!";

//    public static final String ERRMSG_STEP_NOT_INT_1 = "E-PA-0324 Theorem ";
//    public static final String ERRMSG_STEP_NOT_INT_2 = " Step ";
//    public static final String ERRMSG_STEP_NOT_INT_3 = ": Step number is not a valid integer number.";

    public static final String ERRMSG_STEP_NBR_DUP = "E-PA-0325"
        + " Theorem %s Step %s: Duplicate Step number.";

    public static final String ERRMSG_READER_POSITION_LITERAL = ""
        + "  Proof Text input reader last position at ";

    public static final String ERRMSG_STMT_NOT_DONE = "E-PA-0326 Theorem %s:"
        + " New Proof Text statement begun while current statement incomplete."
        + " The token starting in column 1, beginning the new statement = %s";

    public static final String ERRMSG_PREMATURE_END = "E-PA-0327 Theorem %s:"
        + " Premature end of input reached during loading of a Proof Text"
        + " statement.";

    public static final String ERRMSG_PREMATURE_END2 = "E-PA-0328 Theorem %s:"
        + " Premature end of input reached during loading of a Proof Text"
        + " statement.";

    public static final String ERRMSG_EXTRA_TOKEN = "E-PA-0329 Theorem %s:"
        + " Found extra token at the end of the current Proof Text statement."
        + " Expecting next token in column 1 designating the start of the next"
        + " statement! Bogus token = %s";

    public static final String ERRMSG_BAD_HDR_TOKEN = "E-PA-0330 Theorem %s:"
        + " Invalid token in Proof Text Header. Expecting '<MM>' but found %s";

    public static final String ERRMSG_BAD_HDR_TOKEN2 = "E-PA-0331 Theorem %s:"
        + " Invalid token in Proof Text Header."
        + " Expecting '<PROOF_ASST>' but found %s";

    public static final String ERRMSG_BAD_HDR_TOKEN3 = "E-PA-0332 Theorem %s:"
        + " Invalid token in Proof Text Header."
        + " Expecting 'THEOREM=something' but found %s";

    public static final String ERRMSG_BAD_THRM_VAL = "E-PA-0333 Theorem %s:"
        + " Invalid 'THEOREM=' value = %s must be corrected before the rest of"
        + " the Proof Text is validated or unified.";

    public static final String ERRMSG_BAD_LOC_TOKEN = "E-PA-0334 Theorem %s:"
        + " Invalid token in Proof Text Header. Expecting 'LOC_AFTER=something'"
        + " but found %s";

    public static final String ERRMSG_BAD_LOC_VAL = "E-PA-0335 Theorem %s:"
        + " Invalid 'LOC_AFTER=' value = %s must be corrected before the rest"
        + " of the Proof Text is validated or unified.";

    public static final String ERRMSG_BAD_LABEL_CHAR = "E-PA-0337 Theorem %s:"
        + " theorem label contains characters that are prohibited by the"
        + " Metamath.pdf specification.";

    public static final String ERRMSG_PROHIB_LABEL = "E-PA-0338 Theorem %s:"
        + " theorem label is on the Metamath.pdf specification list of"
        + " prohibited labels. These include reserved names such as 'nul',"
        + " 'lpt1', etc.";

    public static final String ERRMSG_BAD_TYP_CD = "E-PA-0339 Theorem %s:"
        + " is a valid theorem, but has Type Code = %s, which is not equal to"
        + " the Grammar's Provable Logic Statement Type. ";

    public static final String ERRMSG_NOT_A_THRM = "E-PA-0340 Theorem %s:"
        + " label is valid but is not a theorem label";

    public static final String ERRMSG_LOC_NOTFND = "E-PA-0341 Theorem %s:"
        + " Invalid 'LOC_AFTER=' label = %s, not found in the Logical System"
        + " statement table. You could change the label, or, enter blank or '?'"
        + " to accept the default sequence in the database (at the end).";

    public static final String ERRMSG_BAD_TYP_CD2 = "E-PA-0342 Theorem %s"
        + " Step %s: Invalid first symbol in proof step formula = %s."
        + " The Proof Assistant requires that each formula begin with the"
        + " Grammar's Provable Logic Statement Type = %s";

    public static final String ERRMSG_SYM_NOTFND = "E-PA-0343 Theorem %s"
        + " Step %s: Invalid symbol in proof step formula. Input token = %s"
        + " not found in Logical System Symbol Table.";

    public static final String ERRMSG_VAR_SCOPE = "E-PA-0344 Theorem %s"
        + " Step %s: Invalid variable symbol %s in proof step formula: not in"
        + " scope of theorem (not in the theorem's Frame).";

    public static final String ERRMSG_SYM_MAXSEQ = "E-PA-0345 Theorem %s"
        + " Step %s: Invalid symbol in proof step formula. Input token = %s has"
        + " sequence number >= sequence number of Theorem or LOC_AFTER statement.";

    public static final String ERRMSG_PARSE_ERR = "E-PA-0346 Theorem %s"
        + " Step %s: Formula contains one of more grammatical parse errors"
        + " (somewhere in there). The error is probably a typo, like a missing"
        + " space or unbalanced parentheses. Note that Metamath is"
        + " case-sensitive, and in Proof Assistant, '$.' is not used to"
        + " terminate statements.";

    public static final String ERRMSG_FORMULA_NOMATCH = "E-PA-0347 Theorem %s"
        + " Step %s: Formula does not match the theorem's formula!"
        + " How curious... The File/New menu option provides the easiest way to"
        + " build a skeletal proof that avoids this error condition. Please"
        + " check your input against the Metamath file that was input for this"
        + " theorem.\nThe actual formula is: %s\n";

    public static final String ERRMSG_REF_NOTFND = "E-PA-0348 Theorem %s"
        + " Step %s: Invalid Ref = %s on derivation proof step. Does not"
        + " specify a valid statement in the Metamath file that was loaded. You"
        + " can leave Ref blank to allow Unify to figure it out for you.";

    public static final String ERRMSG_REF_MAXSEQ = "E-PA-0349 Theorem %s Step %s:"
        + " Invalid Ref = %s on derivation proof step. Ref statement sequence"
        + " number >= sequence number of Theorem or LOC_AFTER statement."
        + " You can leave Ref blank to allow Unify to figure it out for you.";

    public static final String ERRMSG_REF_NOT_ASSRT = "E-PA-0350 Theorem %s"
        + " Step %s: Invalid Ref = %s is not an Assertion. A derivation step"
        + " Ref must refer to an Assertion such as a logic Axiom or a Theorem."
        + " You can leave Ref blank to allow Unify to figure it out for you.";

    public static final String ERRMSG_REF_BAD_TYP = "E-PA-0351 Theorem %s"
        + " Step %s: Invalid Ref = %s, has Type Code = %s which is not equal to"
        + " the Grammar's Provable Logic Statement Type. You can leave Ref"
        + " blank to allow Unify to figure it out for you.";

    public static final String ERRMSG_BAD_HYP_STEP = "E-PA-0352 Theorem %s"
        + " Step %s: Invalid hyp step = %s. Hyp step must refer to a previous"
        + " proof step. Input '?' if you are not yet ready to specify the hyp step.";

    public static final String ERRMSG_HYP_STEP_NOTFND = "E-PA-0353 Theorem %s"
        + " Step %s: Hyp step = %s not found among previous proof steps."
        + " Input '?' if you are not yet ready to specify the hyp step.";

    public static final String ERRMSG_REF_NBR_HYPS = "E-PA-0354 Theorem %s"
        + " Step %s: Number of hyp steps specified = %d does not match the"
        + " number expected = %d for Ref = %s -- according to the database,"
        + " anyway. Input '?' if you are not yet ready to specify the hyp"
        + " step(s). Or, leave the Ref field blank to allow Unify to find the"
        + " applicable statement label.";

    public static final String ERRMSG_DUP_LOG_HYPS = "E-PA-0355 Theorem %s"
        + " Step %s: Theorem has duplicate LogHyp formulas(?) If you insist"
        + " upon having duplicates(?), please explicitly input a Ref label on"
        + " each Hypothesis Proof Step.";

    public static final String ERRMSG_HYP_FORMULA_ERR = "E-PA-0356 Theorem %s"
        + " Step %s: Theorem does not have a LogHyp formula matching this"
        + " formula. The File/New menu option provides the easiest way to build"
        + " a skeletal proof that avoids this error condition. Please check your"
        + " input against the Metamath file that was input for this theorem.";

    public static final String ERRMSG_REF_NOTFND2 = "E-PA-0357 Theorem %s"
        + " Step %s: Invalid Ref = %s on derivation proof step. Does not"
        + " specify a valid statement in the Metamath file that was loaded."
        + " You can leave Ref blank to allow Unify to figure it out for you.";

    public static final String ERRMSG_REF_NOT_LOGHYP = "E-PA-0358 Theorem %s"
        + " Step %s: Input Ref label = %s does not specify a valid LogHyp"
        + " statement in the Metamath file that was loaded.";

    public static final String ERRMSG_LOGHYP_MISMATCH = "E-PA-0359 Theorem %s"
        + " Step %s: Input Ref label = %s is not one of the LogHyps for this"
        + " Theorem, as specified in the Metamath file that was loaded.";

    public static final String ERRMSG_HYP_FORMULA_ERR2 = "E-PA-0360 Theorem %s"
        + " Step %s: Formula does not match the formula of Logical Hypothesis"
        + " %s, as specified in the Metamath file that was loaded. The"
        + " File/New menu option provides the easiest way to build a skeletal"
        + " proof that avoids this error condition. Please check your input"
        + " against the Metamath file data that was input for this theorem."
        + "\nThe actual formula is: %s\n";

    public static final String ERRMSG_HYP_REF_DUP = "E-PA-0361 Theorem %s"
        + " Step %s: Ref label = %s duplicates the label of another statement"
        + " on the Metamath file that For new theorems it is recommended that"
        + " you leave all Ref fields blank -- or that you enter all of them"
        + " (missing Ref's are automatically generated and may conflict with"
        + " manually entered Refs.)";

    public static final String ERRMSG_REF_CHAR_PROHIB = "E-PA-0362 Theorem %s"
        + " Step %s: Ref label = %s contains characters that are prohibited"
        + " by the Metamath.pdf specification.";

    public static final String ERRMSG_PROHIB_LABEL2 = "E-PA-0363 Theorem %s"
        + " Step %s: Ref label = %s: theorem label is on the Metamath.pdf"
        + " specification list of prohibited labels. These include reserved"
        + " names such as 'nul', 'lpt1', etc.";

    public static final String ERRMSG_DUP_HYP_REF = "E-PA-0364 Theorem %s"
        + " Step %s: Ref label = %s is Hypothesis Step label that duplicates"
        + " the Ref on another proof step. Generally, it is recommended that"
        + " you leave all Ref fields blank -- or that you enter all of them"
        + " (missing Ref's are automatically generated and may conflict with"
        + " manually entered Refs.)";

    public static final String ERRMSG_DV_SYM_ERR = "E-PA-0365 Theorem %s:"
        + " Invalid symbol in Distinct Variable statement. Input token = %s"
        + " not found in Logical System Symbol Table.";

    public static final String ERRMSG_DV_SYM_MAXSEQ = "E-PA-0366 Theorem %s:"
        + " Invalid symbol in Distinct Variable statement. Input token = %s."
        + " Sequence number >= sequence number of Theorem or LOC_AFTER statement.";

    public static final String ERRMSG_DV_SYM_CNST = "E-PA-0367 Theorem %s:"
        + " Invalid symbol in Distinct Variable statement. Input token = %s"
        + " is a constant, not a variable.";

    public static final String ERRMSG_DV_VAR_DUP = "E-PA-0368 Theorem %s:"
        + " Invalid symbol in Distinct Variable statement. Input token = %s"
        + " is a duplicate of another variable in the statement.";

    public static final String ERRMSG_DERIVE_FEATURE_STEP_NOTFND = "A-PA-0369"
        + " Programmer Error! Ooops. Sorry! Failed to find current derivation"
        + " step while performing addDerivStepForDeriveFeature() function!";

    public static final String ERRMSG_FORMULA_REQ = "E-PA-0370 Theorem %s"
        + " Step %s: Formula is required on hypothesis steps and on the"
        + " derivation 'qed' step.";

    public static final String ERRMSG_REF_NBR_HYPS_LT_INPUT = "E-PA-0371"
        + " Theorem %s Step %s: Number of hyp steps specified = %d is greater"
        + " than the number expected = %d for Ref = %s -- according to the"
        + " database, anyway. Input '?' if you are not yet ready to specify the"
        + " hyp step(s). Or, leave the Ref field blank to allow Unify to find"
        + " the applicable statement label.";

//    public static final String ERRMSG_FORMULA_OR_REF_REQ_1 = "E-PA-0372 Theorem ";
//    public static final String ERRMSG_FORMULA_OR_REF_REQ_2 = " Step ";
//    public static final String ERRMSG_FORMULA_OR_REF_REQ_3 = ": Formula or Ref required on derivation"
//        + " steps (Formula is always required on the 'qed' step).";

    public static final String ERRMSG_STMT_LABEL_DUP_OF_SYM_ID = "E-PA-0373"
        + " Theorem %s: theorem label duplicates a symbol id. This is"
        + " prohibited according to the Metamath.pdf spec change of 24-Jun-2006.";

    public static final String ERRMSG_STMT_LABEL_DUP_OF_SYM_ID2 = "E-PA-0374"
        + " Theorem %s Step %s: Ref label = %s: Ref label duplicates a symbol"
        + " id. This is prohibited according to the Metamath.pdf spec change"
        + " of 24-June-2006.";

    public static final String ERRMSG_SMOOSH_FAILED = "A-PA-0375 Program bug: "
        + "Fell through DerivationStep.smooshLeft()";

    public static final String ERRMSG_WV_LOC_ERR = "E-PA-0376 Theorem %s"
        + " Step %s: Work Var input in Hypothesis or QED step. Input token = %s."
        + " Work Vars may only be used in non-QED Derivation Steps.";

    public static final String ERRMSG_HYP_HAS_LOCAL_REF = "E-PA-0377 Theorem %s"
        + " Step %s: Hypothesis Proof Step line input with Local Ref escape"
        + " character ('#') in the Step/Hyp/Ref field. A Hypothesis should not"
        + " refer to other hypotheses.";

    public static final String ERRMSG_QED_HAS_LOCAL_REF = "E-PA-0378 Theorem %s"
        + " Step %s: QED Derivation Step line input with Local Ref escape"
        + " character ('#') in the Step/Hyp/Ref field. ";

    public static final String ERRMSG_BAD_LOCAL_REF = "E-PA-0379 Theorem %s"
        + " Step : #LocalRef invalid. Must match a previous step's Step or Ref,"
        + " and cannot refer to a step that is itself using a #LocalRef.";

    public static final String ERRMSG_HYP_HAS_SELECTOR_CHOICE = "E-PA-0380"
        + " Theorem %s Step %s: StepSelectorDialog selection (now) points to"
        + " a Hyp step in the ProofWorksheet.";

    public static final String ERRMSG_LOCAL_REF_HAS_SELECTOR_CHOICE = "E-PA-0381"
        + " Theorem %s Step %s: StepSelectorDialog selection (now) points to"
        + " a Derivation step with a #LocalRef.";

    public static final String ERRMSG_LOCAL_REF_HAS_SELECTOR_SEARCH = "E-PA-0382"
        + " Theorem %s Step %s: Step Selector Search requested for a Derivation"
        + " step containing a #LocalRef.";

    public static final String ERRMSG_SELECTOR_SEARCH_STEP_NOTFND = "E-PA-0383"
        + " Theorem %s: Step Selector Search requested but the input cursor was"
        + " not positioned on a valid Derivation proof step.";

    public static final String ERRMSG_SELECTOR_CHOICE_STEP_NOTFND = "E-PA-0384"
        + " Theorem %s: StepSelectorDialog selection proof step not found"
        + " in the Proof Worksheet.";

    public static final String ERRMSG_DV_VAR_SCOPE_ERR = "E-PA-0385 Theorem %s:"
        + " Invalid variable symbol in Distinct Variable statement."
        + " Input token = %s is a not an active variable in the current scope.";

    // ----------------------------------------------------------
    // Messages from ProofUnifier.java
    // ----------------------------------------------------------

    public static final String ERRMSG_STEP_REF_HYP_NBR_ERR = "A-PA-0401"
        + " Theorem %s Step %s: Ref assertion invalid for step. The number of"
        + " logical hypotheses is wrong. This is treated as a 'fatal' error"
        + " because the error should have been caught earlier in"
        + " ProofWorksheet.java and should never have gotten this far into the"
        + " system!";

    public static final String ERRMSG_ALT_UNIFY_REFS = "I-PA-0402 Theorem %s"
        + " Step %s: Alternate unification Ref assertions found: %s";

    public static final String ERRMSG_INCOMPLETE_HYPS = "I-PA-0403 Theorem %s"
        + " Step %s: Proof incomplete for derivation proof step."
        + " The step was successfully unified with Ref %s, but one (or more) of"
        + " the step's hypotheses has an incomplete Hyp value (='?').";

    public static final String ERRMSG_ASSRT_SUBST_SLOT = "A-PA-0404"
        + " Theorem %s Step %s: Impossible! Shoot the programmer, there is a is"
        + " a bug in the program. The proof step has an assrtSubst array that"
        + " is missing an empty slot for Hyp number %d";

    public static final String ERRMSG_VERIFY_RECHECK_ERR = "E-PA-0405"
        + " Theorem %s Step %s: Proof and unification completed for step, but a"
        + " recheck of the proof using the Proof Verifier was requested, and it"
        + " is reporting an error. This could reflect a program bug OR it could"
        + " be that this step or an earlier step Ref is wrong and that the"
        + " variable substitutions across multiple steps are inconsistent."
        + " Recheck Ref's OR...use another way to find out the truth: enter the"
        + " Proof Ref's in the metamath.exe Proof Assistant and compare the RPN"
        + " proof it generates with the Proof Assistant's RPN proof... Proof"
        + " Verifier error message follows.";

    public static final String ERRMSG_REARRANGE_HYPS_ERR = "A-PA-0406 Theorem %s"
        + " Step %s: rearrangeHyps() could not find original LogHyp.";

    public static final String ERRMSG_MERGE_LOGHYP_SUBST_ERR = "A-PA-0407"
        + " Theorem %s Step %s: assrtLogHypSubst array element not found in"
        + " MandFrame Hyp Array!";

    public static final String ERRMSG_INIT_FORMULA_SUBST_ERR = "A-PA-0408"
        + " Theorem %s Step %s: assrtFormulaSubst array element not found in"
        + " MandFrame Hyp Array!";

    public static final String ERRMSG_DV_VERIFY_ERR = "%s\nE-PA-0409 Theorem %s"
        + " Step %s: Unification completed for derivation proof step. However,"
        + " the Distinct Variable restrictions specified by the step's Ref"
        + " assertion %s require one or more additional Distinct Variable"
        + " restrictions to be specified on the theorem being proved. You can"
        + " enter $d statements (leave off the '$.') in Proof Assistant to add"
        + " new restrictions temporarily, or modify the input Metamath file."
        + " Specific error details above.";

    public static final String ERRMSG_REF_UNIFY_ERR = "E-PA-0410 Theorem %s"
        + " Step %s: Unification failure in derivation proof step %s."
        + " The step's formula and/or its hypotheses could not be reconciled"
        + " with the referenced Assertion. Try the Unify/Step Selector Search"
        + " to find unifiable assertions for the step.";

    public static final String ERRMSG_STEP_UNIFY_ERR = "E-PA-0411 Theorem %s"
        + " Step %s: Unification failure in derivation proof step. The step's"
        + " formula and/or its hypotheses could not be reconciled with an"
        + " Assertion (axiom or theorem) in the loaded Metamath file(s). Either"
        + " the Unification Search for a unifying Assertion failed due to a"
        + " Not Found condition, or the Unification Search was not attempted"
        + " because the proof step or one of its hypotheses contains Work"
        + " Variables. Note that Unification Search is NOT performed for proof"
        + " steps involving work variables -- you must enter an Assertion label"
        + " for these steps. Try the Unify/Step Selector Search to find"
        + " unifiable assertions for the step.";

    public static final String ERRMSG_UNIFY_SEARCH_EXCLUDE = "I-PA-0412"
        + " Excluded these assertions from Unification search list as requested"
        + " on input RunParm: %s";

    public static final String ERRMSG_UNIFY_TABLES_NOT_INIT = "A-PA-0414"
        + " ProofUnifier tables not initialized prior to use of ProofUnifier."
        + " This is a programming error.";

    public static final String ERRMSG_WV_CLEANUP_SHORTAGE = "E-PA-0415"
        + " Theorem %s. We have run out of dummy variables to assign to Work"
        + " Variables in the final Work Variable cleanup for the proof of the"
        + " theorem. The shortage is for Type Code '%s' for which there are"
        + " only %d unused dummy variables available in the theorem."
        + " To correct the problem it will be necessary to add additional"
        + " variables of this type in the theorem's scope. Or, you can manually"
        + " update the Proof Worksheet, assigning dummy variables to the"
        + " remaining Work Variables (the cleanup algorithm cannot figure out"
        + " when reuse of a dummy variable is permitted across individual proof"
        + " steps, and is sub-optimal in this regard -- a human may perform"
        + " more efficiently in this context :-)";

    public static final String ERRMSG_UPD_WV_ASSIGNED_NULL_VALUE = "A-PA-0416"
        + " A very serious bug!!! Somehow, and this has never before occurred,"
        + " a 'null' value was assigned (somewhere) to a Work Variable that was"
        + " (supposedly) updated! Look in ProofUnifier."
        + "doUpdateDerivationStepWorkVars().";

    public static final String ERRMSG_HYP_STEP_CNT_IN_WORKSHEET_ERROR = "A-PA-0417"
        + "Houston, we have another problem! Somehow the proofWorksheet."
        + "hypStepCnt came to be different than the number of Hyp steps in the"
        + " Proof Worksheet... I am in ConvertWorkVarsToDummyVars() now..."
        + " Row, row, row your boat... Dave? What are you doing, Dave?";

    public static final String ERRMSG_NO_CONVERT_WV = "I-PA-0418 Theorem %s:"
        + " Proof unification was successful, but work variables remain among"
        + " the derivation steps and we were requested to not replace these"
        + " with dummy variables. Choose \"Unify\" instead of \"Unify+Don't"
        + " convert WorkVars\" to automatically assign dummy variables to the"
        + " remaining WorkVars.";

    // ----------------------------------------------------------
    // Messages from ProofAsstPreferences.java
    // ----------------------------------------------------------

    public static final String ERRMSG_INVALID_FONT_FAMILY_NAME = "E-PA-0501"
        + " Invalid input Font Family Name = %s";

    public static final String ERRMSG_INVALID_SOFT_DJ_VARS_ERROR_OPTION = ""
        + "E-PA-0502 Invalid option input = %s for ProofAsstDjVarsSoftErrors"
        + " RunParm. Choices are: 'Ignore', 'Report', 'GenerateNew',"
        + " 'GenerateReplacements', and 'GenerateDifferences'.";

    public static final String ERRMSG_INVALID_SOFT_DJ_ERROR_OPTION_NBR = ""
        + "E-PA-0503 Invalid Soft Dj Vars Error Option Number = %s";

    public static final String ERRMSG_INVALID_INCOMPLETE_STEP_CURSOR = "E-PA-0504"
        + " Invalid option input = %s for ProofAsstIncompleteStepCursor"
        + " RunParm. Choices are: 'First', 'Last' (the default), and 'AsIs'.";

    public static final String ERRMSG_INVALID_INCOMPLETE_STEP_CURSOR_OPTION_NBR = ""
        + "E-PA-0505 Invalid Incomplete Step Cursor Option Number = %s";

    public static final String ERRMSG_INVALID_STEP_SELECTOR_MAX_RESULTS_NBR = ""
        + "E-PA-0506 Invalid StepSelectorMaxResults Number = %s. Must be"
        + " between (inclusive) 1 and %d";

    public static final String ERRMSG_INVALID_BOOLEAN = "E-PA-0507"
        + " Invalid %s option = %s. Must equal 'yes', 'no', 'on', 'off',"
        + " 'true' or 'false'.";

    public static final String ERRMSG_INVALID_PROOF_FORMAT = "E-PA-0508"
        + " Invalid option input = %s for ProofAsstProofFormat RunParm."
        + " Choices are: 'Normal', 'Packed', and 'Compressed'.";

    public static final String ERRMSG_SET_LOOK_AND_FEEL = "E-PA-0509"
        + " Unable to set Look and Feel option '%s'. Available options are %s.";

    public static final String ERRMSG_LOOK_AND_FEEL_MISSING = "E-PA-0510"
        + " Look and Feel option '%s' does not exist. Available options are %s.";

    // ----------------------------------------------------------
    // Messages from DerivationStep.java
    // ----------------------------------------------------------

    public static final String ERRMSG_SUBST_TO_VARS_NOT_DJ = "E-PA-0601"
        + " DerivationStep %s substitution (to) vars subject to DjVars"
        + " restriction by proof step but not listed as DjVars in theorem to be"
        + " proved: %s";

    // ----------------------------------------------------------
    // Messages from StepUnifier.java
    // ----------------------------------------------------------

    public static final String ERRMSG_ADD_TO_APPLIED_ARRAY_OFLOW = "A-PA-0701"
        + " Severe Problem! Maximum size of 'applied' array exceeded in"
        + " StepUnifier.addToAppliedArray(). This might *not* be a bug(?), but"
        + " it will require a recompile of the source code, at least.";

    // ----------------------------------------------------------
    // Messages from ProofAsstCursor.java
    // ----------------------------------------------------------

    public static final String ERRMSG_PA_CURSOR_INSTRUMENTATION = "E-PA-0801"
        + " Theorem %s: Output Cursor at Stmt: %s fieldId: %d"
        + " caretCharNbr: %d caretLine: %d caretCol: %d";

    // ----------------------------------------------------------
    // Messages from StepSelectorSearch.java
    // ----------------------------------------------------------

    public static final String ERRMSG_SELECTOR_SEARCH_NULL_PARSE_TREE = "A-PA-0901"
        + "Step %s: Null parse tree for Hyp's formula,"
        + " this should have been caught!";

    public static final String ERRMSG_SELECTOR_SEARCH_ASSRT_LIST_EMPTY = "A-PA-0902"
        + "Input list of assertions (for unification) empty!";

    // ----------------------------------------------------------
    // Messages from EraseWffsPreprocessRequest.java
    // ----------------------------------------------------------

    public static final String ERRMSG_ERASE_WFFS_ERROR = "A-PA-1001"
        + "Unable to process Proof text area during preprocessing edit"
        + " operation. Specific error message follows: %s";

}
