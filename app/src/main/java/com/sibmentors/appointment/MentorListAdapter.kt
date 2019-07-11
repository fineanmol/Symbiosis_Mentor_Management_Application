package com.sibmentors.appointment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
        val listmentor = mentorList[position]
        Log.d("TAG1",listmentor)




        mentorname.text = listmentor.toString()
        mentorid.text = "id"
        return view
    }
}