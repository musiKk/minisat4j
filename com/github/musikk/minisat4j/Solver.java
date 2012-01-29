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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.github.musikk.minisat4j.util.Timer;

/**
 * This class represents the bridge to the constraint solver minisat. It
 * converts Clauses into the format readable by minisat and converts the reply
 * back to be used programmatically by Java programs.
 * 
 * @author Werner Hahn
 * @version 0.1
 * 
 */
public class Solver {

	/**
	 * The default file name for the properties file.
	 */
	private static final String DEFAULT_PROPERTIES_FILE = "solver.cfg";

	/**
	 * The default name of the minisat executable.
	 */
	private static final String DEFAULT_EXECUTABLE = "minisat";

	/**
	 * The default value of the verbosity.
	 */
	private static final boolean DEFAULT_VERBOSITY = false;

	/**
	 * The name of the minisat executable.
	 */
	private String executable;

	/**
	 * The value of the verbosity.
	 */
	private boolean verbose;

	/**
	 * The list of clauses that represent the CNF to be solved by minisat.
	 */
	private final List<Clause> clauses;

	/**
	 * Creates a new solver.
	 */
	public Solver() {
		Properties props = new Properties();
		File propertiesFile = new File(DEFAULT_PROPERTIES_FILE);
		try {
			props.load(new FileInputStream(propertiesFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("config file not found");
		} catch (IOException e) {
			throw new RuntimeException("error reading config file");
		}
		loadProperties(props);

		this.clauses = new ArrayList<Clause>();
	}

	/**
	 * Creates a new solver with the specified properties. The external
	 * properties file gets ignored.
	 * 
	 * @param props
	 *            the properties to use for the solver.
	 */
	public Solver(Properties props) {
		loadProperties(props);
		clauses = new ArrayList<Clause>();
	}

	/**
	 * Loads the properties into the variables.
	 * 
	 * @param props
	 *            the properties to use for the solver.
	 */
	private void loadProperties(Properties props) {
		this.executable = props.getProperty("solver.executable",
				DEFAULT_EXECUTABLE);
		this.verbose = Boolean.parseBoolean(props.getProperty("verbose",
				Boolean.toString(DEFAULT_VERBOSITY)));
	}

	/**
	 * Converts the list of clauses into a format readable by minisat, lets the
	 * solver solve the problem and parses the reply back into a format readable
	 * by Java programs.
	 * 
	 * @return the result of the solver
	 */
	public SolverResult solve() {

		Timer timer = Timer.startTimer();

		Runtime r = Runtime.getRuntime();

		try {

			Process p = r.exec(new String[] { executable, "/dev/stdin",
					"/dev/stdout" });

			BufferedWriter solverStdin = new BufferedWriter(
					new OutputStreamWriter(p.getOutputStream()));

			int varCount = Variable.getNumberOfVariables();
			int clauseCount = this.clauses.size();
			String header = String.format("p cnf %d %d", varCount, clauseCount);

			if (verbose) {
				System.out.println(header);
			}
			solverStdin.write(header);
			solverStdin.newLine();

			for (Clause c : this.clauses) {
				String clauseString = c.toString();
				if (verbose) {
					System.out.println(clauseString);
				}

				solverStdin.write(clauseString);
				solverStdin.newLine();
			}
			solverStdin.flush();
			solverStdin.close();

			System.err.println("calculating " + varCount + " variables and "
					+ clauseCount + " clauses");

			BufferedReader solverStdout = new BufferedReader(
					new InputStreamReader(p.getInputStream()));

			String satisfiability = solverStdout.readLine();
			boolean satisfiable = false;
			if (satisfiability.equals("SAT")) {
				satisfiable = true;
			}
			SolverResult result = new SolverResult(satisfiable);

			if (satisfiable) {
				String variables = solverStdout.readLine();
				result.addVariables(variables);
			}

			BufferedReader solverStderr = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			String line = null;
			while ((line = solverStderr.readLine()) != null) {
				result.addStatisticsLine(line);
			}

			timer.stop();

			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Adds a clause to the CNF.
	 * 
	 * @param c
	 *            the clause to add
	 */
	public void addClause(Clause c) {
		this.clauses.add(c);
	}

	/**
	 * Adds clauses to the CNF.
	 * 
	 * @param cs
	 *            the clauses to add
	 */
	public void addClauses(Clause[] cs) {
		this.addClauses(Arrays.asList(cs));
	}

	/**
	 * Adds clauses to the CNF.
	 * 
	 * @param cs
	 *            the clauses to add
	 */
	public void addClauses(Collection<Clause> cs) {
		this.clauses.addAll(cs);
	}

	/**
	 * A main method used for testing purposes.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Properties props = new Properties();
		props.load(new FileInputStream(new File("solver.cfg")));

		Solver s = new Solver(props);

		Variable left = Variable.getVariable();

		Variable[] vars = new Variable[5];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = Variable.getVariable();
		}

		s.addClauses(Clause.equivalence(left, Arrays.asList(vars)));
		s.addClauses(Clause.onlyOne(vars));
		s.addClause(new Clause(left.not()));

		SolverResult result = s.solve();
		System.out.println("satisfiable: " + result.isSatisfiable());

		System.out.printf("left  : %s%n", left.getResult());
		for (int i = 0; i < vars.length; i++) {
			System.out.printf("var #%d: %s%n", i, vars[i].getResult());
		}

	}
}
