package com.github.satoshun.reactive.gc;


import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

public class RxGC {

  private final static int DEFAULT_WAIT_MS = 1000;

  private final static boolean IS_DEBUG = true;

  public static Completable watch(Object target) {
    ReferenceQueue<Object> queue = new ReferenceQueue<>();
    return watch(new WeakReference<>(target, queue), queue);
  }

  private static Completable watch(final Reference<?> target,
                                   final ReferenceQueue<Object> queue) {
    return Completable.create(new CompletableOnSubscribe() {
      // avoids GC
      private final Reference<?> reference = target;

      @Override
      public void subscribe(CompletableEmitter e) throws Exception {
        while (true) {
          Reference<?> ref = queue.poll();
          if (ref != null) {
            break;
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
