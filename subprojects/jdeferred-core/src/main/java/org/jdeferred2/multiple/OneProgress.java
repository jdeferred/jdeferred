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
package org.jdeferred2.multiple;

import org.jdeferred2.Promise;

/**
 * Progress update by one of the {@link Promise}.
 *
 * @author Ray Tsang
 */
@SuppressWarnings("rawtypes")
public class OneProgress<P> extends MasterProgress implements OneValue<P> {
	private final int index;
	private final Promise<?, ?, P> promise;
	private final P progress;

	public OneProgress(int done, int fail, int total, int index, Promise<?, ?, P> promise, P progress) {
		super(done, fail, total);
		this.index = index;
		this.promise = promise;
		this.progress = progress;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public P getValue() {
		return getProgress();
	}

	public Promise<?, ?, P> getPromise() {
		return promise;
	}

	public P getProgress() {
		return progress;
	}

	@Override
	public String toString() {
		return "OneProgress [index=" + index + ", promise=" + promise
			+ ", progress=" + progress + ", getDone()=" + getDone()
			+ ", getFail()=" + getFail() + ", getTotal()=" + getTotal()
			+ "]";
	}
}
