package com.sibmentor.appointment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.mentor_refer.*

class Mentor_refer : AppCompatActivity() {
    val userref = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mentor_refer)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
        currentUser?.let { user ->

            val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                    //create new user
                    Toast.makeText(this@Mentor_refer, "User details not found", Toast.LENGTH_LONG).show()
                    logout()
                } else {
                    var lastvalue = "Slots"
                    for (e in dataSnapshot.children) {
                        val employee = e.getValue(Data::class.java)
                        var name = (employee!!.name).split(" ").first()
                        var eid = employee.studentId
                        mentorrefer(name, eid, lastvalue)


                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            userNameRef?.addListenerForSingleValueEvent(eventListener)

        }
    }

    private fun mentorrefer(name: String, eid: String, lastvalue: String) {
        mentorrefertext.text = "$name$eid$lastvalue"

        invitebtn.setOnClickListener(View.OnClickListener {

            //region SharingCode
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody =
                "Hey, \nthis is my Mentor Booking Code : $name$eid$lastvalue \n,enter this code before booking menu, so you can see my available slots, and book them easily. Keep Booking !!"
            sharingIntent.putExtra(
                android.content.Intent.EXTRA_SUBJECT,
                "Slot Booking Management : Android Application"
            )
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
            //endregion
        })
        mentorrefertext.setOnClickListener(View.OnClickListener {
            //region SharingCode
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody =
                "Hey, \nthis is my Mentor Booking Code : $name$eid$lastvalue \n,enter this code before booking menu, so you can see my available slots, and book them easily. Keep Booking !!"
            sharingIntent.putExtra(
                android.content.Intent.EXTRA_SUBJECT,
                "Slot Booking Management : Android Application"
            )
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))

        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.backarrow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (item.itemId == android.R.id.home) // Press Back Icon
        {
            finish()
        }


        return super.onOptionsItemSelected(item)
    }
}
