/*
 * Copyright 2013-2016 Ray Tsang
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
 * Progress fail by one of the {@link Promise}.
 * @author Ray Tsang
 *
 */
@SuppressWarnings("rawtypes")
public class OneReject {
	private final int index;
	private final Promise promise;
	private final Object reject;
	
	public OneReject(int index, Promise promise, Object reject) {
		super();
		this.index = index;
		this.promise = promise;
		this.reject = reject;
	}

	public int getIndex() {
		return index;
	}

	public Promise getPromise() {
		return promise;
	}

	public Object getReject() {
		return reject;
	}

	@Override
	public String toString() {
		return "OneReject [index=" + index + ", promise=" + promise
				+ ", reject=" + reject + "]";
	}
	
	
	
}
