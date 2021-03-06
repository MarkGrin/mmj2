//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * VerifyException.java  0.02 08/23/2005
 */

package mmj.lang;

/**
 * Thrown by package mmj.verify methods when a verification error is detected in
 * LogicalSystem.
 */
public class VerifyException extends LangException {

    /**
     * Default Constructor, {@code VerifyException}.
     */
    public VerifyException() {
        super();
    }

    /**
     * Contructor, {@code VerifyException} with error message.
     * 
     * @param errorMessage error message.
     * @param args formatting arguments.
     */
    public VerifyException(final String errorMessage, final Object... args) {
        super(errorMessage, args);
    }
}
