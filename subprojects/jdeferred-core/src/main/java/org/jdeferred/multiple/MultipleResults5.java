/*
 * Copyright 2013-2016 Ray Tsang
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
package org.jdeferred.multiple;

/**
 * Contains a list of {@link OneResult}.
 *
 * @author Ray Tsang
 */
public class MultipleResults5<A, B, C, D, E> extends MultipleResults4<A, B, C, D> {
    protected OneResult<E> e;

    protected void setFifth(OneResult<E> e) {
        this.e = e;
    }

    public OneResult<E> getFifth() {
        return e;
    }

    public int size() {
        return 5;
    }

    @Override
    public String toString() {
        return "MultipleResults5 [a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + ", e=" + e + "]";
    }
}
