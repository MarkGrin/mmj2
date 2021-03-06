//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ParseNode.java  0.09 03/01/2008
 *
 * Sep-30-2005: use getMandHypArrayLength() instead of
 *              getMandHypArray().
 *
 * Jan-6-2006:  - added calcMaxDepth() for Proof Assistant.
 *              - added new non-recursive isDeepDup() and
 *                 unifyWithSubtree().
 * Mar-5-2006:  - misc. comments.
 *              - unifyWithSubtree() has hardcoded msg needs
 *                fixin'.
 * Aug-27-2006: - added renderParsedSubExpr() for TMFF project
 *
 * Jun-01-2007: - version 0.06
 *     --> efficiency and logic tweaks in
 *         mmj.lang.ParseNode.java#unifyWithSubtree() and
 *         mmj.lang.ParseNode.java#isDeepDup()
 *
 * Aug-1-2007  - version 0.07
 *     --> misc. Work Var Enhancements:
 *         -- added cloneNodeToUseWorkVars()
 *         -- changed "stmt" and "child" to public
 *            because the inconvenience is intolerable.
 *         -- Modified deepCloneApplyingAssrtSubst() to maintain
 *            workVarList instead of keeping count of "dummy"
 *            objects inserted.
 *
 * Feb-1-2008  - version 0.08
 *         -- Add convertToRPN() version of converting
 *            just a sub-tree (not an entire tree)
 *            to RPN.
 *
 * Apr-1-2008 -- version 0.09
 *         -- Clone deepCloneApplyingAssrtSubst()
 *            so that input variable "workVarList" is
 *            optional, and if null will not be used.
 */

package mmj.lang;

import java.util.*;

import mmj.lang.ParseTree.RPNStep;

/**
 * Parse Node is an n-way tree node for Metamath Stmt trees.
 * <p>
 * ParseNode is dual use, and provides the tree node structure for
 * grammatical/syntax parses -- these parse trees can be readily converted to
 * Reverse Polish Notation (see Stmt.exprRPN) -- and for proof trees, which are
 * normally stored in Metamath .mm files in RPN format but can easily be
 * converted to ParseTrees.
 * <p>
 * The difference between proof and grammatical parse trees is content and
 * purpose, there is no structural difference.
 * <p>
 * Proof step Stmt references are not restricted to Syntax Axiom and Variable
 * Hypotheses, and can contain references to Logical Hypotheses, Logical Axioms
 * and other Theorems. Logical Axioms and Theorems have Frames (see ScopeFrame
 * and OptFrame) whose hypArrays contain Variable Hypotheses AND Logical
 * Hypotheses, therefore there may be more children nodes under an Assertion's
 * ParseNode than there are under a Syntax Axiom's ParseNode. Both proof parse
 * trees and grammatical parse trees are required to reproduce/generate the
 * original Statement's Formula's Expression (the 2nd through nth Symbols of the
 * Formula), but a Proof is, in addition required to produce the entire Formula,
 * which includes the Type Code and the Expression.
 * <p>
 * Thus, the difference between a proof and a grammatical parse is that a proof
 * derives a theorem's Formula from Axioms with the same Type Code as the
 * statement being proved; a grammatical parse is not under this obligation (it
 * must generate some Type Code however!)
 * <p>
 * Also:
 * <ul>
 * <li>Grammatical parse trees contain Stmt references to either Syntax Axioms
 * or Variable Hypotheses. The children of a Syntax Axiom (which is an Assrt)
 * are either VarHyp's or Syntax Axioms. The number of children of a Syntax
 * Axiom node is equal to the size of the Syntax Axiom's Assrt.varHypArray --
 * which for Syntax Axioms is the same as the Syntax Axiom's
 * Assrt.MandFrame.hypArray varHyp, because a Syntax Axiom has no Logical
 * Hypotheses.</li>
 * <li>Proof Trees contain Stmt references to Assertions (Assrt, which includes
 * Axiom and Theorem), and Hypotheses (Hyp, which includes VarHyp and LogHyp.)
 * The children of an Assertion Proof Tree Node correspond to the
 * Assrt.MandFrame.hypArray, one to one in database sequence. The children of a
 * Hypothesis Proof Tree node depend on whether the Hypothesis is a Logical
 * Hypothesis (LogHyp) or a Variable Hypothesis (VarHyp); if a LogHyp, then the
 * children correspond to the LogHyp.varHypArray, while if a VarHyp there is
 * only one child (even a VarHyp node represents an Expression for substitution
 * in the proof and may have children of its own -- for example, a VarHyp
 * sub-node of a ProofTree may actually contain a Stmt reference to "wi" where
 * the "wi" statement has its own VarHyp's.)</li>
 */

