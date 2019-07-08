package com.sibmentors.appointment

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


class local_slot_adapter(val mCtx: Context, val layoutId: Int, var local_slotList: MutableList<String>) :
    ArrayAdapter<String>(mCtx, layoutId, local_slotList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val stime = view.findViewById<TextView>(R.id.stime)
        val etime = view.findViewById<TextView>(R.id.etime)
        val date = view.findViewById<TextView>(R.id.date)
        val delete = view.findViewById<TextView>(R.id.delete)


        val slotss = local_slotList[position]
        var slots = slotss.split("[").last().toString()
        var slot = slots.split("]").first().toString()
        date.text = slot.split("$").last().toString().split("]").first().toString()
        stime.text = slot.split("$").first().split("-").first().toString().split("[").last().toString()
        etime.text = slot.split("$").first().split("-").last().toString()

        delete.setOnClickListener {
            local_slotList.removeAt(position)
            Log.d("TAG2", position.toString())

            for (x in local_slotList) {
                Log.d("TAG2", x)
            }
            notifyDataSetChanged()
            Snackbar.make(view, "Deleted Successfully", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }

        val intent = Intent("custom-message")
        //intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
        intent.putExtra("quantity", local_slotList.toString())
        intent.putExtra("item", "Slot List")
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        return view
    }
}

