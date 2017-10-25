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
package org.jdeferred.impl;

import java.security.SecureRandom;

/**
 * @author Andres Almiray
 */
class ResolvingRunnable implements Runnable {
	protected static final SecureRandom RANDOM = new SecureRandom();
	protected final int index;

	ResolvingRunnable(int index) {
		this.index = index;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
			Thread.sleep(RANDOM.nextInt(100));
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
