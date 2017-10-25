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

/**
 * @author Andres Almiray
 */
class RejectingCallable extends ResolvingCallable {
	RejectingCallable(int index) {
		super(index);
	}

	@Override
	public Integer call() throws Exception {
		Thread.sleep(500);
		Thread.sleep(RANDOM.nextInt(100));
		throw new IndexedRuntimeException(index);
	}
}
