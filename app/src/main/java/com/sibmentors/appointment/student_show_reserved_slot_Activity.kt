package com.sibmentors.appointment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class student_show_reserved_slot_Activity : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var slotList: MutableList<slotsData>
    lateinit var listview: ListView
    private val currentUser = FirebaseAuth.getInstance().currentUser
    var studentName = ""
    var studentId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_show_reserved_slot)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
        slotList = mutableListOf()
        listview = this.findViewById(R.id.reserved_slot_view)
        ref = FirebaseDatabase.getInstance().getReference("Slots")

        currentUser?.let { user ->

            val userNameRef = ref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            userNameRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(
                            this@student_show_reserved_slot_Activity,
                            "User Already Registered",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        for (e in dataSnapshot.children) {
                            val employee = e.getValue(Data::class.java)!!
                            studentName = employee.name
                            studentId = employee.studentId
                            studentshowtheirbooking(employee!!.mentorreferal)

                        }

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

       // Toast.makeText(this, studentName, Toast.LENGTH_LONG).show()

    }
private  fun studentshowtheirbooking(mentorreferal: String) {

    var multiplementors= mentorreferal.split("/")
    for(i in multiplementors){
    var query = ref.child("${i.split(":").first()}").orderByChild("studentId").equalTo(studentId)
    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                slotList.clear()
                for (e in p0.children) {
                    val employee = e.getValue(slotsData::class.java)
                    var status = employee!!.status
                    if (status == "B") {
                        slotList.add(employee)
                    }
                }
                val adapter = student_reserved_slot_adapter(
                    this@student_show_reserved_slot_Activity,
                    R.layout.s_r_s_adapter_list,
                    slotList
                )
                listview.adapter = adapter
            } else {

                Toast.makeText(
                    this@student_show_reserved_slot_Activity,
                    "Please !! Book an Appointment First",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onCancelled(p0: DatabaseError) {
        }

    })}
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



