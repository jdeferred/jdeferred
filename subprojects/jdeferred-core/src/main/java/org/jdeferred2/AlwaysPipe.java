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

import org.jdeferred2.Promise.State;

/**
 * A pipe invoked when the {@code Promise}'s state becomes {@link Promise.State#RESOLVED} or {@link Promise.State#REJECTED}.
 * Pipes allow to start a new {@link Deferred} and any state change or update invoked on the new {@code Deferred} is
 * piped to the outer {@code Promise}.  This allows to chain asynchronous calls or convert a rejection into resolve
 * or vice versa.
 *
 * @param <D>     Type of the input
 * @param <F>     Type of the failure input
 * @param <D_OUT> Type of the output from this pipe
 * @param <F_OUT> Type of the failure output from this pipe
 * @param <P>     Type of the progress output from this pipe
 *
 * @author Stephan Classen
 * @since 2.0
 * @see Promise#always(AlwaysPipe)
 */
public interface AlwaysPipe<D, F, D_OUT, F_OUT, P> {

	/**
	 * Invoked when the {@code Promise} resolves or rejects a value.
	 *
	 * @param state    the state of the {@code Promise}. Either {@link State#RESOLVED} or {@link State#REJECTED}
	 * @param resolved the resolved value (if any) of the {@code Promise}
	 * @param rejected the rejected value (if any) of the {@code Promise}
	 */
	Promise<D_OUT, F_OUT, P> pipeAlways(final State state, final D resolved, final F rejected);
}
