package tr.k12.enka.networking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewManager
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.login.*

class LoginActivity : AppCompatActivity() {
    var isRegister = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val bundle = intent.extras
        if(bundle?.getBoolean("register") == true) {
            isRegister = true
        }

        if(isRegister) {
            login_title.text = "Kayıt Ol"
            login_sign.text = "KAYIT OL"
            login_sign.setOnClickListener {
                register()
            }
        } else {
            (login_password_again.parent as ViewManager).removeView(login_password_again)
            login_sign.setOnClickListener {
                logIn()
            }
        }

        login_back.setOnClickListener {
            finish()
        }
    }

    fun logIn() {
        showMessage(null)

        (DataStore.SERVER_URL + "/users/login")
            .httpPost()
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .body("mail=" + Utils.encode(login_mail.text.toString()) + "&password=" + Utils.encode(login_password.text.toString()))
            .responseString{ request, response, result ->
                when(result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        Log.d("RESULT", ex.toString())
                        runOnUiThread {
                            when (response.statusCode) {
                                400 -> showMessage("değerleri düzgün girin")
                                401 -> showMessage("hatalı giriş")
                                else -> showMessage("bir hata oluştu")
                            }
                        }
                    }
                    is Result.Success -> {
                        Log.d("RESULT", result.get())
                        runOnUiThread {
                            val jsonResponse = JsonParser.parseString(result.get()).asJsonObject
                            val token = jsonResponse.get("token").asString
                            Log.d("RESULT", "token: $token")
                            AuthHandler.authenticate(login_mail.text.toString(), token)
                            finish()

                            val menu = Intent(this, MenuActivity::class.java)
                            startActivity(menu)
                        }
                    }
                }
            }

    }

    fun register() {
        showMessage(null)

        if(login_password.text.toString() != login_password_again.text.toString()) {
            showMessage("şifreler eşleşmiyor")
            return
        }

        (DataStore.SERVER_URL + "/users/register")
            .httpPost()
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .body("mail=" + Utils.encode(login_mail.text.toString()) + "&password=" + Utils.encode(login_password.text.toString()))
            .responseString{ request, response, result ->
                when(result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        Log.d("RESULT", ex.toString())
                        runOnUiThread {
                            when (response.statusCode) {
                                400 -> showMessage("değerleri düzgün girin")
                                401 -> showMessage("bu mail kapılmış")
                                else -> showMessage("bir hata oluştu")
                            }
                        }
                    }
                    is Result.Success -> {
                        Log.d("RESULT", result.get())
                        runOnUiThread {
                            showMessage("kayıt olundu")
                        }
                    }
                }
            }
    }

    fun showMessage(message: String?) {
        login_message.visibility = if(message === null) View.GONE else View.VISIBLE
        if(message !== null) login_message.text = message
    }
}