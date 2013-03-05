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

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"rawtypes"})
public class MultiplePromisesTest extends AbstractDeferredTest<Integer> {
	@Test
	public void testMultipleDoneWait() {
		deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				
				return 100;
			}
		}, new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				
				return "Hello";
			}
		}).then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				holder.set(1);
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.getResult(0));
				Assert.assertEquals("Hello", results.getResult(1));
			}
		});
		
		waitForCompletion();
		Assert.assertEquals((Integer) 1, holder.get());
	}
	
	@Test
	public void testMultipleFailWait() {
		deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				
				throw new RuntimeException("oops");
			}
		}, new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				
				return "Hello";
			}
		}).then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				Assert.fail("Shouldn't be here");
			}
		}).fail(new FailCallback<OneReject>() {
			public void onFail(OneReject result) {
				holder.set(1);
				Assert.assertEquals(0, result.getIndex());
			}
		});
		
		waitForCompletion();
		Assert.assertEquals((Integer) 1, holder.get());
	}
	
	@Test
	public void testComplex() {
		Promise p1 = deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				
				return 100;
			}
		}).then(new DoneCallback<Integer>() {
			public void onDone(Integer result) {
				Assert.assertEquals((Integer) 100, result);
			}
		});
		Promise p2 = deferredManager.when(new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				
				return "Hello";
			}
		}).then(new DoneCallback<String>() {

			public void onDone(String result) {
				Assert.assertEquals("Hello", result);
			}
		});
		
		deferredManager.when(p1, p2)
		.then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				holder.set(1);
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.getResult(0));
				Assert.assertEquals("Hello", results.getResult(1));
			}
		}).fail(new FailCallback<OneReject>() {
			public void onFail(OneReject result) {
				Assert.fail("shouldn't be here");
			}
		});
		
		waitForCompletion();
		Assert.assertEquals((Integer) 1, holder.get());
	}
}
