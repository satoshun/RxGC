package com.github.satoshun.reactive.gc.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.satoshun.reactive.gc.RxGC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private Object target = new Object();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    RxGC.watch(target)
            .subscribeOn(Schedulers.io())
            .subscribe(() -> Log.d("RxGC single", "GCed"));

    new Thread(() -> {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // release object
      target = null;
    }).start();

    testMultiWatch();
  }

  private List<String> target1 = new ArrayList<>();
  private List<String> target2 = new LinkedList<>();

  private void testMultiWatch() {
    RxGC.watch(target1, target2)
            .subscribeOn(Schedulers.io())
            .subscribe(
                    ref -> Log.d("RxGC multi next", String.valueOf(ref.get())),
                    e -> Log.e("RxGC multi error", e.getMessage()),
                    () -> Log.d("RxGC multi complete", "complete"));

    new Thread(() -> {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // release object
      target1 = null;
      target2 = null;
    }).start();
  }
}
