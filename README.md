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

<a name="features"></a>Features
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
  * ```deferred.notify(0.80);```
* Android Support
* Java 8 Lambda friendly
* Yes it's on Maven Central Repository!

Maven
-----
```xml
<dependency>
    <groupId>org.jdeferred</groupId>
    <artifactId>jdeferred-core</artifactId>
    <version>${version}</version>
</dependency>
```

Gradle
-----
```
compile 'org.jdeferred:jdeferred-core:${version}'
```

Find available versions on [Maven Central Repository](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.jdeferred%22%20AND%20a%3A%22jdeferred-core%22).

<a name="examples"></a>Quick Examples
==============

<a name="examples-deferred-promise"></a>Deferred object and Promise
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
deferred.notify("100%");
```

<a name="example-filter"></a>Filter
-----------
```java
Deferred d = …;
Promise p = d.promise();
Promise filtered = p.then(new DoneFilter<Integer, Integer>() {
  public Integer filterDone(Integer result)
    return result * 10;
  }
});

filtered.done(new DoneCallback<Integer>() {
  public void onDone(Integer result) {
    // result would be original * 10
    System.out.println(result);
  }
});

d.resolve(3) -> 30.
```

<a name="example-pipe"></a>Pipe
----
> Since 1.1.0-Beta1

```java
Deferred d = ...;
Promise p = d.promise();

p.then(new DonePipe<Integer, Integer, Exception, Void>() {
  public Deferred<Integer, Exception, Void> pipeDone(Integer result) {
    if (result < 100) {
      return new DeferredObject<Integer, Void, Void>().resolve(result);
    } else {
      return new DeferredObject<Integer, Void, Void>().reject(new Exception(...));
    }
  }
}).done(...).fail(...);

d.resolve(80) -> done!
d.resolve(100) -> fail!

```

<a name="example-dm"></a>Deferred Manager
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

<a name="example-runnable-callable"></a>Runnable and Callable
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

<a name="example-wait"></a>Wait and WaitSafely
-------------------
> Since 1.0.1

Normally, when using this framework, you would want to do things asynchronously.  However, if there is a need to wait for all deferred tasks to finish, you can use Object.wait or Promise.waitSafely methods.

```java
Promise p = dm.when(...)
  .done(...)
  .fail(...)

synchronized (p)
  while (p.isPending()) {
    try {
      p.wait();
    } catch (InterruptedException e) { ... }
  }
}
```

Alternatively, you can use a more simplified shortcut
```java
Promise p = dm.when(...)
  .done(...)
  .fail(...)

try {
  p.waitSafely();
} catch (InterruptedException e) {
  ... 
}
```
<a name="example-lambda"></a>Java 8 Lambda
-------------
Now this is pretty cool when used with Java 8 Lambda!

```Java
dm.when(() -> {
  return "Hey!";
}).done(r -> System.out.println(r));

dm.when(
  () -> { return "Hello"; },
  () -> { return "World"; }
).done(rs ->
  rs.forEach(r -> System.out.println(r.getResult()))
);
```

<a name="example-groovy"></a>Groovy
-----
You can also easily use with Groovy!

```Groovy
@Grab('org.jdeferred:jdeferred-core:1.2.6')
import org.jdeferred.*
import org.jdeferred.impl.*

def deferred = new DeferredObject()
def promise = deferred.promise()

promise.done { result ->
  println "done: $result" 
}.fail { rejection ->
  println "fail: $rejection"
}.always { state, result, rejection ->
  println "always"
}

deferred.resolve("done")
```

<a name="example-android"></a>Android Support
---------------
> Since 1.1.0-Beta1

```jdeferred-android``` is now available, and it can be included just like any other Android libraries!
It also uses Android Maven plugin and builds apklib file.  If you use Android Maven plugin, you can include
dependency:

APKLIB with Maven:
```xml
<dependency>
  <groupId>org.jdeferred</groupId>
  <artifactId>jdeferred-android</artifactId>
  <version>${version}</version>
  <type>apklib</type>
</dependency>
```


AAR with Maven:
> Since 1.2.0-Beta1

```xml
<dependency>
  <groupId>org.jdeferred</groupId>
  <artifactId>jdeferred-android-aar</artifactId>
  <version>${version}</version>
  <type>aar</type>
</dependency>
```

AAR with Gradle:
```
compile 'org.jdeferred:jdeferred-android-aar:${version}'
// or
compile 'org.jdeferred:jdeferred-android-aar:${version}@aar'
```

Find available versions on [Maven Central Repository](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.jdeferred%22%20AND%20a%3A%22jdeferred-core%22).

```jdeferred-android``` introduces a new ```DeferredManager``` implementation called ```AndroidDeferredManager```.
```AndroidDeferredManager``` makes sure that callbacks are executed in UI Thread rather than background Thread
in order for callbacks to make UI updates.  Alternatively, callbacks can also implement ```AndroidExecutionScopeable```
interface to fine-grain control whether the callback should execute in UI Thread or background Thread.

```AndroidDeferredManager``` also supports new ```DeferredAsyncTask``` object.  This object is based on 
Android's ```AsyncTask```.

If you need to always execute callbacks in background thread, then you can continue to use ```DefaultDeferredManager```.

Lastly, because JDeferred use SLF4J - you can further route log messages using ```slf4j-android```.


<a name="example-async-servlet"></a>Asynchronous Servlet
--------------------
Here is a sample code on how to use JDeferred with Asynchronous Servlet!

```java
@WebServlet(value = "/AsyncServlet", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private ExecutorService executorService = Executors.newCachedThreadPool();
  private DeferredManager dm = new DefaultDeferredManager(executorService);

  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
    final AsyncContext actx = request.startAsync(request, response);
    
    dm.when(new Callable<String>() {
      @Override
      public String call() throws Exception {
        if (actx.getRequest().getParameter("fail") != null) {
          throw new Exception("oops!");
        }
        Thread.sleep(2000);
        return "Hello World!";
      }
    }).then(new DoneCallback<String>() {
      @Override
      public void onDone(String result) {
        actx.getRequest().setAttribute("message", result);
        actx.dispatch("/hello.jsp");
      }
    }).fail(new FailCallback<Throwable>() {
      @Override
      public void onFail(Throwable exception) {
        actx.getRequest().setAttribute("exception", exception);
        actx.dispatch("/error.jsp");
      }
    });
  }
}
```
<!-- Google Code for GitHub Visit Conversion Page -->
<script type="text/javascript">
/* <![CDATA[ */
var google_conversion_id = 974052972;
var google_conversion_language = "en";
var google_conversion_format = "3";
var google_conversion_color = "ffffff";
var google_conversion_label = "wsVZCOycvgkQ7Ly70AM";
var google_conversion_value = 0;
var google_remarketing_only = false;
/* ]]> */
</script>
<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
</script>
<noscript>
<div style="display:inline;">
<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/974052972/?value=0&amp;label=wsVZCOycvgkQ7Ly70AM&amp;guid=ON&amp;script=0"/>
</div>
</noscript>

<a name="deprecations"></a>Deprecations
==============

<a name="deprecations-v1.2.5"></a>v1.2.5
--------
* ~~```DeferredManager.StartPolicy.MANAUL```~~ is deprecated and will be removed in the next minor version. Use ```DeferredManager.StartPolicy.MANUAL``` instead.
