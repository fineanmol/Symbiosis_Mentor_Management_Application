package com.sibmentors.appointment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_user_signup.*

class UserSignup : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_signup)
        mAuth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("users")

        u_register.setOnClickListener {
            val email = u_r_email.text.toString().trim()
            val password = u_r_pass.text.toString().trim()
            val namef = name.text.toString().trim()
            val number =mobile.text.toString().trim()
            val studentidf = studentid.text.toString().trim()
            val status = ("NB")
            val user_type = ("S")
            //val flag2 = (" ").toString().trim()

            if (namef.isEmpty()) {
                name.error = "Name Required"
                name.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                u_r_email.error = "Email Required"
                u_r_email.requestFocus()
                return@setOnClickListener
            }
            if (number.isEmpty()) {
                mobile.error = "Number Required"
                mobile.requestFocus()
                return@setOnClickListener
            }
            if (studentidf.isEmpty()) {
                studentid.error = "Student-Id Required"
                studentid.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                u_r_email.error = "Valid Email Required"
                u_r_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.PHONE.matcher(number).matches() || number.length > 10 || number.length < 9) {
                mobile.error = "Valid Phone Number Required"
                mobile.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                u_r_pass.error = "6 char password required"
                u_r_pass.requestFocus()
                return@setOnClickListener
            }
            registerUser(email, password, namef, number, studentidf, status, user_type)

        }

        u_r_login.setOnClickListener {
            startActivity(Intent(this@UserSignup, MainActivity::class.java))
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        name: String,
        number: String,
        dob: String,
        status: String,
        user_type: String
    ) {

        progressbar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressbar.visibility = View.GONE
                if (task.isSuccessful) {
                    addUser(email, password, name, number, dob, status, user_type)
                    login()
                } else {
                    task.exception?.message?.let {
                        toast(it)
                    }
                }
            }

    }

    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let {
            login()
        }
    }

    private fun addUser(
        email: String,
        password: String,
        name: String,
        number: String,
        studentId: String,
        status: String,
        user_type: String
    ) {


        ref = FirebaseDatabase.getInstance().reference
        val userId= (ref.push().key).toString()
        val addUser = Data(userId, email, password, name, number, studentId, status, user_type)


        ref.child("users").child(userId).setValue(addUser)
        Toast.makeText(this,"Registration Successful", Toast.LENGTH_LONG).show()



    }

}