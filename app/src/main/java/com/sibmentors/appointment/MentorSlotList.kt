package com.sibmentors.appointment

import android.content.*
import android.content.ClipData
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
        var  end1 = end.split("$").first().split("-").last().toString().trim()

        currentUser?.let { user ->

            val userNameRef = ref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            userNameRef?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(
                            this@MentorSlotList,
                            "User Not Registered",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val namernd = (5..10000).random()
                        for (e in dataSnapshot.children) {
                            val employee = e.getValue(Data::class.java)!!
                            val reserved_by = ""
                            var generated = employee.name
                            var studentId = ""
                            var studentNumber = ""
                            var status = "NB"
                            var mentorcode = employee.name + namernd
                            val sId = """${generated.split(" ").first()}${employee.studentId}B$rnds"""
                            val addSlot = slotsData(sId, begin1, end1, date1, generated, reserved_by, studentId, studentNumber, status)
                            ref.child(sId).child(mentorcode).setValue(addSlot)
                            //region CopyMentorCode
                            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "label",
                                "This is the unique code of your Mentor\nwhich is required to book their slots :$sId"
                            )
                            clipboard.primaryClip = clip
                            //endregion
                            Toast.makeText(
                                this@MentorSlotList,
                                "Unique Slot code Copied,\n Share this code to Students\n Its needed while Booking",
                                Toast.LENGTH_LONG
                            ).show()


                        }

                    }
                }
            })
        }}

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
                                    val intent = Intent(this, mentorhomev2::class.java)
                                    startActivity(intent)
                                })
                            .show()
                        /*Alert Box*/

                    }
                    if (parts1.size <= 1) {
                        /**Alert Box*/
                        val alertbox = AlertDialog.Builder(this)
                            .setMessage("No slot present to be submit \n Generate slots again?")
                            .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
                                // do something when the button is clicked
                                val intent = Intent(this, addSlotActivity::class.java)
                                startActivity(intent)


                            })
                            .setNegativeButton("No", // do something when the button is clicked

                                DialogInterface.OnClickListener { arg0, arg1 ->
                                    val intent = Intent(this, mentorhomev2::class.java)
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