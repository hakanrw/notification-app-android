package tr.k12.enka.networking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.core.view.marginBottom
import kotlinx.android.synthetic.main.menu.*
import java.lang.Exception


class MenuActivity : AppCompatActivity() {
    var backPressed = 0
    var shouldThreadStop = false
    lateinit var checkThread: Thread


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        menu_notification_bar.removeAllViews()
        menu_notification_bar.addView(TextView(baseContext).apply {
            this.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            this.text = "yükleniyor"
        })

        menu_mail.text = DataStore.getMail()

        menu_close.setOnClickListener {
            AuthHandler.quit()
            finish()
        }

        DataStore.setField("newReceived", null)
        getNotifications()

        checkThread = Thread {
            while (!shouldThreadStop) {
                try {
                    Thread.sleep(1500);
                } catch (exception: InterruptedException) {
                    // ignored
                }
                DataStore.dict.remove("newReceived");
                if(DataStore.getField("newReceived") !== null) {
                    getNotifications()
                    DataStore.setField("newReceived", null)
                }
            }
        }
        checkThread.start()
    }

    override fun onBackPressed() {
        backPressed++
        if(backPressed >= 2) {
            this.moveTaskToBack(true)
            backPressed = 0
        }
    }

    fun getNotifications() {
        val notifThread = Thread {
            try {
                val notifications = checkNotifications(true)
                runOnUiThread {
                    menu_notification_bar.removeAllViews()
                    val inflater = LayoutInflater.from(baseContext)
                    notifications.forEach {
                        val layout = inflater.inflate(R.layout.notification, menu_notification_bar, false) as CardView
                        val linearLayout = layout.getChildAt(0) as LinearLayout
                        (linearLayout.getChildAt(0) as TextView).text = it.title
                        (linearLayout.getChildAt(1) as TextView).text = it.message
                        (linearLayout.getChildAt(2) as TextView).text = dateToString(it.date)

                        menu_notification_bar.addView(layout)
                    }
                }
            } catch (exception: Exception) {
                Log.d("ERROR", "exception", exception);
                runOnUiThread {
                    menu_notification_bar.removeAllViews()
                    menu_notification_bar.addView(TextView(baseContext).apply {
                        this.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        this.text = "bir hata oluştu"
                    })
                }
            }
        }
        notifThread.start()
    }

    override fun onDestroy() {
        shouldThreadStop = true
        checkThread.interrupt()
        super.onDestroy()
    }
}
