package com.sibmentors.appointment


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
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

class MentorListAdapter(val mCtx: Context, val layoutId: Int, val mentorList: List<String>) :
    ArrayAdapter<String>(mCtx, layoutId, mentorList) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("Slots")
    val userref = FirebaseDatabase.getInstance().getReference("users")


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)
        val mentorname = view.findViewById<TextView>(R.id.mentorName)
        val mentorid = view.findViewById<TextView>(R.id.mentorId)
        val deletebtn = view.findViewById<TextView>(R.id.delete)
        val dividerbtn = view.findViewById<View>(R.id.divider)
        val listmentor = mentorList[position]
        Log.d("TAG1", listmentor)

        var extractedid = ""
        for (i in listmentor) {
            if (i.isDigit())
                extractedid += i
        }
        if (listmentor.isNotEmpty() || (listmentor != "MentorCodes" && listmentor != "")) {
            val userNameRef = ref.parent?.child("users")?.orderByChild("studentId")?.equalTo(extractedid)
            val eventListener = object : ValueEventListener {
                @SuppressLint("ResourceAsColor")
                override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                    //create new user

                } else {
                    for (e in dataSnapshot.children) {
                        val employee = e.getValue(Data::class.java)
                        mentorname.text=employee?.name

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            userNameRef?.addValueEventListener(eventListener)
            mentorid.text = listmentor
            //mentorname.text = listmentor
        }
        if (listmentor == "" || listmentor.isNullOrBlank()) {
            deletebtn.visibility = View.GONE
            mentorid.text = ""
            mentorname.text = ""
            dividerbtn.visibility = View.INVISIBLE
            Toast.makeText(mCtx, "You have to add atleast 1 mentor's Code \n <-- GoBack and Add First", Toast.LENGTH_LONG).show()
        }
        deletebtn.setOnClickListener {


            val alertbox = AlertDialog.Builder(mCtx)
                .setMessage("Do you want to Delete this Mentor !!\nYou have to add this mentor's code again if you want to see their slots")
                .setPositiveButton("Delete", DialogInterface.OnClickListener { arg0, arg1 ->
                    // do something when the button is clicked
                    //region StudentBookButtonFunction

                    currentUser?.let { user ->
                        // Toast.makeText(mCtx, user.email, Toast.LENGTH_LONG).show()
                        val userNameRef = ref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
                        val eventListener = object : ValueEventListener {
                            @SuppressLint("ResourceAsColor")
                            override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                                //create new user
                                Toast.makeText(mCtx, "No Appointments are Available Yet!!", Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                for (e in dataSnapshot.children) {
                                    val employee = e.getValue(Data::class.java)

                                    var studentkey = employee?.id
                                    var mentorrefercodes = employee!!.mentorreferal

                                    var mentorcodes = mentorrefercodes.split("/")
                                    var newcodes = ""
                                    for (i in mentorcodes) {
                                        if (i.split(":").first() == listmentor) {

                                            var newrefercodes = mentorrefercodes.replace("/$i/", "/")
                                            var newrefercodes1 = newrefercodes.replace("$i/", "")
                                            var newrefercodes2 = newrefercodes1.replace("/$i", "")
                                            var newrefercodes3 = newrefercodes2.replace("$i", "")
                                            userref.child(studentkey!!).child("mentorreferal")
                                                .setValue(newrefercodes3)
                                            Toast.makeText(mCtx, "Deleted!!  \nNow You won't see their available slots &#128522", Toast.LENGTH_LONG).show()


                                        }

                                    }

                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        }
                        userNameRef?.addListenerForSingleValueEvent(eventListener)

                    }
                })
                .setNegativeButton("No", // do something when the button is clicked
                    DialogInterface.OnClickListener { arg0, arg1 -> })
                .show()


        }






        return view
    }
}
/* private fun deletebtnclickmethod(
     view: View,
     refercode: String,
     listmentor: String
 ) {

     deletebtn.setOnClickListener {
         val alertbox = AlertDialog.Builder(mCtx)
             .setMessage("Do you want to Delete this Mentor !!\nYou have to add this mentor's code again if you want to see their slots")
             .setPositiveButton("Delete", DialogInterface.OnClickListener { arg0, arg1 ->
                 // do something when the button is clicked
                 //region StudentBookButtonFunction

                 currentUser?.let { user ->
                     // Toast.makeText(mCtx, user.email, Toast.LENGTH_LONG).show()
                     val userNameRef = ref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
                     val eventListener = object : ValueEventListener {
                         @SuppressLint("ResourceAsColor")
                         override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                             //create new user
                             Toast.makeText(mCtx, "No Appointments are Available Yet!!", Toast.LENGTH_LONG)
                                 .show()
                         } else {
                             for (e in dataSnapshot.children) {
                                 val employee = e.getValue(Data::class.java)

                                 var studentkey = employee?.id
                                 var mentorrefercodes = employee!!.mentorreferal

                                 var mentorcodes = mentorrefercodes.split("/")
                                 var newcodes=""
                                 for (i in mentorcodes) {
                                     if (i.split(":").first() == listmentor ) {

                                         var newrefercodes=mentorrefercodes.replace("/$i/","/")
                                         var newrefercodes1=newrefercodes.replace("$i/","")
                                         var newrefercodes2=newrefercodes1.replace("/$i","")
                                         var newrefercodes3=newrefercodes2.replace("$i","")
                                         userref.child(studentkey!!).child("mentorreferal").setValue(newrefercodes3)



                                     }

                                 }

                             }
                         }

                         override fun onCancelled(databaseError: DatabaseError) {
                         }
                     }
                     userNameRef?.addListenerForSingleValueEvent(eventListener)

                 }
             })
             .setNegativeButton("No", // do something when the button is clicked
                 DialogInterface.OnClickListener { arg0, arg1 -> })
             .show()


             }
     }
 }*/