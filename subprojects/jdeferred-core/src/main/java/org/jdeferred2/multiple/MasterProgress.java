/*
 * Copyright 2013-2018 Ray Tsang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred2.multiple;

/**
 * Progress indicating how many promises need to finish ({@link #total}),
 * and how many had already finish ({@link #fulfilled}).
 * @author Ray Tsang
 *
 */
public class MasterProgress {
	private final int done;
	private final int fail;
	private final int total;
	
	public MasterProgress(int done, int fail, int total) {
		super();
		this.done = done;
		this.fail = fail;
		this.total = total;
	}

	public int getDone() {
		return done;
	}

	public int getFail() {
		return fail;
	}

	public int getTotal() {
		return total;
	}

	@Override
	public String toString() {
		return "MasterProgress [done=" + done + ", fail=" + fail
				+ ", total=" + total + "]";
	}
}