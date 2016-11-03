package com.github.satoshun.reactive.gc.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.satoshun.reactive.gc.RxGC;

import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private Object target = new Object();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    RxGC.watch(target)
            .subscribeOn(Schedulers.io())
            .subscribe(new Action() {
              @Override
              public void run() throws Exception {
                Log.d("RxGC", "GCed");
              }
            });

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        // release object
        target = null;
      }
    }).start();
  }
}
