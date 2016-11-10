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

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;

/**
 * An implementation of {@link Deferred} interface.
 * 
 * <pre>
 * <code>
 * final {@link Deferred} deferredObject = new {@link DeferredObject}
 * 
 * {@link Promise} promise = deferredObject.promise();
 * promise
 *   .done(new DoneCallback() { ... })
 *   .fail(new FailCallback() { ... })
 *   .progress(new ProgressCallback() { ... });
 *   
 * {@link Runnable} runnable = new {@link Runnable}() {
 *   public void run() {
 *     int sum = 0;
 *     for (int i = 0; i < 100; i++) {
 *       // something that takes time
 *       sum += i;
 *       deferredObject.notify(i);
 *     }
 *     deferredObject.resolve(sum);
 *   }
 * }
 * // submit the task to run
 * 
 * </code>
 * </pre>
 * 
 * @see DoneCallback
 * @see FailCallback
 * @see ProgressCallback
 * @author Ray Tsang
 */
public class DeferredObject<D, F, P> extends AbstractPromise<D, F, P> implements Deferred<D, F, P> {
	
	@Override
	public Deferred<D, F, P> resolve(final D resolve) {
		synchronized (this) {
			if (!isPending())
				throw new IllegalStateException("Deferred object already finished, cannot resolve again");
			
			this.state = State.RESOLVED;
			this.resolveResult = resolve;
			
			try {
				triggerDone(resolve);
			} finally {
				triggerAlways(state, resolve, null);
			}
		}
		return this;
	}

	@Override
	public Deferred<D, F, P> notify(final P progress) {
		synchronized (this) {
			if (!isPending())
				throw new IllegalStateException("Deferred object already finished, cannot notify progress");
			
			triggerProgress(progress);
		}
		return this;
	}

	@Override
	public Deferred<D, F, P> reject(final F reject) {
		synchronized (this) {
			if (!isPending())
				throw new IllegalStateException("Deferred object already finished, cannot reject again");
			this.state = State.REJECTED;
			this.rejectResult = reject;
			
			try {
				triggerFail(reject);
			} finally {
				triggerAlways(state, null, reject);
			}
		}
		return this;
	}

	public Promise<D, F, P> promise() {
		return this;
	}
}
