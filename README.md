# 此项目仿照RxAndroid编写，目的是方便在Harmony上可以方便的使用Rxjava相关库。

暂时只支持Rxjava2，关于LICENSE我不太懂，如果有问题，请及时联系我，我会及时修改。

# RxHarmony: Reactive Extensions for Harmony

Harmony specific bindings for [RxJava 2](http://github.com/ReactiveX/RxJava).

This module adds the minimum classes to RxJava that make writing reactive components in Harmony
applications easy and hassle-free. More specifically, it provides a `Scheduler` that schedules on
the main thread or any given `EventRunner`.

# Sample usage

A sample project which provides runnable code examples that demonstrate uses of the classes in this
project is available in the `sample-app/` folder.

## Observing on the main thread

One of the most common operations when dealing with asynchronous tasks on Harmony is to observe the task's
result or outcome on the main thread. Using vanilla Harmony, this would typically be accomplished with an
`TaskDispatcher`. With RxJava instead you would declare your `Observable` to be observed on the main thread:

```java
Observable.just("one", "two", "three", "four", "five")
    .subscribeOn(Schedulers.newThread())
    .observeOn(HarmonySchedulers.mainThread())
    .subscribe(/* an Observer */);
```

This will execute the `Observable` on a new thread, and emit results through `onNext` on the main thread.

## Observing on arbitrary loopers

The previous sample is merely a specialization of a more general concept: binding asynchronous
communication to an Harmony message loop, or `EventRunner`. In order to observe an `Observable` on an arbitrary
`EventRunner`, create an associated `Scheduler` by calling `HarmonySchedulers.from`:

```java
EventRunner backgroundEventRunner = // ...
Observable.just("one", "two", "three", "four", "five")
    .observeOn(HarmonySchedulers.from(backgroundEventRunner))
    .subscribe(/* an Observer */)
```

This will execute the Observable on a new thread and emit results through `onNext` on whatever thread is
running `backgroundEventRunner`.


## Bugs and Feedback

For bugs, feature requests, and discussion please use [GitHub Issues][issues].

## LICENSE

    Copyright 2015 The RxAndroid authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [list]: http://groups.google.com/d/forum/rxjava
 [so]: http://stackoverflow.com/questions/tagged/rx-android
 [twitter]: http://twitter.com/RxJava
 [issues]: https://github.com/ReactiveX/RxAndroid/issues
 [start]: https://github.com/ReactiveX/RxJava/wiki/Getting-Started
