/*
 * Copyright 2013-2018 Ray Tsang
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
 * A pipe invoked when the {@code Promise}'s state becomes {@link Promise.State#RESOLVED}.
 * Pipes allow to start a new {@link Deferred} and any state change or update invoked on the new {@code Deferred} is
 * piped to the outer {@code Promise}.  This allows to chain asynchronous calls or convert a rejection into resolve
 * or vice versa.
 *
 * @param <D>     Type of the input
 * @param <D_OUT> Type of the output from this pipe
 * @param <F_OUT> Type of the failure output from this pipe
 * @param <P_OUT> Type of the progress output from this pipe
 *
 * @author Ray Tsang
 * @see Promise#pipe(DonePipe)
 * @see Promise#pipe(DonePipe, FailPipe)
 * @see Promise#pipe(DonePipe, FailPipe, ProgressPipe)
 */
public interface DonePipe<D, D_OUT, F_OUT, P_OUT> {
	Promise<D_OUT, F_OUT, P_OUT> pipeDone(final D result);
}
