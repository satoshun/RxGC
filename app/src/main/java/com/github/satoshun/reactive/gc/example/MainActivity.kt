package com.github.satoshun.reactive.gc.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.github.satoshun.reactive.gc.RxGC
import com.github.satoshun.reactive.gc.watchGC
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

  private var target: Any? = Any()

  private var target1: List<String>? = ArrayList()
  private var target2: List<String>? = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    testSingleRefWatch()
    testMultiRefWatch()

  }

  private fun testSingleRefWatch() {
    target!!.watchGC()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          Toast.makeText(this, "did garbage collection", Toast.LENGTH_LONG).show()
        }

    Thread {
      try {
        Thread.sleep(2000)
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }

      // release object
      target = null
    }.start()
  }

  private fun testMultiRefWatch() {
    RxGC.watch<List<String>>(target1!!, target2)
        .subscribeOn(Schedulers.io())
        .subscribe(
            { ref -> Toast.makeText(this, "did garbage collection $ref", Toast.LENGTH_LONG).show() },
            { e -> Log.e("RxGC multi error", e.message) },
            { Log.d("RxGC multi complete", "complete") })

    Thread {
      try {
        Thread.sleep(5000)
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }

      // release object
      target1 = null

      try {
        Thread.sleep(2000)
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }

      // release object
      target2 = null
    }.start()
  }
}
