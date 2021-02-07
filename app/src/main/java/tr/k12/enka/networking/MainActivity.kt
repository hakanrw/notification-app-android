package tr.k12.enka.networking

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("status", "loading main")

        PreferenceSingleton.init(applicationContext)
        setContentView(R.layout.main)
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if(!isServiceAlive(NotificationService::class.java)) {
            val intent = Intent(this, NotificationService::class.java)
            startService(intent)
        }

        // Signed in
        if(DataStore.getToken() !== null) {
            Log.d("status", "loading menu")
            val menu = Intent(this, MenuActivity::class.java)
            startActivity(menu)
        }


        main_sign.setOnClickListener{
            val logIn = Intent(this, LoginActivity::class.java)
            startActivity(logIn)
        }

        main_register.setOnClickListener{
            val logIn = Intent(this, LoginActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean("register", true)
            logIn.putExtras(bundle)
            startActivity(logIn)
        }
    }

    private fun isServiceAlive(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}