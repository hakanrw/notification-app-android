package tr.k12.enka.networking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.JsonParser
import tr.k12.enka.networking.types.Notification
import java.text.SimpleDateFormat
import java.util.*


class NotificationService : Service() {

    var shouldThreadStop: Boolean = false

    companion object {
        val CHANNEL_ID = "tr.k12.enka.networking.NOTIFCITAIONS"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        PreferenceSingleton.init(applicationContext)
        if(DataStore.getReceiveDate() === null) DataStore.setReceiveDate(dateToString(Calendar.getInstance().time))

        val thread = Thread {
            while(!shouldThreadStop) {
                Thread.sleep(5000)
                Log.d("backgroundProcess", "checking")

                // disable token cache
                DataStore.dict.remove("token")

                if(DataStore.getToken() === null) {
                    DataStore.setReceiveDate(dateToString(Calendar.getInstance().time))
                    continue
                }

                try {
                    val notification = checkNotifications(false)
                    Log.d("backgroundProcess", "notifications:" + notification.map { it.toString() }.joinToString(""))
                    Log.d("backgorundProcess", DataStore.getReceiveDate())
                    if(notification.isNotEmpty()) {
                        DataStore.setField("newReceived", "true")
                        notification.forEach {
                            val contentIntent = PendingIntent.getActivity(
                                    applicationContext,
                                    0,
                                    Intent(),  // add this
                                    PendingIntent.FLAG_UPDATE_CURRENT)
                            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setContentTitle(it.title)
                                    .setContentText(it.message)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .setContentIntent(contentIntent)
                                    .setAutoCancel(true)
                                    //.addAction(R.drawable.icon, "Call", pIntent)
                                    //.addAction(R.drawable.icon, "More", pIntent)
                                    //.addAction(R.drawable.icon, "And more", pIntent).build();

                            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(
                                        CHANNEL_ID,
                                        "Channel human readable title",
                                        NotificationManager.IMPORTANCE_HIGH)
                                notificationManager.createNotificationChannel(channel)
                            }

                            notificationManager.notify(1, notificationBuilder.build());

                        }
                    }
                } catch (exception: Exception) {
                    Log.d("backgroundService", "error", exception)
                }
            }
        }
        thread.start()
        return START_STICKY
    }

    override fun onDestroy() {
        shouldThreadStop = true
            super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}

fun checkNotifications(fullCheck: Boolean): List<Notification> {
    val (response, request, result) = (DataStore.SERVER_URL + "/notifications/get?${if(fullCheck) "fullCheck=true" else "checkDate=" + Utils.encode(DataStore.getReceiveDate())}")
        .httpGet()
        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        .header("Cookie", "SESSION_M=${DataStore.getToken()}")
        .responseString()

    when(result) {
        is Result.Failure -> {
            val ex = result.getException()
            throw ex
        }
        is Result.Success -> {
            val notifList = JsonParser.parseString(result.get()).asJsonObject.get("notifications").asJsonArray.toList()
            if(!fullCheck && !notifList.isEmpty()) DataStore.setReceiveDate(dateToString(Calendar.getInstance().time))

            return notifList.map {
                val notifJson = it.asJsonObject;
                Notification(notifJson.get("title").asString, notifJson.get("message").asString, stringToDate(notifJson.get("date").asString))
            }
        }
    }
}

fun stringToDate(string: String): Date {
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    isoFormat.timeZone = TimeZone.getTimeZone("UTC-0")
    return isoFormat.parse(string)
}

fun dateToString(date: Date): String {
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    isoFormat.timeZone = TimeZone.getTimeZone("UTC-0")
    return isoFormat.format(date)
}
