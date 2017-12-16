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

/**
 * Promise interface to observe when some action has occurred on the corresponding {@link Deferred} object.
 *
 * A promise object should be obtained from {@link Deferred#promise()), or
 * by using DeferredManager.
 *
 * <pre>
 * <code>
 * Deferred deferredObject = new DeferredObject();
 * Promise promise = deferredObject.promise();
 * promise.done(new DoneCallback() {
 *   public void onDone(Object result) {
 *     // Done!
 *   }
 * });
 *
 * // another thread using the same deferredObject
 * deferredObject.resolve("OK");
 *
 * </code>
 * </pre>
 *
 * @see Deferred#resolve(Object)
 * @see Deferred#reject(Object)
 * @see Deferred#notify(Object)
 *
 * @author Ray Tsang
 * @author Stephan Classen
 *
 * @param <D> Type used for {@link #done(DoneCallback)}
 * @param <F> Type used for {@link #fail(FailCallback)}
 * @param <P> Type used for {@link #progress(ProgressCallback)}
 */
public interface Promise<D, F, P> {
	enum State {
		/**
		 * The Promise is still pending - it could be created, submitted for execution,
		 * or currently running, but not yet finished.
		 */
		PENDING,

		/**
		 * The Promise has finished running and a failure occurred.
		 * Thus, the Promise is rejected.
		 *
		 * @see Deferred#reject(Object)
		 */
		REJECTED,

		/**
		 * The Promise has finished running successfully.
		 * Thus, the Promise is resolved.
		 *
		 * @see Deferred#resolve(Object)
		 */
		RESOLVED
	}

	/**
	 * @return the state of this promise.
	 */
	State state();

	/**
	 * Queries the state of this promise, returning {@code true} iff it is {@code State.PENDING}.
	 * 
	 * @see State#PENDING
	 * @return {@code true} if the current state of this promise is {@code State.PENDING}, {@code false} otherwise.
	 */
	boolean isPending();

	/**
	 * Queries the state of this promise, returning {@code true} iff it is {@code State.RESOLVED}.
	 *
	 * @see State#RESOLVED
	 * @return {@code true} if the current state of this promise is {@code State.RESOLVED}, {@code false} otherwise.
	 */
	boolean isResolved();

	/**
	 * Queries the state of this promise, returning {@code true} iff it is {@code State.REJECTED}.
	 *
	 * @see State#REJECTED
	 * @return {@code true} if the current state of this promise is {@code State.REJECTED}, {@code false} otherwise.
	 */
	boolean isRejected();

	/**
	 * Equivalent to {@link #done(DoneCallback)}
	 *
	 * @param doneCallback see {@link #done(DoneCallback)}
	 * @return {@code this} for chaining more calls
	 */
	Promise<D, F, P> then(DoneCallback<? super D> doneCallback);

	/**
	 * Equivalent to {@link #done(DoneCallback)}.{@link #fail(FailCallback)}
	 * 
	 * @param doneCallback see {@link #done(DoneCallback)}
	 * @param failCallback see {@link #fail(FailCallback)}
	 * @return {@code this} for chaining more calls
	 */
	Promise<D, F, P> then(DoneCallback<? super D> doneCallback, FailCallback<? super F> failCallback);

	/**
	 * Equivalent to {@link #done(DoneCallback)}.{@link #fail(FailCallback)}.{@link #progress(ProgressCallback)}
	 *
	 * @param doneCallback see {@link #done(DoneCallback)}
	 * @param failCallback see {@link #fail(FailCallback)}
	 * @param progressCallback see {@link #progress(ProgressCallback)}
	 * @return {@code this} for chaining more calls
	 */
	Promise<D, F, P> then(DoneCallback<? super D> doneCallback,
			FailCallback<? super F> failCallback, ProgressCallback<? super P> progressCallback);

	/**
	 * Equivalent to {@code then(doneFilter, null, null)}
	 *
	 * @see #then(DoneFilter, FailFilter, ProgressFilter)
	 * @param doneFilter the filter to execute when a result is available
	 * @return a new promise for the filtered result
	 */
	<D_OUT> Promise<D_OUT, F, P> then(DoneFilter<? super D, ? extends D_OUT> doneFilter);

