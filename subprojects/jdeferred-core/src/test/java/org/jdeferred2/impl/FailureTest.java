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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred2.AlwaysCallback;
import org.jdeferred2.CallbackExceptionHandler;
import org.jdeferred2.Deferred;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.ProgressCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.Promise.State;
import org.junit.Assert;
import org.junit.Test;

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
}
