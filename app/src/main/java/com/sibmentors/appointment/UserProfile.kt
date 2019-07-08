package com.sibmentors.appointment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfile : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser

    val ref = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        currentUser?.let { user ->
            val userNameRef = ref.orderByChild("email").equalTo(currentUser.let { user -> user.email })
            userNameRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                    } else {
                        for (e in dataSnapshot.children) {
                            val employee = e.getValue(Data::class.java)!!


                            val name = if (user.displayName.isNullOrEmpty()) employee.name else user.displayName
                            ref.child(employee.id).child("name").setValue(name)
                            edit_text_name.setText(name)

                            //  val addSlot = slotsData(sId, begin, end, date, generated, reserved_by, studentId, studentNumber, status)

                            //  Toast.makeText(this@UserEmailUpdate, "Selected Slots Saved", Toast.LENGTH_LONG).show()


                        }

                    }
                }
            })

            text_email.text = user.email

            text_phone.text = if (user.phoneNumber.isNullOrEmpty()) "Verify Phone Number" else user.phoneNumber

            if (user.isEmailVerified) {
                text_not_verified.visibility = View.INVISIBLE
            } else {
                text_not_verified.visibility = View.VISIBLE
            }
        }

        button_save.setOnClickListener {


            val name = edit_text_name.text.toString().trim()

            if (name.isEmpty()) {
                edit_text_name.error = "name required"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }

            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener { task ->
                    progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        this.toast("Profile Updated")
                        startActivity(Intent(this, UserHomeV2::class.java))
                    } else {
                        this.toast(task.exception?.message!!)
                    }
                }

        }
        text_not_verified.setOnClickListener {

            currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        this.toast("Verification Email Sent")
                    } else {
                        this.toast(it.exception?.message!!)
                    }
                }

        }

        text_phone.setOnClickListener {
            startActivity(Intent(this, UserPhoneVerify::class.java))
        }

        text_email.setOnClickListener {
            startActivity(Intent(this, UserEmailUpdate::class.java))
        }

        text_password.setOnClickListener {
            startActivity(Intent(this, UserPassUpdate::class.java))
        }

    }
    override fun onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        var intent= Intent(this,UserHomeV2::class.java)
        startActivity(intent)
    }

}
