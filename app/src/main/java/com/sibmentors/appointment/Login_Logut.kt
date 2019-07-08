package com.sibmentors.appointment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

/** Commented Login method*/
fun Context.login() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.let { user ->
        val rootRef = FirebaseDatabase.getInstance().reference
        val userNameRef = rootRef.child("users").orderByChild("email").equalTo(user.email)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (e in dataSnapshot.children) {
                    val employee = e.getValue(Data::class.java)
                    if (employee != null) {
                        val u_type = employee.user_type
                        if (u_type == "S") startActivity(Intent(this@login, UserHomeV2::class.java))
                        else if (u_type == "M") startActivity(Intent(this@login, mentorhomev2::class.java))

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        userNameRef.addListenerForSingleValueEvent(eventListener)

    }

}


fun Context.logout() {
    val alertbox = AlertDialog.Builder(this)
        .setMessage("Do you want to Logout?")
        .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
            // do something when the button is clicked
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "You have been logout Successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)


        })
        .setNegativeButton("No", // do something when the button is clicked
            DialogInterface.OnClickListener { arg0, arg1 -> })
        .show()

}
