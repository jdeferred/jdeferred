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
import org.jdeferred.CallbackExceptionHandler;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"unchecked", "rawtypes"})
public class FailureTest extends AbstractDeferredTest {
	@Test
	public void testBadCallback() {
		final AtomicInteger counter = new AtomicInteger();

		deferredManager.when(successCallable(100, 1000))
			.done(new DoneCallback() {
				public void onDone(Object result) {
					counter.incrementAndGet();
					throw new RuntimeException("this exception is expected");
				}
			}).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				counter.incrementAndGet();
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				Assert.fail("Shouldn't be here");
			}
		}).always(new AlwaysCallback<Integer, Throwable>() {

			@Override
			public void onAlways(State state, Integer resolved, Throwable rejected) {
				counter.incrementAndGet();
			}
		});

		waitForCompletion();
		Assert.assertEquals(3, counter.get());
	}

	@Test
	public void testResolvingTwice() {
		Deferred<Integer, Void, Void> deferred = new DeferredObject<Integer, Void, Void>();
		deferred.done(new DoneCallback<Integer>() {

			@Override
			public void onDone(Integer result) {
				// do nothing;
			}
		});

		boolean exceptionCaught = false;
		deferred.resolve(1);
		try {
			deferred.resolve(2);
		} catch (IllegalStateException e) {
			exceptionCaught = true;
		}
		Assert.assertTrue(exceptionCaught);
	}

	@Test
	public void testResolvingTwiceInThread() {
		final AtomicBoolean exceptionCaught = new AtomicBoolean();

		final Deferred<Integer, Void, Void> deferred = new DeferredObject<Integer, Void, Void>();
		deferredManager.when(new Runnable() {
			@Override
			public void run() {
				deferred.resolve(0);
				deferred.resolve(1);
			}
		}).fail(new FailCallback<Throwable>() {

			@Override
			public void onFail(Throwable result) {
				Assert.assertTrue(result instanceof IllegalStateException);
				exceptionCaught.set(true);
			}
		});

		waitForCompletion();
		Assert.assertTrue(exceptionCaught.get());
	}

	@Test
	public void testGlobalExceptionHandler() {
		final ConcurrentHashMap<CallbackExceptionHandler.CallbackType, Exception> handled =
			new ConcurrentHashMap<CallbackExceptionHandler.CallbackType, Exception>();

		GlobalConfiguration.setGlobalCallbackExceptionHandler(new CallbackExceptionHandler() {
			@Override
			public void handleException(CallbackType callbackType, Exception e) {
				handled.put(callbackType, e);
			}
		});

		Promise<String, String, String> p = new DeferredObject<String, String, String>().resolve("ok").promise();
		p.done(new DoneCallback<String>() {
			@Override
			public void onDone(String result) {
				throw new RuntimeException("oops");
			}
		});

		Assert.assertEquals(1, handled.size());
		Assert.assertTrue("DONE_CALLBACK is missing", handled.containsKey(CallbackExceptionHandler.CallbackType.DONE_CALLBACK));
		handled.clear();

		p = new DeferredObject<String, String, String>().reject("no").promise();
		p.fail(new FailCallback<String>() {
			@Override
			public void onFail(String result) {
				throw new RuntimeException("oops");
			}
		});

		Assert.assertEquals(1, handled.size());
		Assert.assertTrue("FAIL_CALLBACK is missing", handled.containsKey(CallbackExceptionHandler.CallbackType.FAIL_CALLBACK));
		handled.clear();

		p.always(new AlwaysCallback<String, String>() {
			@Override
			public void onAlways(State state, String resolved, String rejected) {
				throw new RuntimeException("oops");
			}
		});
		Assert.assertEquals(1, handled.size());
		Assert.assertTrue("ALWAYS_CALLBACK is missing", handled.containsKey(CallbackExceptionHandler.CallbackType.ALWAYS_CALLBACK));
		handled.clear();

		DeferredObject<String, String, String> progressObject = new DeferredObject<String, String, String>();
		p = progressObject.promise();
		p.progress(new ProgressCallback<String>() {
			@Override
			public void onProgress(String progress) {
				throw new RuntimeException("oops");
			}
		});

		progressObject.notify("50%");

		Assert.assertEquals(1, handled.size());
		Assert.assertTrue("PROGRESS_CALLBACK is missing", handled.containsKey(CallbackExceptionHandler.CallbackType.PROGRESS_CALLBACK));
		handled.clear();
	}

	@Test
	public void testCallbackExeptionHandlerPrecedence() throws Exception {
		// given:
		final AtomicBoolean globalInvoked = new AtomicBoolean();
		final AtomicBoolean dmInvoked = new AtomicBoolean();
		final AtomicBoolean localInvoked = new AtomicBoolean();

		class InvokeWitnessCallbackExceptionHandler implements CallbackExceptionHandler {
			private final AtomicBoolean witness;

			private InvokeWitnessCallbackExceptionHandler(AtomicBoolean witness) {
				this.witness = witness;
			}

			@Override
			public void handleException(CallbackType callbackType, Exception e) {
				witness.set(true);
			}
		}

		DoneCallback<Integer> doneCallback = new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				throw new RuntimeException("oops");
			}
		};

		// when:
		GlobalConfiguration.setGlobalCallbackExceptionHandler(new InvokeWitnessCallbackExceptionHandler(globalInvoked));
		deferredManager.setCallbackExceptionHandler(new InvokeWitnessCallbackExceptionHandler(dmInvoked));
		deferredManager.when(new ResolvingCallable(0))
			.setCallbackExceptionHandler(new InvokeWitnessCallbackExceptionHandler(localInvoked))
			.done(doneCallback).waitSafely(300);

		// then:
		assertFalse("Global CallbackExceptionHandler should not be invoked", globalInvoked.get());
		assertFalse("DM CallbackExceptionHandler should not be invoked", dmInvoked.get());
		assertTrue("Local CallbackExceptionHandler should have been invoked", localInvoked.get());

		// reset
		localInvoked.set(false);

		// when:
		deferredManager.when(new ResolvingCallable(0))
			.done(doneCallback).waitSafely(300);

		// then:
		assertFalse("Global CallbackExceptionHandler should not be invoked", globalInvoked.get());
		assertTrue("DM CallbackExceptionHandler should have been invoked", dmInvoked.get());
		assertFalse("Local CallbackExceptionHandler should not be invoked", localInvoked.get());

		// reset
		dmInvoked.set(false);

		// when:
		deferredManager.setCallbackExceptionHandler(null);
		deferredManager.when(new ResolvingCallable(0))
			.done(doneCallback).waitSafely(300);

		// then:
		assertTrue("Global CallbackExceptionHandler should have been invoked", globalInvoked.get());
		assertFalse("DM CallbackExceptionHandler should not be invoked", dmInvoked.get());
		assertFalse("Local CallbackExceptionHandler should not be invoked", localInvoked.get());

		// reset
		globalInvoked.set(false);

		// when:
		GlobalConfiguration.setGlobalCallbackExceptionHandler(null);
		deferredManager.when(new ResolvingCallable(0))
			.done(doneCallback).waitSafely(300);

		// then:
		assertFalse("Global CallbackExceptionHandler should not be invoked", globalInvoked.get());
		assertFalse("DM CallbackExceptionHandler should not be invoked", dmInvoked.get());
		assertFalse("Local CallbackExceptionHandler should not be invoked", localInvoked.get());
	}
}
