package com.github.satoshun.reactive.gc.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.satoshun.reactive.gc.RxGC
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {

  private var target: Any? = Any()

  private var target1: List<String>? = ArrayList()
  private var target2: List<String>? = LinkedList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    testSingleRefWatch()
    testMultiRefWatch()

  }

  private fun testSingleRefWatch() {
    RxGC.watch(target)
        .subscribeOn(Schedulers.io())
        .subscribe { Log.d("RxGC single", "GCed") }

    Thread {
      try {
        Thread.sleep(3000)
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }

      // release object
      target = null
    }.start()
  }

  private fun testMultiRefWatch() {
    RxGC.watch<List<String>>(target1, target2)
        .subscribeOn(Schedulers.io())
        .subscribe(
            { ref -> Log.d("RxGC multi next", ref.get().toString()) },
            { e -> Log.e("RxGC multi error", e.message) },
            { Log.d("RxGC multi complete", "complete") })

    Thread {
      try {
        Thread.sleep(3000)
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }

      // release object
      target1 = null
      target2 = null
    }.start()
  }
}
