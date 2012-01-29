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
package com.github.musikk.minisat4j.util;

/**
 * A very simple timer class that keeps track of a duration for a single
 * process.
 * 
 * @author Werner Hahn
 * @version 0.1
 * 
 */
public class Timer {

	/**
	 * The timestamp this Timer was created.
	 */
	private final long startMillis;

	/**
	 * The timestamp this Timer was stopped.
	 */
	private long endMillis;

	/**
	 * Creates a new instance of this class and "starts" the timer.
	 */
	private Timer() {
		this.startMillis = System.currentTimeMillis();
	}

	/**
	 * Start and return a new Timer.
	 * 
	 * @return the Timer
	 */
	public static Timer startTimer() {
		return new Timer();
	}

	/**
	 * Stops a timer. Might be called more than once in order to add durations.
	 */
	public void stop() {
		this.endMillis = System.currentTimeMillis();
	}

	/**
	 * The time in milliseconds between the start and stop of the timer.
	 * 
	 * @return
	 */
	public long getDuration() {
		return endMillis - startMillis;
	}

}
