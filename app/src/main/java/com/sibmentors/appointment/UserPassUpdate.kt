package com.sibmentors.appointment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_update_email.button_authenticate
import kotlinx.android.synthetic.main.activity_user_update_email.edit_text_password
import kotlinx.android.synthetic.main.activity_user_update_email.layoutPassword
import kotlinx.android.synthetic.main.activity_user_update_email.progressbar
import kotlinx.android.synthetic.main.activity_user_update_pass.*

class UserPassUpdate : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("users")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_update_pass)

        layoutPassword.visibility = View.VISIBLE
        layoutUpdatePassword.visibility = View.GONE

        button_authenticate.setOnClickListener {

            val password = edit_text_password.text.toString().trim()

            if (password.isEmpty()) {
                edit_text_password.error = "Password required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }


            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                layoutPassword.visibility = View.GONE
                                layoutUpdatePassword.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                edit_text_password.error = "Invalid Password"
                                edit_text_password.requestFocus()
                            }
                            else -> this.toast(task.exception?.message!!)
                        }
                    }
                progressbar.visibility = View.GONE
            }

        }

        button_pass_update.setOnClickListener {

            val password = edit_text_new_password.text.toString().trim()

            if (password.isEmpty() || password.length < 6) {
                edit_text_new_password.error = "atleast 6 char password required"
                edit_text_new_password.requestFocus()
                return@setOnClickListener
            }

            if (password != edit_text_new_password_confirm.text.toString().trim()) {
                edit_text_new_password_confirm.error = "password did not match"
                edit_text_new_password_confirm.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                progressbar.visibility = View.VISIBLE
                user.updatePassword(password)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        if (task.isSuccessful) {
                            val userNameRef = ref.orderByChild("email").equalTo(user.email)
                            userNameRef.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (!dataSnapshot.exists()) {

                                    } else {
                                        for (e in dataSnapshot.children) {
                                            val employee = e.getValue(Data::class.java)!!
                                            val sId = employee.id

                                            ref.child(sId).child("pass").setValue(password)

                                        }

                                    }
                                }
                            })

                            this.toast("Password Updated")
                            var passIntent= Intent(this,UserProfile::class.java)
                            startActivity(passIntent)

                        } else {
                            this.toast(task.exception?.message!!)
                        }
                    }
            }
        }
    }
    override fun onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        var intent= Intent(this,UserProfile::class.java)
        startActivity(intent)
    }

}
