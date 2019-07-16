package com.sibmentor.appointment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_update_email.*


class UserEmailUpdate : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("users")
    var pastEmail = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_update_email)

        layoutPassword.visibility = View.VISIBLE
        layoutUpdateEmail.visibility = View.GONE


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
                                layoutUpdateEmail.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                edit_text_password.error = "Invalid Password"
                                edit_text_password.requestFocus()
                            }
                            else -> this.toast(task.exception?.message!!)
                        }
                    }
            }

        }

        button_update.setOnClickListener { view ->
            currentUser?.let { user ->
                pastEmail = user.email.toString()
            }
            val email = edit_text_email.text.toString().trim()

            if (email.isEmpty()) {
                edit_text_email.error = "Email Required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_text_email.error = "Valid Email Required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility = View.VISIBLE
            currentUser?.let { user ->
                user.updateEmail(email)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        if (task.isSuccessful) {
                            val userNameRef = ref.orderByChild("email").equalTo(pastEmail)
                            userNameRef.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (!dataSnapshot.exists()) {

                                    } else {
                                        for (e in dataSnapshot.children) {
                                            val employee = e.getValue(Data::class.java)!!
                                            val sId = employee.id
                                            Toast.makeText(
                                                this@UserEmailUpdate,
                                                email + sId,
                                                Toast.LENGTH_LONG
                                            ).show()

                                            ref.child(sId).child("email").setValue(email)

                                        }

                                    }
                                }
                            })
                            this.toast("Email Updated")
                            startActivity(Intent(this, UserProfile::class.java))
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
