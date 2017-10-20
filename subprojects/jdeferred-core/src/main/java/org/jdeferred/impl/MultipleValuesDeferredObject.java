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

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleValues;
import org.jdeferred.multiple.OneProgress;
import org.jdeferred.multiple.OneReject;
import org.jdeferred.multiple.OneResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andres Almiray
 */
class MultipleValuesDeferredObject extends DeferredObject<MultipleValues, Throwable, MasterProgress> implements Promise<MultipleValues, Throwable, MasterProgress> {
	private final MutableMultipleValues values;
	private final int numberOfPromises;
	private final AtomicInteger doneCount = new AtomicInteger();
	private final AtomicInteger failCount = new AtomicInteger();

	MultipleValuesDeferredObject(Promise<?, ?, ?>[] promises) {
		this.numberOfPromises = promises.length;
		this.values = new DefaultMutableMultipleValues(promises.length);

		for (int i = 0; i < numberOfPromises; i++) {
			configurePromise(i, promises[i]);
		}
	}

	protected <D, F, P> void configurePromise(final int index, final Promise<D, F, P> promise) {
		promise.fail(new FailCallback<F>() {
			public void onFail(F result) {
				synchronized (MultipleValuesDeferredObject.this) {
					if (!MultipleValuesDeferredObject.this.isPending())
						return;

					values.set(index, new OneReject<F>(index, promise, result));
					final int fail = failCount.incrementAndGet();
					final int done = doneCount.get();

					MultipleValuesDeferredObject.this.notify(new MasterProgress(
						doneCount.get(),
						fail,
						numberOfPromises));

					if (fail + done == numberOfPromises) {
						MultipleValuesDeferredObject.this.resolve(values);
					}
				}
			}
		}).progress(new ProgressCallback<P>() {
			public void onProgress(P progress) {
				synchronized (MultipleValuesDeferredObject.this) {
					if (!MultipleValuesDeferredObject.this.isPending())
						return;

					MultipleValuesDeferredObject.this.notify(new OneProgress<P>(
						doneCount.get(),
						failCount.get(),
						numberOfPromises, index, promise, progress));
				}
			}
		}).done(new DoneCallback<D>() {
			public void onDone(D result) {
				synchronized (MultipleValuesDeferredObject.this) {
					if (!MultipleValuesDeferredObject.this.isPending())
						return;

					values.set(index, new OneResult<D>(index, promise, result));
					final int fail = failCount.get();
					final int done = doneCount.incrementAndGet();

					MultipleValuesDeferredObject.this.notify(new MasterProgress(
						done,
						failCount.get(),
						numberOfPromises));

					if (fail + done == numberOfPromises) {
						MultipleValuesDeferredObject.this.resolve(values);
					}
				}
			}
		});
	}
}
