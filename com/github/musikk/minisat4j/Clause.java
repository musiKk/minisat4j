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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a clause for the constraint solver. A clause consists
 * of possibly negative variables that are disjunctively combined.
 * 
 * @author Werner Hahn
 * @version 0.1
 * 
 */
public class Clause {

	/**
	 * The List of variables contained in this clause.
	 */
	private final List<Variable> variables;

	/**
	 * Creates a new instance of this class with an empty List of variables.
	 */
	public Clause() {
		variables = new ArrayList<Variable>();
	}

	/**
	 * Creates a new instance of this class with the given variables.
	 * 
	 * @param vars
	 *            the variables for this clause
	 */
	public Clause(Variable... vars) {
		this();
		for (Variable var : vars) {
			this.variables.add(var);
		}
	}

	/**
	 * Adds a new Variable to the variables of this clause.
	 * 
	 * @param var
	 *            the Variable to add
	 */
	public void addVariable(Variable var) {
		this.variables.add(var);
	}

	/**
	 * Returns a String representation of this Clause. It simply concatenates
	 * the String values of the variables sperated by spaces. This behaviour is
	 * specified and can be used reliably in possible future versions of this
	 * program.
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < variables.size(); i++) {
			s += variables.get(i) + " ";
		}
		s += "0";
		return s;
	}

	/**
	 * Returns constraints that ensure the following equivalence:<br>
	 * - left is a single variable<br>
	 * - right is a list of variables that are disjunctively combined<br>
	 * - the equivalence is left <-> right
	 * 
	 * @param left
	 *            a single variable
	 * @param right
	 *            a List of variables
	 * @return the clauses
	 */
	public static Clause[] equivalence(Variable left, List<Variable> right) {

		int numberOfClauses = right.size() + 1;
		Clause[] clauses = new Clause[numberOfClauses];
		// this is left -> right
		clauses[0] = new Clause(right.toArray(new Variable[] {}));
		clauses[0].addVariable(left.not());

		for (int i = 1; i < numberOfClauses; i++) {
			clauses[i] = new Clause(right.get(i - 1).not(), left);
		}
		return clauses;
	}

	/**
	 * Returns constraints that ensure that one and only one of the provided
	 * variables can be true. If n is the number of variables provided the
	 * number of constraints amounts to 1/2 * n * (n-1).
	 * 
	 * @param vars
	 *            the variables
	 * @return the clauses
	 */
	public static Clause[] onlyOne(Variable... vars) {

		final int numberOfClauses = (vars.length * (vars.length - 1)) / 2 + 1;
		Clause[] clauses = new Clause[numberOfClauses];

		int index = 0;
		for (int i = 0; i < vars.length - 1; i++) {
			Variable firstVar = vars[i];
			for (int j = i + 1; j < vars.length; j++) {
				Variable secondVar = vars[j];
				clauses[index++] = new Clause(firstVar.not(), secondVar.not());
			}
		}

		clauses[clauses.length - 1] = new Clause(vars);

		return clauses;

	}

	/**
	 * A main for testing purposes.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		int VAR_COUNT = 5;
		Variable left = Variable.getVariable();
		Variable[] vars = new Variable[VAR_COUNT];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = Variable.getVariable();
		}

		Clause[] equivalence = Clause.equivalence(left, Arrays.asList(vars));
		for (Clause c : equivalence) {
			System.out.println(c);
		}

	}

}
