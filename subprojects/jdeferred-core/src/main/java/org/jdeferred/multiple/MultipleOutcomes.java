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
public class MultipleOutcomes<T extends OneOutcome> implements Iterable<T> {

    protected final List<T> results;

    public MultipleOutcomes(int size) {
        this.results = new CopyOnWriteArrayList(new Object[size]);
    }

    protected void set(int index, T result) {
        results.set(index, result);
    }

    public T get(int index) {
        return results.get(index);
    }

    public Iterator<T> iterator() {
        return results.iterator();
    }

    public int size() {
        return results.size();
    }

    @Override
    public String toString() {
        return "MultipleResults [results=" + results + "]";
    }
}
