package com.sibmentors.appointment

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


class customAdapter(val mCtx: Context, val layoutId: Int, val slotList: List<slotsData>) :
    ArrayAdapter<slotsData>(mCtx, layoutId, slotList) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("Slots")
    val userref = FirebaseDatabase.getInstance().getReference("users")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val mentor = view.findViewById<TextView>(R.id.mentor_Name)
        val date = view.findViewById<TextView>(R.id.dateslot)
        val time = view.findViewById<TextView>(R.id.slot_timing)

        val book = view.findViewById<TextView>(R.id.bookbtn)
        //val deleteBtn = view.findViewById<TextView>(R.id.delete)

        val slot = slotList[position]

        mentor.text = slot.generated_by
        date.text = slot.date
        time.text = (slot.begins_At + ("-").toString() + slot.stop_At)

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
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    //create new user
                                    Toast.makeText(mCtx, "No Appointments are Available Yet!!", Toast.LENGTH_LONG).show()
                                } else {
                                    for (e in dataSnapshot.children) {
                                        val employee = e.getValue(Data::class.java)
                                        var studentId = employee?.studentId
                                        var studentName = employee?.name
                                        var phone = employee?.number
                                        var studentkey = employee?.id
                                        var status = employee?.status
                                        if (status == "NB") {
                                            userref.child(studentkey!!).child("status").setValue("B")
                                            ref.child("Nikhil Nishad").child(id).child("studentNumber").setValue(phone)
                                            ref.child("Nikhil Nishad").child(id).child("reserved_by").setValue(studentName)
                                            ref.child("Nikhil Nishad").child(id).child("studentId").setValue(studentId)
                                            ref.child("Nikhil Nishad").child(id).child("status").setValue("B")
                                            Toast.makeText(
                                                mCtx,
                                                "$studentName Appointment Booked! \n at: ${time.text}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                mCtx,
                                                "You have already booked an appointment.",
                                                Toast.LENGTH_LONG
                                            ).show()

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
        return view
    }
}