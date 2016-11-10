/*
 * Copyright Ray Tsang ${author}
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
 * 
 * @author Ray Tsang
 *
 */
@SuppressWarnings("rawtypes")
public class OneResult {
	private final int index;
	private final Promise promise;
	private final Object result;
	
	public OneResult(int index, Promise promise, Object result) {
		super();
		this.index = index;
		this.promise = promise;
		this.result = result;
	}
	public int getIndex() {
		return index;
	}
	public Promise getPromise() {
		return promise;
	}
	public Object getResult() {
		return result;
	}
	@Override
	public String toString() {
		return "OneResult [index=" + index + ", promise=" + promise
				+ ", result=" + result + "]";
	}
}
