package com.sibmentor.appointment

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MentorSlotList : AppCompatActivity() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userref = FirebaseDatabase.getInstance().getReference("users")
    lateinit var ref1: DatabaseReference
    lateinit var parts: MutableList<String>
    lateinit var listView: ListView
    var receiver: BroadcastReceiver? = null
    lateinit var qty: String
    lateinit var date: String
    lateinit var stime: String
    lateinit var etime: String
    lateinit var ref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ref = FirebaseDatabase.getInstance().getReference("Slots")
        setContentView(R.layout.activity_mentor_slot_list)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
        listView = this.findViewById(R.id.listview)
        val bundle: Bundle? = intent.extras
        var list: String? = bundle?.getString("slotList")
        //textView.text=list.toString()
        val myString = list.toString()
        parts = myString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()

        parts.forEach {
            for (x in parts) {
                Log.d("TAG1", x)
            }
        }

        val adapter = local_slot_adapter(this, R.layout.slot_local_list_view, parts)

        listView.adapter = adapter

        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("custom-message"))


    }

    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val ItemName = intent.getStringExtra("item")
            qty = intent.getStringExtra("quantity")

        }
    }

    override fun onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        val alertbox = AlertDialog.Builder(this)
            .setMessage("Do you want to leave the page?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
                // do something when the button is clicked
                var intent = Intent(this, addSlotActivity::class.java)
                startActivity(intent)

            })
            .setNegativeButton("No", // do something when the button is clicked
                DialogInterface.OnClickListener { arg0, arg1 -> })
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun addSlot(begin: String, end: String, date: String, rnds: Int) {
        var date1 = date.split("$").last().toString().trim().replace("]", "")
        val begin1 = begin.split("$").first().split("-").first().toString().trim().replace("[", "")
        var end1 = end.split("$").first().split("-").last().toString().trim()

        currentUser?.let { user ->

            val userNameRef = ref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            userNameRef?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                    Toast.makeText(
                        this@MentorSlotList,
                        "User Not Registered",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val namernd = (5..100000).random()
                    for (e in dataSnapshot.children) {
                        val employee = e.getValue(Data::class.java)!!
                        val mentorcodesarray = e.getValue(MentorsCodeArray::class.java)
                        val reserved_by = ""
                        var generated = employee.name
                        var studentId = ""
                        var studentNumber = ""
                        var status = "NB"
                        var mentorcode = employee.name + namernd

                        var sId = """${generated.split(" ").first()}${employee.studentId}Slots"""
                        val addSlot = BookedData(
                            sId,
                            begin1,
                            end1,
                            date1,
                            generated,
                            reserved_by,
                            studentId,
                            studentNumber,
                            status,
                            mentorcode
                        )

                        ref.child(sId).child(mentorcode).setValue(addSlot)
                        val database = FirebaseDatabase.getInstance()
                        val myRef = database.getReference("MentorsCodes")
                        myRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                if (!dataSnapshot.exists()) {
                                    //create new user
                                    myRef.child("List").setValue("")

                                } else {
                                    for (ds in dataSnapshot.children) {
                                        val codes = ds.getValue(String::class.java)
                                        Log.d("TAG", "$codes")
                                        var valu=codes

                                        if (valu.isNullOrBlank() && valu == "") {
                                            myRef.child("List").setValue(sId)
                                        }
                                        if (valu?.length!! > 2) {
                                            var dupfind = valu.split(",")
                                            for (i in dupfind) {
                                                if (i == sId) {
                                                    var status = "false"
                                                    sId = ""
                                                    break
                                                }

                                            }
                                            if (sId != "") {
                                                valu += ",$sId"
                                                myRef.child("List").setValue(valu)
                                            }
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        })



                        Toast.makeText(
                            this@MentorSlotList,
                            "Slots Generated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()


                    }

                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.generatedslotmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (item.itemId == android.R.id.home) // Press Back Icon
        {
            val alertbox = AlertDialog.Builder(this)
                .setMessage("Do you want to leave the page?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
                    // do something when the button is clicked
                    var intent = Intent(this, addSlotActivity::class.java)
                    startActivity(intent)

                })
                .setNegativeButton("No", // do something when the button is clicked
                    DialogInterface.OnClickListener { arg0, arg1 -> })
                .show()
        }

        if (id == R.id.action_logout) {

            logout()

            return true
        }
        if (id == R.id.submit) {

            val alertbox1 = AlertDialog.Builder(this)
                .setMessage("Do you want to Submit?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
                    // do something when the button is clicked

                    var parts1 = qty.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
                    if (parts1.size > 1) {
                        val rnds = (5..1000).random()

                        for (x in parts1) {
                            Log.d("TAG1", x)
                            date = x.split("$").last().toString().trim().replace("]]", "")
                            stime = x.split("$").first().split("-").first().toString().trim().replace("[[", "")
                            etime = x.split("$").first().split("-").last().toString().trim()

                            addSlot(stime, etime, date, rnds)
                            //Toast.makeText(this, stime+"-"+etime+" "+date, Toast.LENGTH_LONG).show()
                        }

                        /*Alert Box*/
                        val alertbox = AlertDialog.Builder(this)
                            .setMessage("Do you want to Add More Slots?")
                            .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
                                // do something when the button is clicked
                                val intent = Intent(this, addSlotActivity::class.java)
                                startActivity(intent)


                            })
                            .setNegativeButton("No", // do something when the button is clicked

                                DialogInterface.OnClickListener { arg0, arg1 ->
                                    val intent = Intent(this, Mentorhomev2::class.java)
                                    startActivity(intent)
                                })
                            .show()
                        /*Alert Box*/

                    }
                    if (parts1.size <= 1) {
                        /**Alert Box*/
                        val alertbox = AlertDialog.Builder(this)
                            .setTitle("Generate slots again?")
                            .setMessage("Minimum two slots required to Submit!")
                            .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
                                // do something when the button is clicked
                                val intent = Intent(this, addSlotActivity::class.java)
                                startActivity(intent)


                            })
                            .setNegativeButton("No", // do something when the button is clicked

                                DialogInterface.OnClickListener { arg0, arg1 ->
                                    val intent = Intent(this, Mentorhomev2::class.java)
                                    startActivity(intent)
                                })
                            .show()
                        /*Alert Box*/
                    }
                })
                .setNegativeButton("No", // do something when the button is clicked

                    DialogInterface.OnClickListener { arg0, arg1 ->

                    })
                .show()


            return true
        }

        if (id == R.id.contactUs) {
            startActivity(Intent(this, AboutDeveloper::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}