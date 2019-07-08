package com.sibmentors.appointment

import android.content.Intent
import android.graphics.Color
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

class mentorShowSlotActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseUser
    lateinit var ref: DatabaseReference
    lateinit var slotList: MutableList<slotsData>
    lateinit var listview: ListView
    var d = " "
    var m = " "
    var y = " "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_show_slot)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
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
            listview = findViewById(R.id.show_allslots_list)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    //  Toast.makeText(this@mentorShowSlotActivity, filter[position], Toast.LENGTH_SHORT).show()
                    ref = FirebaseDatabase.getInstance().getReference("Slots").child("Nikhil Nishad")
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
                                    if (position == 0) {
                                        //    Log.d("TAGDD", targetDate.toString() + "--" + Date)
                                        if (targetDate == Date) {

                                            slotList.add(employee)
                                            if (slotList.isEmpty()) {
                                                Toast.makeText(
                                                    this@mentorShowSlotActivity, // Parent view
                                                    "No Appointments of Today are booked yet", // Message to show
                                                    Toast.LENGTH_LONG // How long to display the message.
                                                ).show()
                                            }

                                        }
                                        /*if (targetDate != Date) {
                                            Toast.makeText(
                                                this@mentorShowSlotActivity, // Parent view
                                                "No Appointments of Today are booked yet", // Message to show
                                                Toast.LENGTH_LONG // How long to display the message.
                                            ).show()
                                        }*/
                                    }
                                    if (position == 1) {

                                        if (targetDate == (Date + 1)) {
                                            slotList.add(employee)
                                            if (slotList.isEmpty()) {
                                                Toast.makeText(
                                                    this@mentorShowSlotActivity, // Parent view
                                                    "No Appointments!! You can take a day off", // Message to show
                                                    Toast.LENGTH_LONG // How long to display the message.
                                                ).show()
                                            }
                                        }
                                        /* if (targetDate != (Date + 1)) {
                                             Toast.makeText(
                                                 this@mentorShowSlotActivity, // Parent view
                                                 "No Appointments!! You can take a day off ", // Message to show
                                                 Toast.LENGTH_LONG // How long to display the message.
                                             ).show()
                                         }*/
                                    }
                                    if (position == 2) {
                                        if (targetWeek == Week) {
                                            slotList.add(employee)
                                            if (slotList.isEmpty()) {
                                                Toast.makeText(
                                                    this@mentorShowSlotActivity, // Parent view
                                                    "No Appointments for whole week!! You can take a week off ", // Message to show
                                                    Toast.LENGTH_LONG // How long to display the message.
                                                ).show()
                                            }
                                        }
                                        /*if (targetWeek != Week) {
                                            Toast.makeText(
                                                this@mentorShowSlotActivity, // Parent view
                                                "No Appointments for whole week!! You can take a week off ", // Message to show
                                                Toast.LENGTH_LONG // How long to display the message.
                                            ).show()
                                        }*/
                                    }
                                    if (position == 3) {
                                        /* Toast.makeText(
                                             this@mentorShowSlotActivity,
                                             targetMonth.toString() + "--" + month,
                                             Toast.LENGTH_SHORT
                                         ).show()*/
                                        if (targetMonth == month) {
                                            slotList.add(employee)
                                        }
                                        if (targetMonth != month) {
                                            Toast.makeText(
                                                this@mentorShowSlotActivity, // Parent view
                                                "No Appointments for whole month!! You can go for Vacation ", // Message to show
                                                Toast.LENGTH_SHORT // How long to display the message.
                                            ).show()
                                        }
                                    }
                                }
                                val adapter =

                                    m_show_slot_list_adapter(
                                        this@mentorShowSlotActivity,
                                        R.layout.show_slot_adapter_layout,
                                        slotList
                                    )
                                listview.adapter = adapter
                            }
                            if (!p0.exists()) {
                                Toast.makeText(
                                    this@mentorShowSlotActivity, // Parent view
                                    "You didn't Created any Slot yet!! \n Go Back and Start New Session ", // Message to show
                                    Toast.LENGTH_SHORT // How long to display the message.
                                ).show()

                                spinner.isEnabled = false

                                toolbar.setBackgroundColor(Color.WHITE)

                                toolbar.visibility = View.GONE


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


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mentorhomev2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        try {
            val id = item.itemId
            if (item.itemId == android.R.id.home) // Press Back Icon
            {
                finish()
            }
            if (id == R.id.action_one) {
                startActivity(Intent(this, addSlotActivity::class.java))
                //Toast.makeText(this, "Add Slot Clicked", Toast.LENGTH_LONG).show()
                return true
            }
            if (id == R.id.action_two) {

                logout()

                return true
            }

            if (id == R.id.contactUs) {

                startActivity(Intent(this, AboutDeveloper::class.java))
                return true
            }

            return super.onOptionsItemSelected(item)
        } catch (e: Exception) {
            e.message
            return super.onOptionsItemSelected(item)
        }
    }
}

