package com.sibmentors.appointment

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class student_reserved_slot_adapter(val mCtx: Context, val layoutId: Int, val slotList: List<slotsData>) :
    ArrayAdapter<slotsData>(mCtx, layoutId, slotList) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("Slots")
    val userref = FirebaseDatabase.getInstance().getReference("users")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val name = view.findViewById<TextView>(R.id.mentor_Name)
        val date = view.findViewById<TextView>(R.id.dateslot)
        val month = view.findViewById<TextView>(R.id.monthslot)
        val slotTiming = view.findViewById<TextView>(R.id.reserved_slot_timing)


        val slot = slotList[position]

        var temp_date = slot.date.split("/").first().toInt()
        var temp_month = slot.date.split("/")[1].toInt()
        var temp_year = slot.date.split("/").last().toInt()

        val calendar1 = Calendar.getInstance()
        calendar1.set(Calendar.YEAR, temp_year)
        calendar1.set(Calendar.MONTH, temp_month)
        calendar1.set(Calendar.DATE, temp_date)
        val dateText = DateFormat.format("EEEE,d MMMM,yyyy", calendar1).toString()
        date.text = dateText.split(" ").first().toString()
        month.text = dateText.split(" ").last().toString()
        name.text = slot.generated_by
        slotTiming.text = (slot.begins_At + ("-").toString() + slot.stop_At)

        return view
    }
}

