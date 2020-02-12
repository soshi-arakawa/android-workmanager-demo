package biz.crambox.workmanager_demo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import biz.crambox.workmanager_demo.utils.SampleTask
import biz.crambox.workmanager_demo.work.SimpleWorkDemo
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val workManager = WorkManager.getInstance()

    private val workerTag = "worker"

    private var workerId: UUID? = null

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MainActivity", "onReceive()")
            SampleTask.task(applicationContext)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_start_one_time_work).setOnClickListener {
            onClickStartOneTimeWorkButton()
        }

        findViewById<Button>(R.id.button_start_one_time_work_after_five_seconds).setOnClickListener {
            onClickStartOneTimeWorkAfterFiveSecondsButton()
        }

        findViewById<Button>(R.id.button_start_periodic_work).setOnClickListener {
            onClickStartPeriodicWorkButton()
        }

        findViewById<Button>(R.id.button_show_states_of_workers).setOnClickListener {
            onClickShowStatesOfWorker()
        }

        findViewById<Button>(R.id.button_start_alarm).setOnClickListener {
            onClickStartAlarm()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter().apply { addAction("ACTION_ALARM") })
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun onClickStartOneTimeWorkButton() {
        val request = OneTimeWorkRequest.from(SimpleWorkDemo::class.java)
        workerId = request.id
        workManager.enqueue(request)
    }

    private fun onClickStartOneTimeWorkAfterFiveSecondsButton() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 5)

        val request = OneTimeWorkRequest.Builder(SimpleWorkDemo::class.java)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        workerId = request.id
        workManager.enqueue(request)
    }

    private fun onClickStartPeriodicWorkButton() {
        val request = PeriodicWorkRequest.Builder(SimpleWorkDemo::class.java, 15, TimeUnit.MINUTES)
            .apply {
                addTag(workerTag)
            }.build()

        workerId = request.id
        workManager.enqueue(request)
    }

    private fun onClickShowStatesOfWorker() {
        var msg = ""

        workerId?.let {
            workManager.getWorkInfoById(it)?.get()
        }?.also {
            msg+= "Worker ID: $workerId, State: ${it.state}\n"
        }
        workManager.getWorkInfosByTag(workerTag).get().forEach {
            msg += "\nWorkerId: ${it.id}, State: ${it.state}"
        }

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun onClickStartAlarm() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 5)
        val intent = Intent()
        intent.action = "ACTION_ALARM"
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
    }
}
