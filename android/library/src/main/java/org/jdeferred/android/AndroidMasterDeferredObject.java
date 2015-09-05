/*
 * Copyright 2013 Ray Tsang
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred.android;

import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.android.AndroidPromise;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.*;

/**
 * This will return a special Promise called {@link MasterDeferredObject}. In short,
 * <ul>
 * <li>{@link Promise#done(DoneCallback)} will be triggered if all promises resolves
 * (i.e., all finished successfully) with {@link MultipleResults}.</li>
 * <li>{@link Promise#fail(FailCallback)} will be
 * triggered if any promises rejects (i.e., if any one failed) with {@link OneReject}.</li>
 * <li>{@link Promise#progress(ProgressCallback)} will be triggered whenever one
 * promise resolves or rejects ({#link {@link MasterProgress}), 
 * or whenever a promise was notified progress ({@link OneProgress}).</li>
 * <li>{@link Promise#always(AlwaysCallback)} will be triggered whenever
 * {@link Promise#done(DoneCallback)} or {@link Promise#fail(FailCallback)}
 * would be triggered</li>
 * </ul>
 *
 * @author Ray Tsang
 *
 */
@SuppressWarnings("rawtypes")
public class AndroidMasterDeferredObject extends
		AndroidDeferredObject<MultipleResults, OneReject, MasterProgress>
		implements AndroidPromise<MultipleResults, OneReject, MasterProgress> {
	private final int numberOfPromises;
	private final AtomicInteger doneCount = new AtomicInteger();
	private final AtomicInteger failCount = new AtomicInteger();
	private final AndroidMultipleResults results;

	@SuppressWarnings("unchecked")
	public AndroidMasterDeferredObject(Promise... promises) {
		if (promises == null || promises.length == 0)
			throw new IllegalArgumentException("Promises is null or empty");
		this.numberOfPromises = promises.length;
		results = new AndroidMultipleResults(numberOfPromises);

		int count = 0;
		for (final Promise promise : promises) {
			final int index = count++;
			promise.fail(new AndroidFailCallback<Object>() {
				@Override
				public AndroidExecutionScope getExecutionScope() {
					return AndroidExecutionScope.BACKGROUND;
				}

				public void onFail(Object result) {
					synchronized (AndroidMasterDeferredObject.this) {
						if (!AndroidMasterDeferredObject.this.isPending())
							return;

						final int fail = failCount.incrementAndGet();
						AndroidMasterDeferredObject.this.notify(new MasterProgress(
								doneCount.get(),
								fail,
								numberOfPromises));

						AndroidMasterDeferredObject.this.reject(new OneReject(index, promise, result));
					}
				}
			}).progress(new AndroidProgressCallback() {
				@Override
				public AndroidExecutionScope getExecutionScope() {
					return AndroidExecutionScope.BACKGROUND;
				}

				public void onProgress(Object progress) {
					synchronized (AndroidMasterDeferredObject.this) {
						if (!AndroidMasterDeferredObject.this.isPending())
							return;

						AndroidMasterDeferredObject.this.notify(new OneProgress(
								doneCount.get(),
								failCount.get(),
								numberOfPromises, index, promise, progress));
					}
				}
			}).done(new AndroidDoneCallback() {
				@Override
				public AndroidExecutionScope getExecutionScope() {
					return AndroidExecutionScope.BACKGROUND;
				}

				public void onDone(Object result) {
					synchronized (AndroidMasterDeferredObject.this) {
						if (!AndroidMasterDeferredObject.this.isPending())
							return;

						results.set(index, new OneResult(index, promise,
								result));
						int done = doneCount.incrementAndGet();

						AndroidMasterDeferredObject.this.notify(new MasterProgress(
								done,
								failCount.get(),
								numberOfPromises));

						if (done == numberOfPromises) {
							AndroidMasterDeferredObject.this.resolve(results);
						}
					}
				}
			});
		}
	}
}
