/*
 * Copyright 2014 Tristan Burch
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred.multiple;

import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

/**
 * This will return a special Promise called {@link org.jdeferred.multiple.MasterAnyDeferredObject}. In short,
 * <ul>
 * <li>{@link org.jdeferred.Promise#done(org.jdeferred.DoneCallback)} will be triggered if all promises resolves
 * (i.e., all finished successfully) with {@link org.jdeferred.multiple.OneResult}.</li>
 * <li>{@link org.jdeferred.Promise#progress(org.jdeferred.ProgressCallback)} will be triggered whenever one
 * promise resolves or rejects ({#link {@link org.jdeferred.multiple.MasterProgress}),
 * or whenever a promise was notified progress ({@link org.jdeferred.multiple.OneProgress}).</li>
 * <li>{@link org.jdeferred.Promise#always(org.jdeferred.AlwaysCallback)} will be triggered whenever
 * {@link org.jdeferred.Promise#done(org.jdeferred.DoneCallback)} or
 * {@link org.jdeferred.Promise#fail(org.jdeferred.FailCallback)} would be triggered</li>
 * </ul>
 *
 * @author Tristan Burch
 */
@SuppressWarnings("rawtypes")
public class MasterAnyDeferredObject extends
        DeferredObject<OneResult, OneReject, MasterProgress>
        implements Promise<OneResult, OneReject, MasterProgress> {
    private final int numberOfPromises;
    private final AtomicInteger doneCount = new AtomicInteger();
    private final AtomicInteger failCount = new AtomicInteger();
    private final MultipleRejects rejects;

    @SuppressWarnings("unchecked")
    public MasterAnyDeferredObject(Promise... promises) {
        if (promises == null || promises.length == 0)
            throw new IllegalArgumentException("Promises is null or empty");
        this.numberOfPromises = promises.length;

        this.rejects = new MultipleRejects(promises.length);

        int count = 0;
        for (final Promise promise : promises) {
            final int index = count++;
            promise
                    .fail(new FailCallback<Object>() {
                        public void onFail(Object result) {
                            synchronized (MasterAnyDeferredObject.this) {
                                rejects.set(index, new OneReject(index, promise, result));

                                if (!MasterAnyDeferredObject.this.isPending())
                                    return;

                                final int fail = failCount.incrementAndGet();
                                MasterAnyDeferredObject.this.notify(new MasterProgress(
                                        doneCount.get(),
                                        fail,
                                        numberOfPromises));
                            }
                        }
                    })
                    .progress(new ProgressCallback() {
                        public void onProgress(Object progress) {
                            synchronized (MasterAnyDeferredObject.this) {
                                if (!MasterAnyDeferredObject.this.isPending())
                                    return;

                                MasterAnyDeferredObject.this.notify(new OneProgress(
                                        doneCount.get(),
                                        failCount.get(),
                                        numberOfPromises, index, promise, progress));
                            }
                        }
                    })
                    .done(new DoneCallback() {
                        public void onDone(Object result) {
                            synchronized (MasterAnyDeferredObject.this) {
                                if (MasterAnyDeferredObject.this.isPending()) {
                                    OneResult theResult = new OneResult(index, promise, result);
                                    int done = doneCount.incrementAndGet();

                                    MasterAnyDeferredObject.this.notify(new MasterProgress(
                                            done,
                                            failCount.get(),
                                            numberOfPromises));

                                    MasterAnyDeferredObject.this.resolve(theResult);
                                    return;
                                }
                            }
                        }
                    });
        }
    }

    public MultipleRejects getRejects() { return rejects; }

}