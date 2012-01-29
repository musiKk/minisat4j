/*
 * Copyright (c) 2009, Werner Hahn
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ONANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.musikk.minisat4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a variable for the constraint solver. Variables are parts of
 * clauses. Every variable consists in a positive form with negative forms
 * created as they are needed.
 * 
 * @author Werner Hahn
 * @version 0.1
 * 
 */
public class Variable {

	/**
	 * The variable that keeps track of the number the next variable gets
	 * assigned to. This is needed because minisat needs all variables to be
	 * consecutive starting from 1 (one).
	 */
	private static int currentVariableNumber;

	/**
	 * This Map maps variable numbers to actual variables.
	 */
	private final static Map<Integer, Variable> variables;

	/**
	 * This Map maps variables to the result (if there is any).
	 */
	private final static Map<Variable, Boolean> results;

	/**
	 * The number of this Variable instance.
	 */
	private final int variableNumber;

	/**
	 * True if the Variable is to be used negative.
	 */
	private final boolean negative;

	static {
		results = new HashMap<Variable, Boolean>();
		variables = new HashMap<Integer, Variable>();
		currentVariableNumber = 1;
	}

	/**
	 * Creates and returns a new Variable.
	 * 
	 * @return
	 */
	public static Variable getVariable() {
		Variable v = new Variable(currentVariableNumber);
		variables.put(currentVariableNumber, v);
		currentVariableNumber++;
		return v;
	}

	/**
	 * Creates a new Variable with the given number.
	 * 
	 * @param variableNumber
	 *            the number for the new Variable
	 */
	private Variable(int variableNumber) {
		this.variableNumber = variableNumber;
		this.negative = false;
	}

	/**
	 * Creates a new Variable with the given number and negativity.
	 * 
	 * @param variableNumber
	 *            the number for the variable
	 * @param true if the Variable is negative, false otherwise
	 */
	private Variable(int variableNumber, boolean negative) {
		this.variableNumber = variableNumber;
		this.negative = negative;
	}

	/**
	 * The total Variable count.
	 * 
	 * @return the number of variables currently in use
	 */
	public static int getNumberOfVariables() {
		return currentVariableNumber - 1;
	}

	/**
	 * Creates a negative clone of this Variable.
	 * 
	 * @return the negative clone
	 */
	private Variable negativeClone() {
		return new Variable(this.variableNumber, !negative);
	}

	/**
	 * Returns a Variable that is the negative of this Variable.
	 * 
	 * @return the negated Variable
	 */
	public Variable not() {
		return this.negativeClone();
	}

	/**
	 * Returns the result of the given Variable. Only to be used after the
	 * solver has run and found a solution.
	 * 
	 * @param v
	 *            the Variable
	 * @return true or false for the assignment by minisat
	 */
	private static Boolean getResult(Variable v) {
		return results.get(v);
	}

	/**
	 * Returns the result of this Variable. Only to be used after the solver has
	 * run and found a solution.
	 * 
	 * @return true or false for the assignment by minisat
	 * @throws IllegalStateException
	 *             if the solver didn't run yet or yielded no solution
	 */
	public Boolean getResult() {
		Boolean rv = Variable.getResult(this);
		if (rv == null) {
			throw new IllegalStateException(
					"solver not yet run or yielded no solution");
		}
		return rv;
	}

	/**
	 * Sets the result value of the Variable denoted by the given number to the
	 * given Boolean value.
	 * 
	 * @param i
	 *            the number of the Variable
	 * @param b
	 *            the result of the Variable denoted by the number
	 */
	protected static void setResult(Integer i, Boolean b) {
		Variable v = variables.get(i);
		results.put(v, b);
	}

	/**
	 * Returns a String of the number of the Variable preceeded by a hyphen if
	 * the Variable is negative. This behaviour is specified and can be used
	 * reliably in possible future versions of this program.
	 */
	@Override
	public String toString() {
		return (negative ? "-" : "") + this.variableNumber;
	}

}
