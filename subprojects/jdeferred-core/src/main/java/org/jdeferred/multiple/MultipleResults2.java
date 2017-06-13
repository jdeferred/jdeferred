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
public class MultipleResults2<A, B> {
    protected OneResult<A> a;
    protected OneResult<B> b;

    protected void setFirst(OneResult<A> a) {
        this.a = a;
    }

    protected void setSecond(OneResult<B> b) {
        this.b = b;
    }

    public OneResult<A> getFirst() {
        return a;
    }

    public OneResult<B> getSecond() {
        return b;
    }

    public int size() {
        return 2;
    }

    @Override
    public String toString() {
        return "MultipleResults2 [a=" + a + ", b=" + b + "]";
    }
}
