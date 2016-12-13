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
package org.jdeferred.es6;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Promise<D, F> implements Runnable {
    public enum State {
        PENDING,
        FULFILLED,
        REJECTED
    }

    private static final ExecutorService es = Executors.newCachedThreadPool();

    private final BiConsumer<Consumer<D>, Consumer<F>> executor;
    private final List<Consumer<D>> fullfillCallbacks = new LinkedList<>();
    private final List<Consumer<F>> rejectCallbacks = new LinkedList<>();
    private final AtomicReference<State> state = new AtomicReference<>(State.PENDING);
    private D result;
    private F reason;

    public Promise(BiConsumer<Consumer<D>, Consumer<F>> executor) {
        this.executor = executor;
        es.submit(this);
    }

    @Override
    public void run() {
        executor.accept(d -> {
            synchronized (this.state) {
                if (this.state.compareAndSet(State.PENDING, State.FULFILLED)) {
                    this.result = d;
                    this.triggerFulfillCallbacks();
                }
            }
        }, f -> {
            synchronized (this.state) {
                if (this.state.compareAndSet(State.PENDING, State.REJECTED)) {
                    this.reason = f;
                    this.triggerRejectCallbacks();
                }
            }
        });
    }

    protected void triggerFulfillCallbacks() {
        this.fullfillCallbacks.forEach(f -> f.accept(this.result));
    }

    protected void triggerRejectCallbacks() {
        this.rejectCallbacks.forEach(f -> f.accept(this.reason));
    }

    public Promise<D, F> then(Consumer<D> onFullfilled) {
        this.fullfillCallbacks.add(onFullfilled);
        synchronized (this.state) {
            if (this.state.get() == State.FULFILLED) {
                this.triggerFulfillCallbacks();
            }
        }
        return this;
    }

    public Promise<D, F> then(Consumer<D> onFulfilled, Consumer<F> onRejected) {
        this.rejectCallbacks.add(onRejected);
        synchronized (this.state) {
            if (this.state.get() == State.REJECTED) {
                this.triggerRejectCallbacks();
            }
        }
        return this;
    }

    public Promise<D, F> catches(Consumer<F> onRejected) {
        this.rejectCallbacks.add(onRejected);
        synchronized (this.state) {
            if (this.state.get() == State.REJECTED) {
                this.triggerRejectCallbacks();
            }
        }
        return this;
    }

    public static <D, F> Promise<Collection<D>, F> all(Promise<D, F>... promises) {
        return all(Arrays.asList(promises));
    }

    public static <D, F> Promise<Collection<D>, F> all(Collection<Promise<D, F>> promises) {
        return new Promise<Collection<D>, F>((resolve, reject) -> {
            final AtomicBoolean rejected = new AtomicBoolean(false);
            final AtomicInteger fullfilledCount = new AtomicInteger(0);
            List<D> results = new ArrayList<>(promises.size());
            promises.forEach(p -> {
                p.then(r -> {
                    if (!rejected.get()) {
                        results.add(r);
                        if (promises.size() == fullfilledCount.incrementAndGet()) {
                            resolve.accept(results);
                        }
                    }
                }).catches(e -> {
                    if (rejected.compareAndSet(false, true))
                        reject.accept(e);
                });
            });
        });
    }

    public static <D, Exception> Promise<D, Exception> reject(Exception e) {
        return new Promise<D, Exception>((resolve, reject) -> {
            reject.accept(e);
        });
    }

    public static <D, F> Promise<D, F> resolve(D value) {
        return new Promise<D, F>((resolve, reject) -> {
            resolve.accept(value);
        });
    }

    public static <D, F> Promise<D, F> race(Promise<D, F>... promises) {
        return race(Arrays.asList(promises));
    }

    public static <D, F> Promise<D, F> race(Collection<Promise<D, F>> promises) {
        return new Promise<D, F>((resolve, reject) -> {
            final AtomicReference<State> state = new AtomicReference<>(State.PENDING);
            promises.forEach(p -> {
                p.catches(e -> {
                    if (state.compareAndSet(State.PENDING, State.REJECTED))
                        reject.accept(e);
                });
                p.then(r -> {
                    if (state.compareAndSet(State.PENDING, State.FULFILLED))
                        resolve.accept(r);
                });
            });
        });
    }

    public static void main(String[] args) {
        Promise<String, Void> p = new Promise<>(
                (resolve, reject) -> {
                    resolve.accept("Hello");

                }
        );

        p.then(System.out::println);

        Promise<Void, Exception> p2 = new Promise<>(
                (resolve, reject) -> {
                    reject.accept(new Exception("ugh"));
                }
        );
        p2.catches(e -> e.printStackTrace());


        List<Promise<Integer, Void>> ps = IntStream.range(0, 10).mapToObj(i -> new Promise<Integer, Void>(
                (resolve, reject) -> {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                    }
                    resolve.accept(i);
                }
        )).collect(Collectors.toList());

        Promise.reject(new Exception("oops")).catches(e -> e.printStackTrace());

        Promise.all(ps).then(r -> r.forEach(System.out::println));

        Promise.race(ps).then(System.out::println);
    }
}
