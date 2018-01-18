package com.github.satoshun.reactive.gc

import io.reactivex.Completable

fun Any.watchGC(): Completable = RxGC.watch(this)

