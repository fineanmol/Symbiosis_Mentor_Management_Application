package com.sibmentors.appointment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
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


class customAdapter(val mCtx: Context, val layoutId: Int, val slotList: List<BookedData>) :
    ArrayAdapter<BookedData>(mCtx, layoutId, slotList) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("Slots")
    val userref = FirebaseDatabase.getInstance().getReference("users")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val mentor = view.findViewById<TextView>(R.id.mentor_Name)
        val date = view.findViewById<TextView>(R.id.dateslot)
        val time = view.findViewById<TextView>(R.id.slot_timing)
        val slot = slotList[position]

        currentUser?.let { user ->

            val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                    //create new user
                    Toast.makeText(mCtx, "User details not found", Toast.LENGTH_LONG).show()
                    //logout()
                } else {

                    for (e in dataSnapshot.children) {
                        val employee = e.getValue(Data::class.java)
                        var refercode = employee!!.mentorreferal
                        bookbtnclickmethod(view, slot, time, refercode)


                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            userNameRef?.addListenerForSingleValueEvent(eventListener)

        }



        mentor.text = slot.generated_by
        date.text = slot.date
        time.text = (slot.begins_At + ("-").toString() + slot.stop_At)


        return view
    }

    private fun bookbtnclickmethod(
        view: View,
        slot: BookedData,
        time: TextView,
        refercode: String
    ) {
        val book = view.findViewById<TextView>(R.id.bookbtn)
        book.setOnClickListener {
            val alertbox = AlertDialog.Builder(mCtx)
                .setMessage("Do you want to Book this Appointment?")
                .setPositiveButton("Book", DialogInterface.OnClickListener { arg0, arg1 ->
                    // do something when the button is clicked
                    //region StudentBookButtonFunction
                    var id = slot.sid

                    currentUser?.let { user ->
                        // Toast.makeText(mCtx, user.email, Toast.LENGTH_LONG).show()
                        val userNameRef = ref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
                        val eventListener = object : ValueEventListener {
                            @SuppressLint("ResourceAsColor")
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    //create new user
                                    Toast.makeText(mCtx, "No Appointments are Available Yet!!", Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    for (e in dataSnapshot.children) {
                                        val employee = e.getValue(Data::class.java)
                                        var studentId = employee?.studentId
                                        var studentName = employee?.name
                                        var phone = employee?.number
                                        var studentkey = employee?.id
                                        var status = employee?.status
                                        var mentorrefercodes = employee!!.mentorreferal

                                        var mentorcodes = mentorrefercodes.split("/")
                                        for (i in mentorcodes) {
                                            if (i.split(":").last() == "NB" && i.split(":").first() == id) {

                                                var newrefercodes = mentorrefercodes.replace(i, "$id:B")
                                                userref.child(studentkey!!).child("mentorreferal")
                                                    .setValue(newrefercodes)
                                                ref.child(id).child(slot.mentorcode).child("studentNumber")
                                                    .setValue(phone)
                                                ref.child(id).child(slot.mentorcode).child("reserved_by")
                                                    .setValue(studentName)
                                                ref.child(id).child(slot.mentorcode).child("studentId")
                                                    .setValue(studentId)
                                                ref.child(id).child(slot.mentorcode).child("status").setValue("B")
                                                Toast.makeText(
                                                    mCtx,
                                                    "$studentName's Appointment Booked! \n at: ${time.text}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                book.text = "Booked!"
                                                book.setBackgroundColor(R.color.material_drawer_dark_selected)
                                                userref.child(studentkey).child("status").setValue("")
                                            }
                                            if (i.split(":").last() == "B" && i.split(":").first() == id) {

                                                Toast.makeText(
                                                    mCtx,
                                                    "You have already booked an appointment.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }


                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        }
                        userNameRef?.addListenerForSingleValueEvent(eventListener)

                    }
                    //endregion

                })
                .setNegativeButton("No", // do something when the button is clicked
                    DialogInterface.OnClickListener { arg0, arg1 -> })
                .show()


        }
    }
}