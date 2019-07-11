package com.sibmentors.appointment

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.util.Log

class MentorList : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var mentorList: MutableList<String>
    lateinit var listview: ListView
    val userref = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mentor_list)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
        mentorList = mutableListOf()
        listview = findViewById(R.id.mentorlistview)


        currentUser?.let { user ->

            val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                    //create new user
                    Toast.makeText(this@MentorList, "User details not found\n Login Again", Toast.LENGTH_LONG).show()
                    logout()
                } else {

                    for (e in dataSnapshot.children) {
                        val user = e.getValue(Data::class.java)
                        var referal = user!!.mentorreferal


                        var mentorlists = referal.split("/")
                        for(i in mentorlists){
                            var mentordetail= i.split(":").first()

                                mentorList.add(mentordetail)

                            Log.d("TAG1",mentordetail)
                            val adapter = MentorListAdapter(this@MentorList, R.layout.mentorlistview_custom, mentorList)
                            listview.adapter = adapter
                        }

                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            userNameRef?.addListenerForSingleValueEvent(eventListener)

        }


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
