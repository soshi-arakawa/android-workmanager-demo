package biz.crambox.workmanager_demo.utils

import android.content.Context
import android.widget.Toast

object SampleTask {

    fun task(context: Context) {
        Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
    }

}