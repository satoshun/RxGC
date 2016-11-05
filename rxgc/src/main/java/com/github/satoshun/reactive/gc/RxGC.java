package com.github.satoshun.reactive.gc;


import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class RxGC {

  private final static int DEFAULT_WAIT_MS = 1000;

  private final static boolean IS_DEBUG = true;

  public static Completable watch(Object target) {
    ReferenceQueue<Object> queue = new ReferenceQueue<>();
    Observable<Reference<Object>> observable = internalWatch(Collections.singletonList(new WeakReference<>(target, queue)), queue);
    return Completable.fromObservable(observable);
  }

  public static <T> Observable<Reference<T>> watch(T target, T... targets) {
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
    return Observable.create(new ObservableOnSubscribe<Reference<T>>() {

      // avoids GC
      private final List<? extends Reference<T>> references = targets;
      private final AtomicInteger count = new AtomicInteger();

      @Override
      public void subscribe(ObservableEmitter<Reference<T>> e) throws Exception {
        while (true) {
          Reference<? extends T> ref = queue.poll();
          if (ref != null) {
            e.onNext((Reference<T>) ref);
            if (count.incrementAndGet() >= targets.size()) {
              break;
            }
            continue;
          }
          Thread.sleep(DEFAULT_WAIT_MS);
          if (IS_DEBUG) {
            Runtime.getRuntime().gc();
          }
        }
        e.onComplete();
      }
    });
  }
}
