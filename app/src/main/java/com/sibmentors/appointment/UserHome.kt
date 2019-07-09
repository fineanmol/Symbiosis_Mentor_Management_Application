package com.sibmentors.appointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_slot.*
import java.util.*


class UserHome : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseUser
    lateinit var ref: DatabaseReference
    lateinit var slotList: MutableList<BookedData>
    lateinit var listview: ListView
    val userref = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = FirebaseAuth.getInstance().currentUser

    var d = " "
    var m = " "
    var y = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)

        currentUser?.let { user ->

            val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                    //create new user
                    Toast.makeText(this@UserHome, "User details not found", Toast.LENGTH_LONG).show()
                    logout()
                } else {

                    for (e in dataSnapshot.children) {
                        val employee = e.getValue(Data::class.java)
                        var mentorids = employee!!.mentorreferal
                        showbooking(mentorids)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            userNameRef?.addListenerForSingleValueEvent(eventListener)

        }
    }
    private fun showbooking(mentorids: String)
    {

        slotList = mutableListOf()
        listview = findViewById(R.id.listview)

        val calendar = Calendar.getInstance()
        var DATE = calendar.get(Calendar.DATE)
        var month = calendar.get(Calendar.MONTH)
        var Week = calendar.get(Calendar.WEEK_OF_YEAR)
        var year = calendar.get(Calendar.YEAR)
        Log.d("TAGD", "$Week/$month/$year")

    var multiplementors= mentorids.split("/")
    for(i in multiplementors){
    //region ShowBookingtoStudent
    ref = FirebaseDatabase.getInstance().getReference("Slots").child("$i")
    var query = ref.orderByChild("status").equalTo("NB")

    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
               // slotList.clear()
                for (e in p0.children) {
                    val employee = e.getValue(BookedData::class.java)
                    var date = employee?.date
                    d = date?.split("/")?.first().toString()
                    m = date?.split("/")?.get(1).toString()
                    y = "20" + date?.split("/")?.last().toString().replace("]", "")
                    val targetCalendar = Calendar.getInstance()
                    targetCalendar.set(Calendar.YEAR, y.toInt())
                    targetCalendar.set(Calendar.MONTH, month)
                    targetCalendar.set(Calendar.DATE, d.toInt())
                    val targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR)
                    val targetYear = targetCalendar.get(Calendar.YEAR)
                    val targetMonth = targetCalendar.get(Calendar.MONTH)
                    val targetDate = targetCalendar.get(Calendar.DATE)

                    Log.d("TAGD", "$targetWeek/$targetMonth/$targetYear")
                    if (Week == targetWeek || year == targetYear) {

                        slotList.add(employee!!)
                        Log.d("TAGD", "ADDED")

                    }
                }

                val adapter = customAdapter(this@UserHome, R.layout.listview_custom, slotList)
                listview.adapter = adapter

            }
            else{
                Toast.makeText(
                    this@UserHome,
                    "No Booking Available Yet !!",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        override fun onCancelled(p0: DatabaseError) {
        }
    })
    //endregion

}

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.user_home_v2, menu)
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

        if (id == R.id.action_logout) {

            logout()

            return true

        }
        if (id == R.id.contactUs) {
            startActivity(Intent(this, AboutDeveloper::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}