public class ParseNode {

    /**
     * Stmt object reference, either a Hyp or an Assrt.
     */
    public Stmt stmt;

    /**
     * Child node array of length 0 to n.
     */
    public ParseNode[] child;

    /**
     * Cached size of node subtree.
     */
    private int size;

    /**
     * Cached expanded size of node subtree.
     */
    private int sizeExpanded;

    /**
     * This temporary variable is 0 if the node has one parent, -1 if the node
     * has multiple parents, and a positive integer when the RPN index of the
     * first parent is known.
     */
    public int firstAppearance;

    /**
     * Default constructor.
     */
    public ParseNode() {}

    /**
     * Construct with a Stmt.
     * 
     * @param stmt a proof step.
     */
    public ParseNode(final Stmt stmt) {
        this.stmt = stmt;
        child = null;
    }

    /**
     * Construct using a VarHyp.
     * 
     * @param varHyp a proof or parse step.
     */
    public ParseNode(final VarHyp varHyp) {
        stmt = varHyp;
        child = new ParseNode[0];
    }

    /**
     * Return ParseNode's Stmt.
     * 
     * @return stmt (could be null depending on the context).
     */
    public Stmt getStmt() {
        return stmt;
    }

    /**
     * Set ParseNode's Stmt.
     * 
     * @param stmt a proof or parse step.
     */
    public void setStmt(final Stmt stmt) {
        this.stmt = stmt;
    }

    /**
     * Return ParseNode's Child ParseNode array.
     * 
     * @return child ParseNode array.
     */
    public ParseNode[] getChild() {
        return child;
    }

    /**
     * Return ParseNode's Child ParseNode array.
     * 
     * @param child ParseNode array.
     */
    public void setChild(final ParseNode[] child) {
        this.child = child;
    }

    /**
     * Unify an input parse subtree (expression) with this subtree and return an
     * array of substitions if successful, or null. Using fixed size work stacks
     * here, set to 1000 in Proof Assistant. Hard to imagine exceeding the size
     * so we'll accept the possibility of array out-of-bounds exception and just
     * recompile if needed (for now... see
     * mmj.pa.PaConstants.UNIFIER_NODE_STACK_SIZE ).
     * 
     * @param subtreeRoot root of parse subtree to unify with this
     * @param varHypArray the VarHyp's in this subtree
     * @param unifyNodeStack fixed size work stack
     * @param compareNodeStack fixed size work stack
     * @return array of subtrees that represent substitutions for the
     *         corresponding VarHyps in the input varHypArray (may contain
     *         nulls).
     */
    public ParseNode[] unifyWithSubtree(final ParseNode subtreeRoot,
        final VarHyp[] varHypArray, final ParseNode[] unifyNodeStack,
        final ParseNode[] compareNodeStack)
    {

        unifyNodeStack[0] = this;
        unifyNodeStack[1] = subtreeRoot;

        int stackCnt = 2;

        final ParseNode[] substArray = new ParseNode[varHypArray.length];

        ParseNode myNode;
        ParseNode subtreeNode;

        stackLoop: while (stackCnt > 0) {
            subtreeNode = unifyNodeStack[--stackCnt];
            myNode = unifyNodeStack[--stackCnt];

            if (myNode.stmt != subtreeNode.stmt) {
                if (!(myNode.stmt instanceof VarHyp)
                    || myNode.stmt.getTyp() != subtreeNode.stmt.getTyp())
                    // mismatch and/or myNode not VarHyp
                    return null;
            }
            // ok, matching syntax nodes
            else if (!(myNode.stmt instanceof VarHyp)) {
                for (int i = myNode.child.length - 1; i >= 0; i--) {
                    unifyNodeStack[stackCnt++] = myNode.child[i];
                    unifyNodeStack[stackCnt++] = subtreeNode.child[i];
                }
                continue;
            }

            // ok, accum the subst while checking for (erroneous)
            // two different subst values for a single VarHyp
            for (int i = 0; i < varHypArray.length; i++)
                if (varHypArray[i] == myNode.stmt) {
                    if (substArray[i] == null)
                        substArray[i] = subtreeNode;
                    else if (!substArray[i].isDeepDup(subtreeNode,
                        compareNodeStack))
                        return null; // bad subst, 2 diff values
                    continue stackLoop; // next!!!
                }

            // if we're here then we messed up!
            String msg = "", delim = "";
            for (final VarHyp element : varHypArray) {
                msg += delim + element.getLabel();
                delim = " ";
            }
            throw new IllegalArgumentException(LangException.format(
                LangConstants.ERRMSG_UNIFY_SUBST_HYP_NOTFND,
                myNode.stmt.getLabel(), msg));
        }
        return substArray;
    }

