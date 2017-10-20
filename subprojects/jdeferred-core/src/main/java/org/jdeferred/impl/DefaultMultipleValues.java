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

import org.jdeferred.multiple.MultipleValues;
import org.jdeferred.multiple.OneOf;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base implementation of {@link MultipleValues}.
 *
 * @author Ray Tsang
 * @author Andres Almiray
 */
class DefaultMultipleValues implements MultipleValues {
	protected final List<OneOf<?>> values;

	DefaultMultipleValues(int size) {
		this.values = new CopyOnWriteArrayList<OneOf<?>>(new OneOf[size]);
	}

	@Override
	public OneOf<?> get(int index) {
		return values.get(index);
	}

	@Override
	public Iterator<OneOf<?>> iterator() {
		return values.iterator();
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [values=" + values + "]";
	}
}
