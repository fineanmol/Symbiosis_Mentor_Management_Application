package com.sibmentor.appointment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_password_reset.*


class passwordReset : androidx.appcompat.app.AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        button_reset_password.setOnClickListener {
            val email = text_email.text.toString().trim()

            if (email.isEmpty()) {
                text_email.error = "Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                text_email.error = "Valid Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility = View.VISIBLE

            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progressbar.visibility = View.GONE
                    if (task.isSuccessful) {
                        val alertbox = AlertDialog.Builder(this)
                            .setMessage("Reset link has been sent to your Registered Email Id, In case you didn't receive a mail check your spam folder or try again")
                            .setPositiveButton("OKAY", DialogInterface.OnClickListener { arg0, arg1 ->
                                // do something when the button is clicked
                                var intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)

                            })

                            .show()
                    } else {
                        this.toast(task.exception?.message!!)
                    }
                }
        }
    }
}