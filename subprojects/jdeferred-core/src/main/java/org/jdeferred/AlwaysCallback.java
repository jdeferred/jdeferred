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
package org.jdeferred;

import org.jdeferred.Promise.State;

/**
 * A callback that's invoked regardless of the success or fail state of a {@code Promise}.
 *
 * @param <D> Type used for {@link Deferred#resolve(Object)}
 * @param <R> Type used for {@link Deferred#reject(Object)}
 *
 * @author Ray Tsang
 * @see Promise#always(AlwaysCallback)
 */
public interface AlwaysCallback<D, R> {
	/**
	 * Invoked when the {@code Promise} resolves or rejects a value.
	 *
	 * @param state    the state of the {@code Promise}
	 * @param resolved the resolved value (if any) of the {@code Promise}
	 * @param rejected the rejected value (if any) of the {@code Promise}
	 */
	void onAlways(final State state, final D resolved, final R rejected);
}
