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
package org.jdeferred.impl;

import org.jdeferred.CancellablePromise;
import org.jdeferred.Deferred;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CancellablePromiseTest extends AbstractDeferredTest {
    private ExecutorService executorService;

    @Test
    public void testCancelInMidWork() {
        // given:
        final Collection<Person> people = new ArrayList<Person>();
        people.add(new Person(1, "Alice"));
        people.add(new Person(2, "Bob"));
        people.add(new Person(3, "Charles"));
        people.add(new Person(4, "David"));
        people.add(new Person(5, "Edward"));
        people.add(new Person(6, "Ajax")); // what? where you expecting Francis?
        people.add(new Person(7, "Ray"));
        people.add(new Person(8, "Andres"));
        final Deferred<Collection<Person>, Throwable, Person> promise = new DeferredObject<Collection<Person>, Throwable, Person>();
        final CancellablePromiseStub<Collection<Person>, Throwable, Person> stub = new CancellablePromiseStub<Collection<Person>, Throwable, Person>(promise);

        final Collection<Person> result = new ArrayList<Person>();
        stub.progress(new ProgressCallback<Person>() {
            @Override
            public void onProgress(Person person) {
                result.add(person);
            }
        });

        // when:
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (Person person : people) {
                    if (!stub.cancelled.get()) {
                        pause(200L, MILLISECONDS);
                        promise.notify(person);
                    }
                }
                promise.resolve(people);
            }
        });
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                pause(600L, MILLISECONDS);
                stub.cancel();
                return null;
            }
        });

        await().timeout(5L, SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return stub.cancelled.get();
            }
        });

        // then:
        assertThat(result.size(), greaterThanOrEqualTo(1));
        assertThat(result.size(), lessThan(8));
    }

    private void pause(long duration, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(duration));
        } catch (InterruptedException ignored) {
            // ok
        }
    }

    private static class CancellablePromiseStub<D, F, P> extends DelegatingPromise<D, F, P> implements CancellablePromise<D, F, P> {
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        public CancellablePromiseStub(Promise<D, F, P> delegate) {
            super(delegate);
        }

        @Override
        public void cancel() {
            cancelled.set(true);
        }
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        executorService = Executors.newFixedThreadPool(2);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        executorService.shutdownNow();
        super.tearDown();
    }

    private static class Person {
        private final int id;
        private final String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
