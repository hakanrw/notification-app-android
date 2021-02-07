package tr.k12.enka.networking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent?) {
        val intent = Intent(context, NotificationService::class.java)
        context.startService(intent)

    }
}