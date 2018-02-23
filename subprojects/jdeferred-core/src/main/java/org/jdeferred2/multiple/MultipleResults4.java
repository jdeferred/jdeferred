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
package org.jdeferred2.multiple;

/**
 * Contains 4 results, typed <tt>V1</tt>, <tt>V2</tt>, <tt>V3</tt>, <tt>V4</tt> wrapped in {@link OneResult}.
 *
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 * @since 2.0
 */
public interface MultipleResults4<V1, V2, V3, V4> extends MultipleResults {
	OneResult<V1> getFirst();

	OneResult<V2> getSecond();

	OneResult<V3> getThird();

	OneResult<V4> getFourth();
}
