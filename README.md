[![](https://jitpack.io/v/satoshun/RxGC.svg)](https://jitpack.io/#satoshun/RxGC)

# RxGC is garbage collection detector for RxJava2.x

this library can detect instance is called from GC.


## install

gradle 

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

```gradle
compile 'com.github.satoshun:RxGC:$latest_version'
```


## usage

```java
Object target = new Object();
RxGC.watch(target)
        .subscribeOn(Schedulers.io())
        .subscribe(() -> Log.d("did Garbage Collection"));
```
