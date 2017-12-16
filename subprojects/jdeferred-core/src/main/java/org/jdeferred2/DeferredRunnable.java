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
package org.jdeferred2;

import org.jdeferred2.DeferredManager.StartPolicy;
import org.jdeferred2.impl.DeferredObject;

/**
 * Use this as superclass in case you need to be able to be able to notify progress.
 * If you don't need to notify progress, you can simply use {@link Runnable}
 *
 * @param <P> Type used for {@link Deferred#notify(Object)}
 *
 * @author Ray Tsang
 * @see #notify(Object)
 */
public abstract class DeferredRunnable<P> implements Runnable {
	private final Deferred<Void, Throwable, P> deferred = new DeferredObject<Void, Throwable, P>();
	private final StartPolicy startPolicy;

	public DeferredRunnable() {
		this.startPolicy = StartPolicy.DEFAULT;
	}

	public DeferredRunnable(StartPolicy startPolicy) {
		this.startPolicy = startPolicy;
	}

	/**
	 * @param progress
	 *
	 * @see Deferred#notify(Object)
	 */
	protected void notify(P progress) {
		deferred.notify(progress);
	}

	protected Deferred<Void, Throwable, P> getDeferred() {
		return deferred;
	}

	public StartPolicy getStartPolicy() {
		return startPolicy;
	}
}