	/**
	 * Equivalent to {@code then(doneFilter, failFilter, null)}
	 *
	 * @see #then(DoneFilter, FailFilter, ProgressFilter)
	 * @param doneFilter the filter to execute when a result is available
	 * @param failFilter the filter to execute when a failure is available
	 * @return a new promise for the filtered result and failure.
	 */
	<D_OUT, F_OUT> Promise<D_OUT, F_OUT, P> then(
			DoneFilter<? super D, ? extends D_OUT> doneFilter,
			FailFilter<? super F, ? extends F_OUT> failFilter);

	/**
	 * This method will register filters such that when a Deferred object is either
	 * resolved ({@link Deferred#resolve(Object)}), rejected ({@link Deferred#reject(Object)}) or
	 * is notified of progress ({@link Deferred#notify(Object)}), the corresponding filter
	 * will be invoked.  The result of the filter will be used to invoke the same action on the
	 * returned promise.
	 *
	 * {@link DoneFilter} and {@link FailFilter} will be triggered at the time the Deferred object is
	 * resolved or rejected.  If the Deferred object is already resolved or rejected the filter is
	 * triggered immediately.
	 *
	 * Filters allow to transform the outcome of a promise into something else.  This concept is equivalent
	 * to the map() method of the java stream API.
	 *
	 * If any of the filter is not specified ({@code null}), a default No Op filter is used.
	 * If your filter is returning a {@link Promise} consider using {@link #then(DonePipe, FailPipe, ProgressPipe)}.
	 *
	 * <pre>
	 * <code>
	 * Deferred deferred = new DeferredObject();
	 * Promise promise = deferred.promise();
	 * Promise filtered = promise.then(new DoneFilter<Integer, Integer>() {
	 *   Integer filterDone(Integer result) {
	 *     return result * 10;
	 *   }
	 * });
	 *
	 * filtered.then(new DoneCallback<Integer>() {
	 *   void onDone(Integer result) {
	 *     System.out.println(result);
	 *   }
	 * });
	 *
	 * deferred.resolve(1); // prints 10
	 * </code>
	 * </pre>
	 *
	 * @param doneFilter the filter to execute when a result is available.
	 *                      If {@code null}, use {@link org.jdeferred2.impl.FilteredPromise.NoOpDoneFilter}
	 * @param failFilter the filter to execute when a failure is available.
	 *                      If {@code null}, use {@link org.jdeferred2.impl.FilteredPromise.NoOpFailFilter}
	 * @param progressFilter the filter to execute when progress info is available.
	 *                          If {@code null}, use {@link org.jdeferred2.impl.FilteredPromise.NoOpProgressFilter}
	 * @return a new promise for the filtered result, failure and progress.
	 */
	<D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<? super D, ? extends D_OUT> doneFilter,
			FailFilter<? super F, ? extends F_OUT> failFilter,
			ProgressFilter<? super P, ? extends P_OUT> progressFilter);

	/**
	 * Equivalent to {#code then(DonePipe, null, null)}
	 *
	 * @see #then(DonePipe, FailPipe, ProgressPipe)
	 * @param donePipe the pipe to invoke when a result is available
	 * @return a new promise for the piped result.
	 */
	<D_OUT> Promise<D_OUT, F, P> then(DonePipe<? super D, ? extends D_OUT, ? extends F, ? extends P> donePipe);

	/**
	 * Equivalent to {@code then(DonePipe, FailPipe, null)}
	 *
	 * @see #then(DonePipe, FailPipe, ProgressPipe)
	 * @param donePipe the pipe to invoke when a result is available
	 * @param failPipe the pipe to invoke when a failure is available
	 * @return a new promise for the piped result and failure.
	 */
	<D_OUT, F_OUT> Promise<D_OUT, F_OUT, P> then(
			DonePipe<? super D, ? extends D_OUT, ? extends F_OUT, ? extends P> donePipe,
			FailPipe<? super F, ? extends D_OUT, ? extends F_OUT, ? extends P> failPipe);

	/**
	 * This method will register pipes such that when a Deferred object is either
	 * resolved ({@link Deferred#resolve(Object)}), rejected ({@link Deferred#reject(Object)}) or
	 * is notified of progress ({@link Deferred#notify(Object)}), the corresponding pipe
	 * will be invoked.
	 *
	 * {@link DonePipe} and {@link FailPipe} will be triggered at the time the Deferred object is
	 * resolved or rejected.  If the Deferred object is already resolved or rejected the filter is
	 * triggered immediately.
	 *
	 * This method is similar to JQuery's pipe() method, where a new {@link Promise} is returned
	 * by the the pipe filter instead of the original.  This is useful to handle return values
	 * and then rewiring it to different callbacks.
	 *
	 * Pipes start a new {@link Deferred} object.  This allows to chain asynchronous calls.
	 *
	 * If your pipe does not do any asynchronous work consider using {@link #then(DoneFilter, FailFilter, ProgressFilter)}
	 *
	 * <pre>
	 * <code>
	 * promise.then(new DonePipe<Integer, Integer, String, Void>() {
	 *   {@literal @}Override
	 *   Deferred<Integer, Void, Void> pipeDone(Integer result) {
	 *     // Reject values greater than 100
	 *     if (result > 100) {
	 *       return new DeferredObject<Integer, Void, Void>().reject("Failed");
	 *     } else {
	 *       return new DeferredObject<Integer, Void, Void>().resolve(result);
	 *     }
	 *   }
	 * }).done(...)
	 * .fail(...);
	 * </code>
	 * </pre>
	 *
	 * @param donePipe the pipe to invoke when a result is available.
	 *                    If {@code null}, result is piped unchanged
	 * @param failPipe the pipe to invoke when a failure is available.
	 *                    If {@code null}, failure is piped unchanged
	 * @param progressPipe the pipe to execute when progress info is available.
	 *                        If {@code null}, progress is piped unchanged
	 * @return a new promise for the piped result, failure and progress.
	 */
	<D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<? super D, ? extends D_OUT, ? extends F_OUT, ? extends P_OUT> donePipe,
			FailPipe<? super F, ? extends D_OUT, ? extends F_OUT, ? extends P_OUT> failPipe,
			ProgressPipe<? super P, ? extends D_OUT, ? extends F_OUT, ? extends P_OUT> progressPipe);

	/**
	 * This method will register a pipe such that when a Deferred object is either
	 * resolved ({@link Deferred#resolve(Object)}) or rejected ({@link Deferred#reject(Object)})
	 * the pipe will be invoked.
	 *
	 * {@link AlwaysPipe} will be triggered at the time the Deferred object is
	 * resolved or rejected.  If the Deferred object is already resolved or rejected the filter is
	 * triggered immediately.
	 *
	 * This method is similar to JQuery's pipe() method, where a new {@link Promise} is returned
	 * by the the pipe filter instead of the original.  This is useful to handle return values
	 * and then rewiring it to different callbacks.
	 *
	 * Pipes start a new {@link Deferred} object.  This allows to chain asynchronous calls.
	 *
	 * <pre>
	 * <code>
	 * promise.always(new AlwaysPipe<Integer, Integer, String, String, Void>() {
	 *   {@literal @}Override
	 *   Promise<Integer, Void, Void> pipeAlways(State state, Integer resolved, Integer rejected) {
	 *     if (state == State.RESOLVED) {
	 *       return new DeferredObject<String, String, Void>().resolve("Success");
	 *     } else {
	 *       return new DeferredObject<String, String, Void>().reject("Failed");
	 *     }
	 *   }
	 * }).done(...)
	 * .fail(...);
	 * </code>
	 * </pre>
	 *
	 * @since 2.0
	 * @param alwaysPipe the pipe to invoke when a result or failure is available.
	 * @return a new promise for the piped result or failure.
	 */
	<D_OUT, F_OUT> Promise<D_OUT, F_OUT, P> always(
			AlwaysPipe<? super D, ? super F, ? extends D_OUT, ? extends F_OUT, ? extends P> alwaysPipe);

	/**
	 * This method will register {@link DoneCallback} so that when a Deferred object
	 * is resolved ({@link Deferred#resolve(Object)}), {@link DoneCallback} will be triggered.
	 * If the Deferred object is already resolved then the {@link DoneCallback} is triggered immediately.
	 *
	 * You can register multiple {@link DoneCallback} by calling the method multiple times.
	 * The order of callback trigger is based on the order they have been registered.
	 *
	 * <pre>
	 * <code>
	 * promise.progress(new DoneCallback(){
	 *   public void onDone(Object done) {
	 *     ...
	 *   }
	 * });
	 * </code>
	 * </pre>
	 *
	 * @see Deferred#resolve(Object)
	 * @param callback the callback to be triggered
	 * @return {@code this} for chaining more calls
	 */
	Promise<D, F, P> done(DoneCallback<? super D> callback);

	/**
	 * This method will register {@link FailCallback} so that when a Deferred object
	 * is rejected ({@link Deferred#reject(Object)}), {@link FailCallback} will be triggered.
	 * If the Deferred object is already rejected then the {@link FailCallback} is triggered immediately.
	 *
	 * You can register multiple {@link FailCallback} by calling the method multiple times.
	 * The order of callback trigger is based on the order they have been registered.
	 *
	 * <pre>
	 * <code>
	 * promise.fail(new FailCallback(){
	 *   public void onFail(Object rejection) {
	 *     ...
	 *   }
	 * });
	 * </code>
	 * </pre>
	 *
	 * @see Deferred#reject(Object)
	 * @param callback the callback to be triggered
	 * @return {@code this} for chaining more calls
	 */
	Promise<D, F, P> fail(FailCallback<? super F> callback);

	/**
	 * This method will register {@link AlwaysCallback} so that when a Deferred object is either
	 * resolved ({@link Deferred#resolve(Object)}) or rejected ({@link Deferred#reject(Object)}),
	 * {@link AlwaysCallback} will be triggered.
	 * If the Deferred object is already resolved or rejected then the {@link AlwaysCallback} is
	 * triggered immediately.
	 *
	 * You can register multiple {@link AlwaysCallback} by calling the method multiple times.
	 * The order of callback trigger is based on the order they have been registered.
	 *
	 * {@link AlwaysCallback}s are triggered after any {@link DoneCallback} or {@link FailCallback}
	 * respectively.
	 *
	 * <pre>
	 * <code>
	 * promise.always(new AlwaysCallback(){
	 *   public void onAlways(State state, Object result, Object rejection) {
	 *     if (state == State.RESOLVED) {
	 *       // do something with result
	 *     } else {
	 *       // do something with rejection
	 *     }
	 *   }
	 * });
	 * </code>
	 * </pre>
	 *
	 * @see Deferred#resolve(Object)
	 * @see Deferred#reject(Object)
	 * @param callback the callback to be triggered
	 * @return {@code this} for chaining more calls
	 */
	Promise<D, F, P> always(AlwaysCallback<? super D, ? super F> callback);

	/**
	 * This method will register {@link ProgressCallback} so that when a Deferred object
	 * is notified of progress ({@link Deferred#notify(Object)}), {@link ProgressCallback} will be triggered.
	 *
	 * You can register multiple {@link ProgressCallback} by calling the method multiple times.
	 * The order of callback trigger is based on the order they have been registered.
	 *
	 * <pre>
	 * <code>
	 * promise.progress(new ProgressCallback(){
	 *   public void onProgress(Object progress) {
	 *     // e.g., update progress in the GUI while the background task is still running.
	 *   }
	 * });
	 * </code>
	 * </pre>
	 *
	 * @see Deferred#notify(Object)
	 * @param callback the callback to be triggered
	 * @return {@code this} for chaining more calls
	 */
	Promise<D, F, P> progress(ProgressCallback<? super P> callback);

	/**
	 * This method will wait as long as the State is Pending.  This method will return fast
	 * when State is not Pending.
	 *
	 * @throws InterruptedException if thread is interrupted while waiting
	 */
	void waitSafely() throws InterruptedException;

	/**
	 * This method will wait when the State is Pending, and return when timeout has reached.
	 * This method will return fast when State is not Pending.
	 *
	 * @param timeout the maximum time to wait in milliseconds
	 * @throws InterruptedException if thread is interrupted while waiting
	 */
	void waitSafely(long timeout) throws InterruptedException;
}
