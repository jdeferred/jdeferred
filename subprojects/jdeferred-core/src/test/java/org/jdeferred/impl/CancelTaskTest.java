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

import org.jdeferred.AlwaysCallback;
import org.jdeferred.CancelCallback;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CancelTaskTest extends AbstractDeferredTest {
	@Test
	public void testCancelTask() {
		final AtomicBoolean cancelWitness = new AtomicBoolean(false);

		DeferredFutureTask<String, Void> deferredFutureTask =
			new DeferredFutureTask<String, Void>(new Callable<String>() {
				@Override
				public String call() throws Exception {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

					return "Hello";
				}
			});

		Promise<String, Throwable, Void> promise = deferredManager.when(deferredFutureTask)
			.then(new DoneCallback<String>() {
				@Override
				public void onDone(String result) {
					Assert.fail("Shouldn't be called, because task was cancelled");
				}
			}).always(new AlwaysCallback<String, Throwable>() {
				@Override
				public void onAlways(State state, String resolved, Throwable rejected) {
					assertEquals(State.CANCELLED, state);
					assertNull(resolved);
					assertNull(rejected);
				}
			}).cancel(new CancelCallback() {
				@Override
				public void onCancel() {
					cancelWitness.set(true);
				}
			});

		deferredFutureTask.cancel(true);

		assertTrue(promise.isCancelled());
		assertTrue(cancelWitness.get());
	}
}
