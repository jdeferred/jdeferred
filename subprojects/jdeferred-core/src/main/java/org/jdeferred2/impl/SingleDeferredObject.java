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

import org.jdeferred2.DeferredFutureTask;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.multiple.OneReject;
import org.jdeferred2.multiple.OneResult;

/**
 * @author Andres Almiray
 */
final class SingleDeferredObject extends DeferredObject<OneResult<?>, OneReject<Throwable>, Void> implements Promise<OneResult<?>, OneReject<Throwable>, Void> {
	private int resolvedOrRejectedTaskIndex;

	SingleDeferredObject(final DeferredFutureTask<?, ?>[] tasks) {
		for (int index = 0; index < tasks.length; index++) {
			configureTask(index, tasks[index]);
		}

		fail(new FailCallback<OneReject<Throwable>>() {
			@Override
			public void onFail(OneReject<Throwable> result) {
				cancelAllTasks(tasks);
			}
		});

		done(new DoneCallback<OneResult<?>>() {
			@Override
			public void onDone(OneResult<?> result) {
				cancelAllTasks(tasks);
			}
		});
	}

	private void cancelAllTasks(DeferredFutureTask<?, ?>[] tasks) {
		for (int index = 0; index < tasks.length; index++) {
			DeferredFutureTask<?, ?> task = tasks[index];
			if (index != resolvedOrRejectedTaskIndex) {
				task.cancel(true);
			}
		}
	}

	private <D, P> void configureTask(final int index, final DeferredFutureTask<D, P> task) {
		task.promise().fail(new FailCallback<Throwable>() {
			public void onFail(Throwable reject) {
				synchronized (SingleDeferredObject.this) {
					if (SingleDeferredObject.this.isPending()) {
						// task $index is rejected
						resolvedOrRejectedTaskIndex = index;
						SingleDeferredObject.this.reject(new OneReject<Throwable>(index, task.promise(), reject));
					}
				}
			}
		}).done(new DoneCallback<D>() {
			public void onDone(D result) {
				synchronized (SingleDeferredObject.this) {
					if (SingleDeferredObject.this.isPending()) {
						// task $index is resolved
						resolvedOrRejectedTaskIndex = index;
						SingleDeferredObject.this.resolve(new OneResult<D>(index, task.promise(), result));
					}
				}
			}
		});
	}
}