    public int unifyWithSubtree(final ParseNode parsenode,
        final ParseNode[] nodeStack, final ParseNode[] otherStack,
        final VarHypSubst[] subtree)
    {
        nodeStack[0] = this;
        nodeStack[1] = parsenode;
        int count = 0;
        stackLoop: for (int stack = 2; stack > 0; count++) {
            final ParseNode thatNode = nodeStack[--stack];
            final ParseNode myNode = nodeStack[--stack];
            if (myNode.stmt != thatNode.stmt) {
                if (!(myNode.stmt instanceof VarHyp)
                    || myNode.stmt.getTyp() != thatNode.stmt.getTyp())
                    return -1;
            }
            else if (!(myNode.stmt instanceof VarHyp)) {
                for (int i = myNode.child.length - 1; i >= 0; i--) {
                    nodeStack[stack++] = myNode.child[i];
                    nodeStack[stack++] = thatNode.child[i];
                }
                continue;
            }
            for (int i = 0; i < count; i++)
                if (subtree[i].targetVarHyp == myNode.stmt) {
                    if (!subtree[i].sourceNode.isDeepDup(thatNode, otherStack))
                        return -1;
                    continue stackLoop;
                }
            subtree[count].targetVarHyp = (VarHyp)myNode.stmt;
            subtree[count].sourceNode = thatNode;
        }
        return count;
    }
    public SubTreeIterator subTreeIterator(final boolean excludeVarHyps) {
        return new SubTreeIterator(this, excludeVarHyps);
    }

    /**
     * Determine if sub-tree is a duplicate of this sub-tree. This is the
     * non-recursive version of isDeepDup(), which can be used equivalently
     * instead.
     * 
     * @param that ParseNode to compare to this ParseNode.
     * @param compareNodeStack fixed length work stack for ParseNodes.
     * @return true if the sub-trees have identical contents.
     */
    public boolean isDeepDup(final ParseNode that,
        final ParseNode[] compareNodeStack)
    {

        ParseNode myNode = this;
        ParseNode thatNode = that;

        int stackCnt = 0;
        while (thatNode != null && myNode.stmt == thatNode.stmt
            && myNode.child.length == thatNode.child.length)
        {
            int i = myNode.child.length - 1;
            if (i < 0) {
                if (stackCnt <= 0)
                    return true;
                thatNode = compareNodeStack[--stackCnt];
                myNode = compareNodeStack[--stackCnt];
            }
            else {
                while (i > 0) {
                    compareNodeStack[stackCnt++] = myNode.child[i];
                    compareNodeStack[stackCnt++] = thatNode.child[i];
                    i--;
                }
                thatNode = thatNode.child[0];
                myNode = myNode.child[0];
            }
        }
        return false;
    }

    /**
     * Determine if sub-tree is a duplicate of this sub-tree.
     * 
     * @param that ParseNode to compare to this ParseNode.
     * @return true if the sub-trees have identical contents.
     */
    public boolean isDeepDup(final ParseNode that) {
        if (that == null || stmt != that.stmt
            || child.length != that.child.length)
            return false;
        if (this == that)
            return true;
        for (int i = 0; i < child.length; i++)
            if (!child[i].isDeepDup(that.child[i]))
                return false;
        return true;
    }

