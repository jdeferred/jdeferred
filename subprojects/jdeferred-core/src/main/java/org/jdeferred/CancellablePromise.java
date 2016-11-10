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
package org.jdeferred;

/**
 * Promise interface that adds cancellation capabilities.
 * <p>
 * Here's one usage pattern for this new type:
 * <p>
 * <pre>
 * <code>
 * DeferredFutureTask<Collection<Bean>, Bean> futureTask = new DeferredFutureTask<>(new DeferredCallable<Collection<Bean>, Bean>() {
 *     public Collection<Bean> call() throws Exception {
 *         // your logic goes here
 *     }
 * });
 * CancellablePromise<Collection<Bean>, Throwable, Bean> promise = new DefaultCancellablePromise<>(deferredManager.when(futureTask), futureTask);
 * // at some point in time later
 * promise.cancel();
 * </code>
 * </pre>
 *
 * @param <D> Type used for {@link #done(DoneCallback)}
 * @param <F> Type used for {@link #fail(FailCallback)}
 * @param <P> Type used for {@link #progress(ProgressCallback)}
 *
 * @author Andres Almiray
 * @since 1.2.5
 */
public interface CancellablePromise<D, F, P> extends Promise<D, F, P> {
    /**
     * Cancels the computation task wrapped by this Promise.
     */
    void cancel();
}
