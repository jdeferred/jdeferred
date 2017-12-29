/*
 * Copyright 2013-2017 Ray Tsang
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
package org.jdeferred2.impl;

import java.security.SecureRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Andres Almiray
 */
class ResolvingCallable implements Callable<Integer> {
	protected static final SecureRandom RANDOM = new SecureRandom();
	protected final int index;
	protected final int delay;
	protected final AtomicBoolean invoked;

	ResolvingCallable(int index) {
		this(index, 200, new AtomicBoolean(false));
	}

	ResolvingCallable(int index, int delay,AtomicBoolean invoked) {
		this.index = index;
		this.delay = delay;
		this.invoked = invoked;
	}

	public AtomicBoolean getInvoked() {
		return invoked;
	}

	@Override
	public Integer call() throws Exception {
		Thread.sleep(delay);
		Thread.sleep(RANDOM.nextInt(100));
		invoked.set(true);
		return index;
	}
}
