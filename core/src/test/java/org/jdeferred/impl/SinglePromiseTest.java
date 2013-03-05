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

import java.util.concurrent.Callable;

import org.jdeferred.DeferredCallable;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SinglePromiseTest extends AbstractDeferredTest<Integer> {
	@Test
	public void testDoneWait() {
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				return 100;
			}
		};
		
		deferredManager.when(task).done(new DoneCallback() {
			public void onDone(Object result) {
				holder.set((Integer) result);
				Assert.assertEquals(result, 100);
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
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				throw new RuntimeException("oops");
			}
		};
		
		deferredManager.when(task).done(new DoneCallback() {
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
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				throw new RuntimeException("oops");
			}
		};
		
		deferredManager.when(task).done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.fail("Should not be here");
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				holder.set(-1);
			}
		});
		
		waitForCompletion();
		holder.assertEquals(-1);
	}

	@Test
	public void testDoneNoWait() {
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				return 100;
			}
		};
		
		deferredManager.when(task).done(new DoneCallback() {
			public void onDone(Object result) {
				holder.set((Integer) result);
				Assert.assertEquals(result, 100);
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
		final ValueHolder<Integer> count = new ValueHolder<Integer>();
		count.set(0);
		
		deferredManager.when(task).done(new DoneCallback() {
			public void onDone(Object result) {
				holder.set((Integer) result);
				Assert.assertEquals(55, result);
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				Assert.fail("Shouldn't be here");
			}
		}).progress(new ProgressCallback() {
			public void onProgress(Object progress) {
				count.set(count.get() + 1);
			}
		});
		
		waitForCompletion();
		holder.assertEquals(55);
		count.assertEquals(10);
	}
}
