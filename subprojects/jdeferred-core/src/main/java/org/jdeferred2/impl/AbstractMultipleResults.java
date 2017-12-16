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

import org.jdeferred2.multiple.MultipleResults;
import org.jdeferred2.multiple.OneResult;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base implementation of {@link MultipleResults}.
 *
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 */
abstract class AbstractMultipleResults implements MultipleResults {
	protected final List<OneResult<?>> results;

	AbstractMultipleResults(int size) {
		this.results = new CopyOnWriteArrayList<OneResult<?>>(new OneResult[size]);
	}

	@Override
	public OneResult<?> get(int index) {
		return results.get(index);
	}

	@Override
	public Iterator<OneResult<?>> iterator() {
		return results.iterator();
	}

	@Override
	public int size() {
		return results.size();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [results=" + results + "]";
	}
}
