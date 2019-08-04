package com.sibmentor.appointment

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*


class AppointmentList2 : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseUser
    lateinit var ref: DatabaseReference
    lateinit var slotList: MutableList<slotsData>
    lateinit var listview: ListView
    var d = " "
    var m = " "
    var y = " "
    val userref = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_list2)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)

        //region Current User
        currentUser?.let { user ->

            val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                    //create new user
                    Toast.makeText(
                        this@AppointmentList2,
                        "User details not found,\nTry Again or Contact Developer",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    var lastvalue = "Slots"
                    for (e in dataSnapshot.children) {
                        val employee = e.getValue(Data::class.java)
                        var name = (employee!!.name).split(" ").first()
                        var eid = employee.studentId
                        appointlist(name, eid, lastvalue)


                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            userNameRef?.addListenerForSingleValueEvent(eventListener)

        }
        //endregion


    }

    private fun appointlist(name: String, eid: String, lastvalue: String) {
        //region Appointment List Code
        val calendar = Calendar.getInstance()
        var month = calendar.get(Calendar.MONTH)
        var Week = calendar.get(Calendar.WEEK_OF_YEAR)
        var year = calendar.get(Calendar.YEAR)
        var Date = calendar.get(Calendar.DATE)

        val filter = arrayOf("Today", "Tomorrow", "This Week", "This Month")
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filter)
            spinner.adapter = arrayAdapter
            slotList = mutableListOf()
            listview = findViewById(R.id.appointmentList)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    /*Toast.makeText(this@AppointmentList2, filter[position], Toast.LENGTH_SHORT).show()*/
                    ref = FirebaseDatabase.getInstance().getReference("Slots").child("$name$eid$lastvalue")
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                slotList.clear()
                                for (e in p0.children) {
                                    val employee = e.getValue(slotsData::class.java)
                                    var status = employee!!.status
                                    var date = employee.date
                                    d = date.split("/").first().toString()
                                    m = date.split("/").get(1).toString()
                                    y = "20" + date.split("/").last().toString().replace("]", "")
                                    val targetCalendar = Calendar.getInstance()
                                    targetCalendar.set(Calendar.YEAR, y.toInt())
                                    targetCalendar.set(Calendar.MONTH, month)
                                    targetCalendar.set(Calendar.DATE, d.toInt())
                                    val targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR)
                                    val targetYear = targetCalendar.get(Calendar.YEAR)
                                    val targetMonth = targetCalendar.get(Calendar.MONTH)
                                    val targetDate = targetCalendar.get(Calendar.DATE)
                                    if (status == "B") {
                                        if (position == 0) {
                                            // Log.d("TAGDD", targetDate.toString() + "--" + Date)
                                            if (targetDate == Date) {
                                                slotList.add(employee)

                                            }
                                        }
                                        if (position == 1) {
                                            slotList.clear()
                                            if (targetDate == (Date + 1)) {
                                                slotList.add(employee)
                                                if (slotList.isEmpty()) {
                                                    Toast.makeText(
                                                        this@AppointmentList2,
                                                        "Tommorow no slot present",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                        if (position == 2) {
                                            /*Toast.makeText(
                                                this@AppointmentList2,
                                                targetWeek.toString() + "--" + Week,
                                                Toast.LENGTH_SHORT
                                            ).show()*/

                                            if (targetWeek == Week) {
                                                slotList.add(employee)
                                            }
                                        }
                                        if (position == 3) {
                                            /* Toast.makeText(
                                                 this@AppointmentList2,
                                                 targetMonth.toString() + "--" + month,
                                                 Toast.LENGTH_SHORT
                                             ).show()*/
                                            if (targetMonth == month) {
                                                slotList.add(employee)
                                            }
                                        }
                                    }
                                }
                                val sortedList = slotList.sortedWith(compareBy({ it.date }, { it.begins_At }))

                                val adapter =
                                    CustomAdapter2(this@AppointmentList2, R.layout.showappointmentvalue, sortedList)
                                listview.adapter = adapter
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                    //slotList.clear()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }
        }
        //endregion
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

