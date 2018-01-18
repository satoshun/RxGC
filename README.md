[![](https://jitpack.io/v/satoshun/RxGC.svg)](https://jitpack.io/#satoshun/RxGC)

# RxGC is garbage collection detector for RxJava2.x

this library can detect instance is called from GC.


## install

add your gradle script

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

```gradle
implementation 'com.github.satoshun.RxGC:rxgc:$latest_version'
```

for kotlin

```gradle
implementation 'com.github.satoshun.RxGC:rxgc-kotlin:$latest_version'
```


## usage

```java
Object target = new Object();
RxGC.watch(target)
        .subscribeOn(Schedulers.io())
        .subscribe(() -> Log.d("did Garbage Collection"));
```
