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

import org.jdeferred2.multiple.AllValues;
import org.jdeferred2.multiple.OneValue;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base implementation of {@link AllValues}.
 *
 * @author Ray Tsang
 * @author Andres Almiray
 */
class DefaultAllValues implements AllValues {
	protected final List<OneValue<?>> values;

	DefaultAllValues(int size) {
		this.values = new CopyOnWriteArrayList<OneValue<?>>(new OneValue[size]);
	}

	@Override
	public OneValue<?> get(int index) {
		return values.get(index);
	}

	@Override
	public Iterator<OneValue<?>> iterator() {
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
