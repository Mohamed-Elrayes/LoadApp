package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.utils.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.content_detail.button_ok
import kotlinx.android.synthetic.main.content_detail.textView_fileName
import kotlinx.android.synthetic.main.content_detail.textView_status

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()

        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if (intent?.extras != null) {
            textView_fileName.text = intent.getStringExtra("download")
            textView_status.text = intent.getStringExtra("status")
        }

        button_ok.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
