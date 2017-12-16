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

import org.jdeferred2.impl.DeferredObject;

/**
 * Deferred interface to trigger an event (resolve, reject, notify).
 * Subsequently, this will allow Promise observers to listen in on the event
 * (done, fail, progress).
 *
 * @param <D> Type used for {@link #resolve(Object)}
 * @param <F> Type used for {@link #reject(Object)}
 * @param <P> Type used for {@link #notify(Object)}
 *
 * @author Ray Tsang
 * @see DeferredObject
 */
public interface Deferred<D, F, P> extends Promise<D, F, P> {
	/**
	 * This should be called when a task has completed successfully.
	 * <p>
	 * <pre>
	 * <code>
	 * {@link Deferred} deferredObject = new {@link DeferredObject}();
	 * {@link Promise} promise = deferredObject.promise();
	 * promise.done(new {@link DoneCallback}() {
	 *   public void onDone(Object result) {
	 *   	// Done!
	 *   }
	 * });
	 *
	 * // another thread using the same deferredObject
	 * deferredObject.resolve("OK");
	 *
	 * </code>
	 * </pre>
	 *
	 * @param resolve the resolved value for this {@code Deferred}
	 *
	 * @return the reference to this {@code Deferred} instance.
	 */
	Deferred<D, F, P> resolve(final D resolve);

	/**
	 * This should be called when a task has completed unsuccessfully,
	 * i.e., a failure may have occurred.
	 * <p>
	 * <pre>
	 * <code>
	 * {@link Deferred} deferredObject = new {@link DeferredObject}();
	 * {@link Promise} promise = deferredObject.promise();
	 * promise.fail(new {@link FailCallback}() {
	 *   public void onFail(Object result) {
	 *   	// Failed :(
	 *   }
	 * });
	 *
	 * // another thread using the same deferredObject
	 * deferredObject.reject("BAD");
	 *
	 * </code>
	 * </pre>
	 *
	 * @param reject the rejected value for this {@code Deferred}
	 *
	 * @return the reference to this {@code Deferred} instance.
	 */
	Deferred<D, F, P> reject(final F reject);

	/**
	 * This should be called when a task is still executing and progress had been made,
	 * E.g., during a file download, notify the download progress.
	 * <p>
	 * <pre>
	 * <code>
	 * {@link Deferred} deferredObject = new {@link DeferredObject}();
	 * {@link Promise} promise = deferredObject.promise();
	 * promise.progress(new {@link ProgressCallback}() {
	 *   public void onProgress(Object progress) {
	 *   	// Failed :(
	 *   }
	 * });
	 *
	 * // another thread using the same deferredObject
	 * deferredObject.reject("100%");
	 *
	 * </code>
	 * </pre>
	 *
	 * @param progress the intermediate result for this {@code Deferred}
	 *
	 * @return the reference to this {@code Deferred} instance.
	 */
	Deferred<D, F, P> notify(final P progress);

	/**
	 * Return an {@link Promise} instance (i.e., an observer).  You can register callbacks in this observer.
	 *
	 * @return the reference to this {@code Deferred} instance as a {@code Promise},
	 */
	Promise<D, F, P> promise();
}
