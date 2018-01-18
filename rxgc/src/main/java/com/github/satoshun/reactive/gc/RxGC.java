package com.github.satoshun.reactive.gc;


import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public class RxGC {

  public static Completable watch(Object target) {
    ReferenceQueue<Object> queue = new ReferenceQueue<>();
    Observable<Reference<Object>> observable = internalWatch(Collections.singletonList(new WeakReference<>(target, queue)), queue);
    return Completable.fromObservable(observable);
  }

  public static <T> Observable<Reference<T>> watch(@NonNull T target, T... targets) {
    ReferenceQueue<T> queue = new ReferenceQueue<>();
    ArrayList<WeakReference<T>> references = new ArrayList<>();
    references.add(new WeakReference<>(target, queue));
    for (T t : targets) {
      references.add(new WeakReference<>(t, queue));
    }
    return internalWatch(references, queue);
  }

  private static <T> Observable<Reference<T>> internalWatch(final List<? extends Reference<T>> targets,
                                                            final ReferenceQueue<T> queue) {
    return new GCObservable<>(targets, queue);
  }
}
