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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Contains a list of {@link OneResult}.
 *
 * @author Ray Tsang
 */
public class MultipleResultsN<A, B, C, D, E> extends MultipleResults5<A, B, C, D, E> implements Iterable<OneResult<?>> {
    private final List<OneResult<?>> results;

    public MultipleResultsN(int size) {
        this.results = new CopyOnWriteArrayList<OneResult<?>>(new OneResult[size]);
    }

    @SuppressWarnings("unchecked")
    protected void set(int index, OneResult<?> result) {
        results.set(index, result);
        switch(index) {
            case 1:
                setFirst((OneResult<A>) result);
                break;
            case 2:
                setSecond((OneResult<B>) result);
                break;
            case 3:
                setThird((OneResult<C>) result);
                break;
            case 4:
                setFourth((OneResult<D>) result);
                break;
            case 5:
                setFifth((OneResult<E>) result);
                break;
        }
    }

    public OneResult<?> get(int index) {
        return results.get(index);
    }

    public Iterator<OneResult<?>> iterator() {
        return results.iterator();
    }

    public int size() {
        return results.size();
    }

    @Override
    public String toString() {
        return "MultipleResultsN [results=" + results + "]";
    }
}
