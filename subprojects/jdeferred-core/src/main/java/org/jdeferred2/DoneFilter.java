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
 * A filter invoked when the {@code Promise}'s state becomes {@link Promise.State#RESOLVED}.
 * Filters allow to convert a value into a new value. This has to happen synchronous.
 * For asynchronous calls see {@link DonePipe}.
 *
 * @param <D>     Type of the input
 * @param <D_OUT> Type of the output from this filter
 *
 * @author Ray Tsang
 * @see Promise#filter(DoneFilter)
 * @see Promise#filter(DoneFilter, FailFilter)
 * @see Promise#filter(DoneFilter, FailFilter, ProgressFilter)
 */
public interface DoneFilter<D, D_OUT> {
	D_OUT filterDone(final D result);
}
