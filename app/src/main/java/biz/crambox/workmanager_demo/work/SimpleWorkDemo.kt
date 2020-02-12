package biz.crambox.workmanager_demo.work

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import biz.crambox.workmanager_demo.utils.SampleTask

class SimpleWorkDemo(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    init {

    }

    override fun doWork(): Result {
        Log.d("SimpleWorkDemo", "doWork()")
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            SampleTask.task(context)
        }
        return Result.success()
    }
}