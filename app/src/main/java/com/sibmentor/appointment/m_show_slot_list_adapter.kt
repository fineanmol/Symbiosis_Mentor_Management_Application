package com.sibmentor.appointment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class m_show_slot_list_adapter(val mCtx: Context, val layoutId: Int, val slotList: List<BookedData>) :
    ArrayAdapter<BookedData>(mCtx, layoutId, slotList) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("Slots")
    val userref = FirebaseDatabase.getInstance().getReference("users")
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val name = view.findViewById<TextView>(R.id.student_Name)
        val date = view.findViewById<TextView>(R.id.dateslot)
        val TimeslotTextView = view.findViewById<TextView>(R.id.textView)

        val slotTiming = view.findViewById<TextView>(R.id.slot_timing)
        val status = view.findViewById<TextView>(R.id.status)
        val delete = view.findViewById<TextView>(R.id.deletebtn)


        val slot = slotList[position]


        date.text = "${slot.date.split("/").first()} - ${slot.date.split("/")[1]}"

        slotTiming.text = slot.begins_At.split("[").last().toString() + ("-").toString() + slot.stop_At
        if (slot.status == "B") {
            status.setTextColor(Color.GREEN)
            name.setTextColor(Color.RED)
            status.text = context.getString(R.string.slot_status)
            name.text = "By: ${slot.reserved_by}"

        }
        if (slot.status != "B") {
            status.setTextColor(Color.LTGRAY)


            status.text = "Not Booked Yet"
        }

        /** Delete Button for Mentor*/
        delete.setOnClickListener {
            val alertbox = AlertDialog.Builder(mCtx)
                .setMessage("Do you want to Delete this Appointment?")
                .setPositiveButton("Delete", DialogInterface.OnClickListener { arg0, arg1 ->
                    deleteInfo(slot)
                })
                .setNegativeButton("No", // do something when the button is clicked
                    DialogInterface.OnClickListener { arg0, arg1 -> })
                .show()
        }



        return view
    }

    /** Delete Button Functionality for Mentor*/
    private fun deleteInfo(slots: BookedData) {
        var s_id = ""
        /** User Data Updated Function*/

        if (slots.reserved_by != "" && slots.studentId != "") {

                    currentUser?.let { user ->

                        val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
                        val eventListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                                //create new user
                                Toast.makeText(mCtx, "User details not found", Toast.LENGTH_LONG).show()
                                //  logout()
                            } else {
                                var lastvalue = "Slots"
                                for (e in dataSnapshot.children) {
                                    val employee = e.getValue(Data::class.java)
                                    var name = (employee!!.name).split(" ").first()
                                    var eid = employee.studentId
                                    deletebookedvalue(name, eid, lastvalue, slots, mCtx)


                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        }
                        userNameRef?.addListenerForSingleValueEvent(eventListener)

                    }

                }


        if (slots.reserved_by == "") {
            currentUser?.let { user ->

                val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
                val eventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                        //create new user
                        Toast.makeText(mCtx, "User details not found", Toast.LENGTH_LONG).show()
                        //  logout()
                    } else {
                        var lastvalue = "Slots"
                        for (e in dataSnapshot.children) {
                            val employee = e.getValue(Data::class.java)
                            var name = (employee!!.name).split(" ").first()
                            var eid = employee.studentId
                            deleteemptyvalue(name, eid, lastvalue, slots, mCtx)


                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                }
                userNameRef?.addListenerForSingleValueEvent(eventListener)

            }


        }

    }
}

private fun deleteemptyvalue(
    name: String,
    eid: String,
    lastvalue: String,
    slots: BookedData,
    mCtx: Context
) {
    var nodeid = name.split(" ").first() + eid + lastvalue
    val myDatabase = FirebaseDatabase.getInstance().getReference("Slots").child(nodeid)
    myDatabase.child(slots.mentorcode).removeValue()
    Toast.makeText(mCtx, "You Deleted an Empty Slot!", Toast.LENGTH_LONG).show()
}

private fun deletebookedvalue(
    name: String,
    eid: String,
    lastvalue: String,
    slots: BookedData,
    mCtx: Context
) {

    // userref.child(s_id).child("status").setValue("NB")
    var nodeid = name.split(" ").first() + eid + lastvalue
    val myDatabase = FirebaseDatabase.getInstance().getReference("Slots").child(nodeid)
    myDatabase.child(slots.mentorcode).removeValue()

    val ref = FirebaseDatabase.getInstance().getReference("Slots")
    val userNameRef = ref.parent?.child("users")?.orderByChild("studentId")?.equalTo(slots.studentId)
    userNameRef?.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(p0: DataSnapshot) {
            for (e in p0.children) {
                val student = e.getValue(Data::class.java)
                var mentor_codes = student?.mentorreferal.toString()

                    var temp_list= mentor_codes.split("/").toMutableList()

                    var pos=temp_list.indexOf(name+eid+lastvalue+":B")
                    if(pos<0){
                        break
                    }
                    else{
                        temp_list[pos]=name+eid+lastvalue+":NB"

                        Log.d("TAG41",pos.toString())

                        var fcodes=temp_list.toString().replace("[","").replace("]","").replace(",","/").replace(" ","")
                        Log.d("TAG41",fcodes)
                        ref.parent?.child("users")?.child(student?.id.toString())?.child("mentorreferal")?.setValue(fcodes)
                    }


                userNameRef.removeEventListener(this)
            }
        }
    })
    Toast.makeText(
        mCtx,
        "Deleted ! \n Please tell ${slots.reserved_by}  to book Again",
        Toast.LENGTH_LONG
    ).show()
}