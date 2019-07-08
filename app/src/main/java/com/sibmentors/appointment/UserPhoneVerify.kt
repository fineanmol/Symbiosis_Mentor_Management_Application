package com.sibmentors.appointment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_verify_phone.*
import java.util.concurrent.TimeUnit

class UserPhoneVerify : AppCompatActivity() {

    private var verificationId: String? = null
    val ref = FirebaseDatabase.getInstance().getReference("users")
    private val currentUser = FirebaseAuth.getInstance().currentUser
    var phone = ""
    var flag = true




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_verify_phone)
        Toast.makeText(this, "Please verify your Phone Number for hustle free Appointment Booking", Toast.LENGTH_LONG)
        layoutPhone.visibility = View.VISIBLE
        layoutVerification.visibility = View.GONE
        currentUser?.let { user ->
            val userNameRef = ref.orderByChild("email").equalTo(currentUser.let { user -> user.email })
            userNameRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(
                            this@UserPhoneVerify,
                            "User Not Registered",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        for (e in dataSnapshot.children) {
                            val employee = e.getValue(Data::class.java)!!
                            edit_text_phone.setText(employee.number)

                            //  val addSlot = slotsData(sId, begin, end, date, generated, reserved_by, studentId, studentNumber, status)
                            //  Toast.makeText(this@UserEmailUpdate, "Selected Slots Saved", Toast.LENGTH_LONG).show()


                        }

                    }
                }
            })

        }


        button_send_verification.setOnClickListener {

            phone = edit_text_phone.text.toString().trim()

            if (phone.isEmpty() || phone.length != 10) {
                edit_text_phone.error = "Enter a valid Phone Number"
                edit_text_phone.requestFocus()
                return@setOnClickListener
            }

            var phoneNumber = '+' + ccp.selectedCountryCode + phone

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    phoneAuthCallbacks
                )


            layoutPhone.visibility = View.GONE
            layoutVerification.visibility = View.VISIBLE
        }

        button_verify.setOnClickListener {
            val code = edit_text_code.text.toString().trim()

            if (code.isEmpty()) {
                edit_text_code.error = "Code required"
                edit_text_code.requestFocus()
                return@setOnClickListener
            }

            verificationId?.let {
                val credential = PhoneAuthProvider.getCredential(it, code)
                addPhoneNumber(credential)
            }
        }
    }


    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {
            phoneAuthCredential?.let {
                addPhoneNumber(phoneAuthCredential)
            }
        }

        override fun onVerificationFailed(exception: FirebaseException?) {
            this@UserPhoneVerify.toast(exception?.message!!)
        }

        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verificationId, token)
            this@UserPhoneVerify.verificationId = verificationId
        }
    }

    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        var i = 0
        FirebaseAuth.getInstance()
            .currentUser?.updatePhoneNumber(phoneAuthCredential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val userNameRef = ref.orderByChild("email").equalTo(currentUser?.let { user -> user.email })
                    userNameRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                Toast.makeText(
                                    this@UserPhoneVerify,
                                    "User Not Registered",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                for (e in dataSnapshot.children) {
                                    val employee = e.getValue(Data::class.java)!!
                                    val Id = employee.id
                                    while (flag) {
                                        ref.child(Id).child("number").setValue(phone)
                                        flag = false
                                    }

                                    //  val addSlot = slotsData(sId, begin, end, date, generated, reserved_by, studentId, studentNumber, status)
                                    //  Toast.makeText(this@UserEmailUpdate, "Selected Slots Saved", Toast.LENGTH_LONG).show()


                                }

                            }
                        }
                    })

                    this.toast("New Phone Number Added")
                    startActivity(Intent(this, UserProfile::class.java))
                } else {
                    this.toast(task.exception?.message!!)
                    startActivity(Intent(this, UserPhoneVerify::class.java))
                }
            }
    }
}
