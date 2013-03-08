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
package org.jdeferred.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DeferredCallable;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise.State;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SinglePromiseTest extends AbstractDeferredTest<Integer> {
	@Test
	public void testDoneWait() {
		deferredManager.when(successCallable(100, 1000))
		.done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.assertEquals(result, 100);
				holder.set((Integer) result);
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				Assert.fail("Shouldn't be here");
			}
		});
		
		holder.assertEquals(null);
		waitForCompletion();
		holder.assertEquals(100);
	}
	
	@Test
	public void testFailWait() {
		deferredManager.when(failedCallable(new RuntimeException("oops"), 1000))
		.done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.fail("Should not be here");
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				holder.set(-1);
			}
		});
		
		holder.assertEquals(null);
		waitForCompletion();
		holder.assertEquals(-1);
	}
	
	@Test
	public void testFailNoWait() {
		final AtomicInteger counter = new AtomicInteger();
		deferredManager.when(failedCallable(new RuntimeException("oops"), 0))
		.done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.fail("Should not be here");
			}
		}).fail(new FailCallback<Throwable>() {
			public void onFail(Throwable result) {
				counter.incrementAndGet();
				Assert.assertEquals("oops", result.getMessage());
			}
		});
		
		waitForCompletion();
		Assert.assertEquals(1, counter.get());
	}

	@Test
	public void testDoneNoWait() {
		deferredManager.when(successCallable(100, 0))
		.done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.assertEquals(result, 100);
				holder.set((Integer) result);
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				Assert.fail("Shouldn't be here");
			}
		});
		
		waitForCompletion();
		holder.assertEquals(100);
	}
	
	@Test
	public void testAlwaysDone() {
		final ValueHolder<Boolean> alwaysTriggered = new ValueHolder<Boolean>(false);
		
		deferredManager.when(successCallable(100, 1000))
		.done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.assertEquals(result, 100);
				holder.set((Integer) result);
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				Assert.fail("Shouldn't be here");
			}
		}).always(new AlwaysCallback<Integer, Throwable>() {
			@Override
			public void onAlways(State state, Integer resolved, Throwable rejected) {
				Assert.assertEquals(State.RESOLVED, state);
				Assert.assertEquals((Integer) 100, resolved);
				alwaysTriggered.set(true);
			}
		});
		
		waitForCompletion();
		holder.assertEquals(100);
		alwaysTriggered.assertEquals(true);
	}
	
	@Test
	public void testAlwaysFail() {
		final ValueHolder<Boolean> alwaysTriggered = new ValueHolder<Boolean>(false);
		
		deferredManager.when(failedCallable(new RuntimeException("oops"), 1000))
		.done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.fail("Shouldn't be here");
			}
		}).fail(new FailCallback<Throwable>() {
			public void onFail(Throwable result) {
				Assert.assertEquals("oops", result.getMessage());
				holder.set(1);
			}
		}).always(new AlwaysCallback<Void, Throwable>() {
			@Override
			public void onAlways(State state, Void resolved, Throwable rejected) {
				Assert.assertEquals(State.REJECTED, state);
				Assert.assertEquals("oops", rejected.getMessage());
				alwaysTriggered.set(true);
			}
		});
		
		waitForCompletion();
		holder.assertEquals(1);
		alwaysTriggered.assertEquals(true);
	}
	
	@Test
	public void testPorgressWait() {
		DeferredCallable<Integer, Integer> task = new DeferredCallable<Integer, Integer>() {
			public Integer call() {
				int sum = 0;
				for (int i = 1; i <= 10; i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					notify(i);
					sum += i;
				}
				
				return sum;
			}
		};
		
		// single threaded only
		final AtomicInteger count = new AtomicInteger(0);
		
		deferredManager.when(task).done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.assertEquals(55, result);
				holder.set((Integer) result);
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				Assert.fail("Shouldn't be here");
			}
		}).progress(new ProgressCallback() {
			public void onProgress(Object progress) {
				count.incrementAndGet();
			}
		});
		
		waitForCompletion();
		holder.assertEquals(55);
		Assert.assertEquals(10, count.get());
	}
	
	@Test
	public void testFuture() {
		ExecutorService es = deferredManager.getExecutorService();
		Future<Integer> future = es.submit(successCallable(999, 100));
		final AtomicInteger doneCount = new AtomicInteger();
		deferredManager.when(future).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				Assert.assertEquals((Integer) 999, result);
				doneCount.incrementAndGet();
			}
		});
		
		Future<Void> failedFuture = es.submit(failedCallable(new RuntimeException("oops"), 300));
		final AtomicInteger failCount = new AtomicInteger();
		deferredManager.when(failedFuture).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				Assert.assertEquals("oops", result.getCause());
				failCount.incrementAndGet();
			}
		});
		
		waitForCompletion();
		Assert.assertEquals(1, doneCount.get());
	}
}