    /**
     * Clones a ParseNode while substituting for any child nodes that match the
     * corresponding matchNode array node. see
     * GrammarRule.buildGrammaticalParseNode for an example. (this is kind of
     * ugly to look at!)
     * 
     * @param matchNode ParseNode array of matching ParseNodes.
     * @param expandedReseqParam ParseNodeHolder array of nodes to splice in to
     *            the original tree where a matchNode is found.
     * @return deep clone of original node with substituted child nodes.
     */
    public ParseNode deepCloneWithGrammarHypSubs(final ParseNode[] matchNode,
        final ParseNodeHolder[] expandedReseqParam)
    {
        final ParseNode out = new ParseNode(stmt);
        out.child = new ParseNode[expandedReseqParam.length];
        for (int i = 0; i < out.child.length; i++)
            if (expandedReseqParam[i] == null)
                out.child[i] = child[i].deepClone();
            else
                out.child[i] = child[i].deepCloneWNodeSub(matchNode[i],
                    expandedReseqParam[i].parseNode);
        return out;
    }

    /**
     * Clones a sub-tree and splices in a substitution node when a given
     * "matchNode" is found.
     * <p>
     * Basically, this is used to splice an expression's sub-tree into a VarHyp
     * sub-tree. Note that the original tree is on top, so we're layering
     * another formula on top, with substituted expressions replacing the
     * formula's hypotheses.
     * 
     * @param matchNode ParseNode to look for and replace.
     * @param substNode ParseNode to replace matchNode.
     * @return output ParseNode, unchanged or substituted.
     */
    public ParseNode deepCloneWNodeSub(final ParseNode matchNode,
        final ParseNode substNode)
    {
        if (this == matchNode)
            return substNode;
        final ParseNode out = new ParseNode(stmt);
        if (child != null) {
            out.child = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++)
                out.child[i] = child[i].deepCloneWNodeSub(matchNode, substNode);
        }
        return out;
    }

    /**
     * Deep clone of a ParseNode sub-tree.
     * 
     * @return ParseNode sub-tree matching the original's content.
     */
    public ParseNode deepClone() {
        final ParseNode out = new ParseNode();
        out.stmt = stmt;
        out.child = new ParseNode[child.length];
        for (int i = 0; i < child.length; i++)
            out.child[i] = child[i].deepClone();
        return out;
    }

    /**
     * (Deep) Clone a ParseNode while substituting a set of VarHyp substitutions
     * specified by a parallel Hyp array and keeping track of the Work Vars
     * output.
     * <p>
     * This function is a helper for mmj.pa.ProofUnifier and its friend
     * mmj.pa.ProofWorksheet.
     * 
     * @param assrtHypArray parallel array for assrtSubst
     * @param assrtSubst array of ParseNode sub-tree roots specifying hyp
     *            substitutions.
     * @param workVarList arrayList of WorkVar updated to contain set of Work
     *            Vars used in the subtree. substituted into the output.
     * @return new ParseNode.
     */
    public ParseNode deepCloneApplyingAssrtSubst(final Hyp[] assrtHypArray,
        final ParseNode[] assrtSubst, final List<WorkVar> workVarList)
    {
        if (!(stmt instanceof VarHyp)) {
            final ParseNode out = new ParseNode(stmt);
            out.child = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++)
                out.child[i] = child[i].deepCloneApplyingAssrtSubst(
                    assrtHypArray, assrtSubst, workVarList);
            return out;
        }

        for (int i = 0; i < assrtHypArray.length; i++)
            if (assrtHypArray[i] == stmt) {
                assrtSubst[i].accumSetOfWorkVarsUsed(workVarList);
                return assrtSubst[i];
            }

        throw new IllegalArgumentException(LangException.format(
            LangConstants.ERRMSG_ASSRT_SUBST_HYP_NOTFND, stmt.getLabel()));
    }

    /**
     * (Deep) Clone a ParseNode while substituting a set of VarHyp substitutions
     * specified by a parallel Hyp array.
     * 
     * @param assrtHypArray parallel array for assrtSubst
     * @param assrtSubst array of ParseNode sub-tree roots specifying hyp
     *            substitutions.
     * @return new ParseNode.
     */
    public ParseNode deepCloneApplyingAssrtSubst(final Hyp[] assrtHypArray,
        final ParseNode[] assrtSubst)
    {
        if (!(stmt instanceof VarHyp)) {
            final ParseNode out = new ParseNode(stmt);
            out.child = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++)
                out.child[i] = child[i].deepCloneApplyingAssrtSubst(
                    assrtHypArray, assrtSubst);
            return out;
        }

        for (int i = 0; i < assrtHypArray.length; i++)
            if (assrtHypArray[i] == stmt)
                return assrtSubst[i];

        throw new IllegalArgumentException(LangException.format(
            LangConstants.ERRMSG_ASSRT_SUBST_HYP_NOTFND, stmt.getLabel()));
    }

    /**
     * Finds *first* VarHyp node in a sub-tree.
     * <p>
     * This is useful mainly for GrammarRule parse trees which have at most one
     * VarHyp per ParseTree.root.child[i].
     * 
     * @return first ParseNode among sub-trees of a ParseNode's children that is
     *         a VarHyp.
     */
    public ParseNode findFirstVarHypNode() {
        if (stmt instanceof VarHyp)
            return this;
        ParseNode out;
        for (final ParseNode element : child) {
            out = element.findFirstVarHypNode();
            if (out != null)
                return out;
        }
        return null;
    }

    /**
     * Calculates the maximum depth of a parse sub-tree.
     * 
     * @return maximum depth of parse sub-tree
     */
    public int calcMaxDepth() {
        if (stmt instanceof WorkVarHyp)
            return 0;
        int childMaxDepth = 0;
        int childDepth;
        for (final ParseNode element : child)
            if ((childDepth = element.calcMaxDepth()) > childMaxDepth)
                childMaxDepth = childDepth;
        return childMaxDepth + 1;
    }

    /**
     * Counts nodes in a ParseNode sub-tree, and initializes the
     * {@link #firstAppearance} field.
     * 
     * @param expanded {@code true} if repeated subtrees are to be counted as
     *            fully expanded, {@code false} to count them as stubs of size 1
     * @return the number of nodes
     */
    public int countParseNodes(final boolean expanded) {
        resetAppearances();
        getCounts();
        return expanded ? sizeExpanded : size;
    }

    private void resetAppearances() {
        if (child != null)
            for (final ParseNode n : child)
                n.resetAppearances();
        firstAppearance = 0;
        size = 0;
    }

    private void getCounts() {
        if (size != 0)
            return;
        size = 1;
        sizeExpanded = 1;
        if (child != null)
            for (final ParseNode element : child) {
                element.getCounts();
                sizeExpanded += element.sizeExpanded;
                if (element.firstAppearance >= 0) {
                    element.firstAppearance = -1;
                    size += element.size;
                }
                else {
                    element.firstAppearance = -2;
                    size++;
                }
            }
    }

    public boolean squishTree(final List<ParseNode> encountered) {
        boolean modified = false;
        for (int i = 0; i < child.length; i++) {
            modified |= child[i].squishTree(encountered);
            boolean found = false;
            for (final ParseNode n : encountered)
                if (child[i].isDeepDup(n)) {
                    found = true;
                    if (child[i] != n) {
                        child[i] = n;
                        modified = true;
                    }
                    break;
                }
            if (!found)
                encountered.add(child[i]);
        }
        if (modified)
            size = sizeExpanded = 0;
        return modified;
    }

    /**
     * Converts a sub-tree expression to Reverse Polish Notation.
     * <p>
     * Intended for creating the RPN for an expression rather than an entire
     * formula (with ParseTree).
     * 
     * @param pressLeaf {@code true} if nodes with no children should be
     *            backreferenced
     * @return RPN Stmt array.
     */
    public RPNStep[] convertToRPN(final boolean pressLeaf) {
        final RPNStep[] outRPN = new RPNStep[countParseNodes(false)];

        final int[] dat = new int[2];
        convertToRPN(pressLeaf, outRPN, dat);
        if (dat[0] != outRPN.length)
            throw new IllegalStateException(LangException.format(
                LangConstants.ERRMSG_SUBTREE_CONV_TO_RPN_FAILURE, outRPN.length
                    - dat[0]));
        return outRPN;
    }

    public void convertToRPN(final boolean pressLeaf, final RPNStep[] list,
        final int[] dat)
    {
        if (firstAppearance > 0) {
            final RPNStep s = new RPNStep(null);
            s.backRef = firstAppearance;
            list[dat[0]++] = s;
        }
        else {
            if (child != null)
                for (final ParseNode n : child)
                    n.convertToRPN(pressLeaf, list, dat);
            final RPNStep s = new RPNStep(stmt);
            if (firstAppearance == -2
                && (pressLeaf || child != null && child.length > 0))
                s.backRef = -(firstAppearance = ++dat[1]);
            list[dat[0]++] = s;
        }
    }

    /**
     * Converts a sub-tree to Reverse Polish Notation.
     * 
     * @param outRPN Stmt Array where RPN will be stored.
     * @param dest location in output array to write the next Stmt reference.
     * @return dest of *next* output Stmt array item.
     */
    public int convertToRPNExpanded(final RPNStep[] outRPN, int dest) {
        if (child != null)
            for (final ParseNode n : child)
                dest = n.convertToRPNExpanded(outRPN, dest);

        outRPN[dest++] = new RPNStep(stmt);
        return dest;
    }

    /**
     * Converts a parse sub-tree into a sub-expression which is output into a
     * String Buffer.
     * <p>
     * Note: this will not work for a proof node! The ParseNode's stmt must be a
     * VarHyp or a Syntax Axiom.
     * <p>
     * The output sub-expression is generated into text not to exceed the given
     * maxLength. If the number of output characters exceeds maxLength output
     * terminates after tidying StringBuilder.
     * <p>
     * The depth of the sub-tree is checked against the input maxDepth
     * parameter, and if the depth exceeds this number, output terminates after
     * tidying StringBuilder.
     * <p>
     * Depth is computed as 1 for each Notation Syntax Axiom Node. VarHyp nodes
     * and Nulls Permitted, Type Conversion and NamedTypedConstant Syntax Axiom
     * nodes are assigned depth = 0 for purposes of depth checking.
     * 
     * @param sb StringBuilder already initialized for appending characters.
     * @param maxDepth maximum depth of Notation Syntax axioms in sub-tree to be
     *            printed. Set to Integer.MAX_VALUE to turn off depth checking.
     * @param maxLength maximum length of output sub-expression. Set to
     *            Integer.MAX_VALUE to turn off depth checking.
     * @return length of sub-expression characters appended to the input
     *         StringBuilder -- or -1 if maxDepth or maxLength exceeded.
     */
    public int renderParsedSubExpr(final StringBuilder sb, final int maxDepth,
        final int maxLength)
    {

        final int rollbackToLength = sb.length();
        final int outputSubExprLength = stmt.renderParsedSubExpr(sb, maxDepth,
            maxLength, child);
        if (outputSubExprLength < 0)
            sb.setLength(rollbackToLength);

        return outputSubExprLength;
    }

    /**
     * Deep clone of a ParseNode sub-tree converting Target Variables to Source
     * Variables.
     * <p>
     * Note: this is used by UnifyWorkManager and only for "raw" substitutions
     * which means that any variables in the tree being cloned are, by
     * definition, target variables; we do not expect any of them to be work
     * variables, and furthermore, we expect all of them to have assigned
     * values.
     * 
     * @return ParseNode sub-tree converted to use WorkVarHyps.
     */
    public ParseNode cloneTargetToSourceVars() {

        final ParseNode out = new ParseNode();

        if (stmt instanceof VarHyp) {
            final ParseNode vHNode = ((VarHyp)stmt).paSubst;
            if (vHNode == null)
                throw new IllegalArgumentException(
                    LangConstants.ERRMSG_NULL_TARGET_VAR_HYP_PA_SUBST);
            out.stmt = vHNode.stmt;
            out.child = vHNode.child;
        }
        else {
            out.stmt = stmt;
            out.child = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++)
                out.child[i] = child[i].cloneTargetToSourceVars();
        }
        return out;
    }

    /**
     * Check to see if or how the input searchWorkVarHyp occurs within the
     * current ParseNode stmt and its subtree and any substitutions made to
     * VarHyps via paSubst.
     * <p>
     * Note that this function is only called if the searchWorkVarHyp.paSubst ==
     * null (which means we are considering assigning a substitution). to it.
     * AND note that it is called only if the currentNode.stmt is a WorkVarHyp.
     * <p>
     * The reason for this hokeyness is that set.mm contains loops of renames,
     * such as &W1 := & W3 := &W1, etc. Not to mention &W1 := &W2 := &W3 := &W1.
     * THESE are "ok" as long as the subtree depth remains 1 (e.g. not &W1 := (
     * &W2 -> &W1 ).
     * 
     * @param searchWorkVarHyp the object of the search.
     * @return occurs Type: 0 = no occurrences, 1 = occurs in error (invalid
     *         loop) -1 = valid rename
     */
    public int checkWorkVarHasOccursIn(final WorkVarHyp searchWorkVarHyp) {

        if (stmt instanceof WorkVarHyp) {

            final Stmt targetStmt = checkWorkVarHasOccursInValidRename(searchWorkVarHyp);

            if (targetStmt == null)
                return LangConstants.WV_OCCURS_IN_NOT_AT_ALL;

            if (targetStmt == searchWorkVarHyp)
                return LangConstants.WV_OCCURS_IN_RENAME_LOOP;

            // else...found a "->" or "ph" or other non-WorkVarHyp

        }
        if (hasOccursIn(searchWorkVarHyp))
            return LangConstants.WV_OCCURS_IN_ERROR;
        else
            return LangConstants.WV_OCCURS_IN_NOT_AT_ALL;
    }

    private Stmt checkWorkVarHasOccursInValidRename(
        final WorkVarHyp searchWorkVarHyp)
    {
        if (stmt == searchWorkVarHyp // valid rename
            || !(stmt instanceof WorkVarHyp))
            return stmt;

        if (((VarHyp)stmt).paSubst == null)
            return null; // found nothing

        return ((VarHyp)stmt).paSubst
            .checkWorkVarHasOccursInValidRename(searchWorkVarHyp);
    }

    /**
     * Looks for an occurrence of a given WorkVarHyp within a subtree.
     * <p>
     * This is used in UnifyWorkManager to perform the "occurs in" check used by
     * Robinson's unification algorithm.
     * 
     * @param searchWorkVarHyp is what we are looking for.
     * @return true iff input searchStmt found in subtree.
     */
    private boolean hasOccursIn(final WorkVarHyp searchWorkVarHyp) {
        if (searchWorkVarHyp == stmt)
            return true;
        if (stmt instanceof WorkVarHyp)
            return ((VarHyp)stmt).paSubst != null
                && ((VarHyp)stmt).paSubst.hasOccursIn(searchWorkVarHyp);
        for (final ParseNode element : child)
            if (element.hasOccursIn(searchWorkVarHyp))
                return true;
        return false;
    }

    /**
     * Returns true if subtree contains a WorkVar which has a non-null assigned
     * substitution update.
     * 
     * @return true if subtree contains an updated WorkVar.
     */
    public boolean hasUpdatedWorkVar() {
        if (stmt instanceof WorkVarHyp && ((VarHyp)stmt).paSubst != null)
            return true;
        for (final ParseNode element : child)
            if (element.hasUpdatedWorkVar())
                return true;
        return false;
    }

    /**
     * Clone subtree replacing any updated Work Vars with clones of their
     * updating subtrees.
     * 
     * @return cloned subtree containing no Work Vars which have updates.
     */
    public ParseNode cloneResolvingUpdatedWorkVars() {
        if (stmt instanceof WorkVarHyp)
            return ((VarHyp)stmt).paSubst == null ? new ParseNode((VarHyp)stmt)
                : ((VarHyp)stmt).paSubst.cloneResolvingUpdatedWorkVars();
        final ParseNode out = new ParseNode(stmt);
        out.child = new ParseNode[child.length];
        for (int i = 0; i < child.length; i++)
            out.child[i] = child[i].cloneResolvingUpdatedWorkVars();
        return out;
    }

    /**
     * Updates an ArrayList to maintain a set of Work Vars used in a subtree.
     * 
     * @param workVarList List of WorkVar objects in subtree.
     */
    public void accumSetOfWorkVarsUsed(final List<WorkVar> workVarList) {
        if (stmt instanceof WorkVarHyp) {
            final WorkVar v = ((WorkVarHyp)stmt).getWorkVar();
            for (int i = 0; i < workVarList.size(); i++)
                if (workVarList.get(i) == v)
                    return;
            workVarList.add(v);
        }
        else
            for (final ParseNode element : child)
                element.accumSetOfWorkVarsUsed(workVarList);
    }

    /**
     * Updates an ArrayList to maintain a set of Var Hyps used in a subtree.
     * 
     * @param varHypList List of VarHyp objects in subtree.
     */
    public void accumVarHypUsedListBySeq(final List<? super VarHyp> varHypList)
    {
        if (stmt instanceof VarHyp)
            ((VarHyp)stmt).accumVarHypListBySeq(varHypList);
        else
            for (final ParseNode element : child)
                element.accumVarHypUsedListBySeq(varHypList);
    }

    /**
     * Accumulate Var Hyps used in the subtree which are also in an input list
     * of Var Hyps.
     * <p>
     * Lists are maintained without duplicates and are in ascending database
     * sequence.
     * <p>
     * Note: in ProofUnifier this is used to accumulate a list of optional
     * variables that are in use in a proof.
     * 
     * @param optionalVarHypList List of Var Hyps being sought for accumulation.
     * @param optionalVarHypsInUseList List of Var Hyps accumulated which are in
     *            the formula and are in the input varList.
     */
    public void accumListVarHypUsedListBySeq(
        final List<VarHyp> optionalVarHypList,
        final List<VarHyp> optionalVarHypsInUseList)
    {

        VarHyp vH;
        if (stmt instanceof VarHyp) {
            vH = (VarHyp)stmt;
            if (vH.containedInVarHypListBySeq(optionalVarHypList))
                vH.accumVarHypListBySeq(optionalVarHypsInUseList);
        }
        else
            for (final ParseNode element : child)
                element.accumListVarHypUsedListBySeq(optionalVarHypList,
                    optionalVarHypsInUseList);
    }

    /**
     * (Deep) Clone a ParseNode while applying updates to WorkVars.
     * <p>
     * This function is a helper for mmj.pa.ProofUnifier and its friend
     * mmj.pa.ProofWorksheet.
     * 
     * @return new ParseNode subtree.
     */
    public ParseNode deepCloneApplyingWorkVarUpdates() {

        if (stmt instanceof WorkVarHyp && ((VarHyp)stmt).paSubst != null)
            return ((VarHyp)stmt).paSubst;

        final ParseNode out = new ParseNode(stmt);
        out.child = new ParseNode[child.length];
        for (int i = 0; i < child.length; i++)
            out.child[i] = child[i].deepCloneApplyingWorkVarUpdates();
        return out;
    }

    public class SubTreeIterator implements Iterator<ParseNode> {

        public boolean hasNext() {
            return !nodeStack.empty();
        }

        public ParseNode next() {
            final ParseNode parsenode = nodeStack.pop();
            for (int i = parsenode.child.length - 1; i >= 0; i--)
                pushIfNotExcluded(parsenode.child[i]);

            return parsenode;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void pushIfNotExcluded(final ParseNode parsenode) {
            if (!(excludeVarHyps && parsenode.stmt instanceof VarHyp))
                nodeStack.push(parsenode);
        }

        Stack<ParseNode> nodeStack;
        boolean excludeVarHyps;

        public SubTreeIterator(final ParseNode parsenode1,
            final boolean excludeVarHyps)
        {
            this.excludeVarHyps = excludeVarHyps;
            nodeStack = new Stack<ParseNode>();
            pushIfNotExcluded(parsenode1);
        }
    }

    @Override
    public String toString() {
        return child == null || child.length == 0 ? stmt.toString() : Arrays
            .toString(child) + "; " + stmt;
    }
}
