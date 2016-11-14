/*
 * Copyright 2013-2016 Ray Tsang
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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractDeferredTest {
	protected DefaultDeferredManager deferredManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	protected void createDeferredManager() {
		this.deferredManager = new DefaultDeferredManager();
	}

	@Before
	public void setUp() throws Exception {
		this.createDeferredManager();
	}

	@After
	public void tearDown() throws Exception {
		waitForCompletion();
	}
	
	protected <R> Callable<R> successCallable(final R result, final int waitMs) {
		return new Callable<R>() {

			@Override
			public R call() throws Exception {
				if (waitMs > 0) {
					Thread.sleep(waitMs);
				}
				
				return result;
			}
		};
	}
	
	protected Callable<Void> failedCallable(final Exception exception, final int waitMs) {
		return new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				if (waitMs > 0) {
					Thread.sleep(waitMs);
				}
				
				throw exception;
			}
		};
	}
	
	protected void waitForCompletion() {
		deferredManager.shutdown();
		while (!deferredManager.isTerminated()) {
			try {
				deferredManager.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}
		}
	}

}
