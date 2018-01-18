package com.github.satoshun.reactive.gc;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

class GCObservable<T> extends Observable<Reference<T>> {
  private final static int DEFAULT_WAIT_MS = 1000;

  private final List<? extends Reference<T>> targets;
  private final ReferenceQueue<T> queue;

  GCObservable(List<? extends Reference<T>> references, ReferenceQueue<T> queue) {
    this.targets = references;
    this.queue = queue;
  }

  @Override protected void subscribeActual(Observer<? super Reference<T>> observer) {
    GCListener<T> gcListener = new GCListener<>(observer, targets, queue);
    gcListener.run();
    observer.onSubscribe(gcListener);
  }

  private static class GCListener<T> extends AtomicReference<Disposable> implements Disposable {
    private final Observer<? super Reference<T>> observer;
    private final List<? extends Reference<T>> targets;
    private final ReferenceQueue<T> queue;
    private final AtomicInteger count = new AtomicInteger();

    private GCListener(
        Observer<? super Reference<T>> observer,
        List<? extends Reference<T>> targets,
        ReferenceQueue<T> queue
    ) {
      this.observer = observer;
      this.targets = targets;
      this.queue = queue;
    }

    void run() {
      while (true) {
        if (isDisposed()) return;
        Reference<? extends T> ref = queue.poll();
        if (ref != null) {
          observer.onNext((Reference<T>) ref);
          if (count.incrementAndGet() >= targets.size()) {
            break;
          }
          continue;
        }
        try {
          Thread.sleep(DEFAULT_WAIT_MS);
        } catch (InterruptedException e) {
          if (!isDisposed()) {
            observer.onError(e);
            return;
          }
        }
      }
      observer.onComplete();
    }

    @Override public void dispose() {
      DisposableHelper.dispose(this);
    }

    @Override public boolean isDisposed() {
      return DisposableHelper.isDisposed(get());
    }
  }
}
