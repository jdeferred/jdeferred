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
package org.jdeferred.multiple;

import org.jdeferred.Promise;

/**
 * Progress result by one of the {@link Promise}.
 *
 * @author Ray Tsang
 */
@SuppressWarnings("rawtypes")
public class OneResult<D> {
	private final int index;
	private final Promise<D, ?, ?> promise;
	private final D result;

	public OneResult(int index, Promise<D, ?, ?> promise, D result) {
		super();
		this.index = index;
		this.promise = promise;
		this.result = result;
	}

	public int getIndex() {
		return index;
	}

	public Promise<D, ?, ?> getPromise() {
		return promise;
	}

	public D getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "OneResult [index=" + index + ", promise=" + promise
			+ ", result=" + result + "]";
	}
}
