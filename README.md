<!--
  Copyright 2013 Ray Tsang
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
    http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

JDeferred
=========

JDeferred is a Java Deferred/Promise library similar to JQuery's Deferred Object.

Inspired by [JQuery](https://github.com/jquery/jquery) and [Android Deferred Object](https://github.com/CodeAndMagic/android-deferred-object).

Please see [jdeferred.org](http://jdeferred.org) for more documentation.

Features
--------
* Deferred object and Promise
* Promise callbacks
  * ```.then(…)```
  * ```.done(…)```
  * ```.fail(…)```
  * ```.progress(…)```
  * ```.always(…)```
* Multiple promises
  * ```.when(p1, p2, p3, …).then(…)```
* Callable and Runnable wrappers
  * ```.when(new Runnable() {…})```
* Uses Executor Service
* Java Generics support
  * ```Deferred<Integer, Exception, Double> deferred;```
  * ```deferred.resolve(10);```
  * ```deferred.reject(new Exception());```
  * ```deferred.progress(0.80);```

  

Quick Examples
==============

Deferred object and Promise
---------------------------

```java
Deferred deferred = new DeferredObject();
Promise promise = deferred.promise();
promise.done(new DoneCallback() {
  public void onDone(Object result) {
    ...
  }
}).fail(new FailCallback() {
  public void onFail(Object rejection) {
    ...
  }
}).progress(new ProgressCallback() {
  public void onProgress(Object progress) {
    ...
  }
}).always(new AlwaysCallback() {
  public void onAlways(State state, Object result, Object rejection) {
    ...
  }
});
```
With the reference to deferred object, you can then trigger actions/updates:

```java
deferred.resolve("done");
deferred.reject("oops");
deferred.progress("100%");
```

Filter/Pipe
-----------
```java
Deferred d = …;
Promise p = d.promise();
Promise filtered = p.then(new DoneFilter<Integer, Integer>(){
  public Integer filterDone(Integer result) P
    return result * 10;
  }
});

filtered.done(new DoneCallback<Integer>{
  public void onDone(Integer result) {
    // result would be original * 10
    System.out.println(result);
  }
});

d.resolve(3) -> 30.

```

Deferred Manager
----------------
```java
DeferredManager dm = new DefaultDeferredManager();
Promise p1, p2, p3;
// initialize p1, p2, p3
dm.when(p1, p2, p3)
  .done(…)
  .fail(…)
```
You can also specify a Executor Service for your need.

```
DeferredManager dm = new DefaultDeferredManager(myExecutorService);
```

Runnable and Callable
---------------------
You can use Callable and Runnable almost like a Promise without any additional work.

```java
DeferredManager dm = new DefaultDeferredManager();
dm.when(new Callable<Integer>(){
  public Integer call() {
    // return something
    // or throw a new exception
  }
}).done(new DoneCallback<Integer>() {
  public void onDone(Integer result) {
    ...
  }
}).fail(new FailCallback<Throwable>() {
  public void onFail(Throwable e) {
    ...
  }
});
```

If you need to notify progress within your Callable or Runnable, you either need to create your own Deferred object and Promise, or you can use DeferredCallable and DeferredRunnable.

Use your own Deferred object

```java
final Deferred deferred = ...
Promise promise = deferred.promise();
promise.then(…);
Runnable r = new Runnable() {
  public void run() {
    while (…) {
      deferred.notify(myProgress);
    }
    deferred.resolve("done");
  }
}
```

Or, extending DeferredRunnable

```java
DeferredManager dm = …;
dm.when(new DeferredRunnable<Double>(){
  public void run() {
    while (…) {
      notify(myProgress);
    }
  }
}).then(…);
```
