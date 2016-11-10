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
package org.jdeferred.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise.State;
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
}
