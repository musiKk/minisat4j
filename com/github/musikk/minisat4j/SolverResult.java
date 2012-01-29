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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class encapsulates some information produced by a run of minisat.
 * 
 * @author Werner Hahn
 * @version 0.1
 * 
 */
public class SolverResult {

	/**
	 * The satisfiability of the associated CNF.
	 */
	private final boolean satisfiable;

	/**
	 * Raw statistics created by minisat for statistical purposes.
	 */
	private final List<String> statistics;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param satisfiable
	 *            true if the associated CNF is satisfiable, false otherwise
	 */
	protected SolverResult(boolean satisfiable) {
		this.satisfiable = satisfiable;
		this.statistics = new ArrayList<String>();
	}

	/**
	 * Adds a new line of statistics.
	 * 
	 * @param line
	 *            the line to add
	 */
	protected void addStatisticsLine(String line) {
		this.statistics.add(line);
	}

	/**
	 * Returns a List of lines containing the statistical information.
	 * 
	 * @return a list of lines
	 */
	public List<String> getStatistics() {
		return new ArrayList<String>(this.statistics);
	}

	/**
	 * Returns a String representation of the statistics. This format might
	 * change in future versions and should only be used for usage by humans.
	 * Use <code>getStatistics()</code> for a reliable format.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String line : this.statistics) {
			sb.append(line);
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Returns the satisfiability of the associated CNF.
	 * 
	 * @return true if the CNF is satisfiable, false otherwise
	 */
	public boolean isSatisfiable() {
		return satisfiable;
	}

	/**
	 * Converts the result line containing the assigned variables of minisat (if
	 * the problem is satisfiable) to actual boolean values and sets the results
	 * of the variables accordingly.
	 * 
	 * @param variablesLine
	 *            the line produced by minisat
	 */
	protected void addVariables(String variablesLine) {
		String[] splittedVariablesLine = variablesLine.split("\\s");
		Pattern p = Pattern.compile("(-?)(\\d+)");
		for (String variableString : splittedVariablesLine) {
			if (variableString.equals("0")) {
				return;
			}
			Matcher m = p.matcher(variableString);
			if (!m.find()) {
				throw new RuntimeException("malformed result: "
						+ variableString);
			}
			boolean positive = true;
			if (m.group(1).equals("-")) {
				positive = false;
			}
			int variableNumber = Integer.parseInt(m.group(2));
			Variable.setResult(variableNumber, positive);
		}
		throw new RuntimeException("unexpected end of input");
	}

}
