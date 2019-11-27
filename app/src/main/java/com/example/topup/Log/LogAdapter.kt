package com.example.paymenttopup.Log

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.example.paymenttopup.Model.Topup
import com.example.topup.R
import kotlinx.android.synthetic.main.row_layout.view.*


class LogAdapter(internal var activity:Activity,
                 internal var showLog:List<Topup>,
                 internal var edt_id: TextView,
                 internal var edt_money: EditText
):BaseAdapter() {

    internal var inflater:LayoutInflater

    init {
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView:View
        rowView = inflater.inflate(R.layout.row_layout,null)

        rowView.edt_id.text = showLog[position].id.toString()
        rowView.edt_money.text = showLog[position].id.toString()

        rowView.setOnClickListener{
            edt_id.setText(rowView.edt_id.text.toString())
            edt_money.setText(rowView.edt_money.text.toString())
        }
        return rowView
    }

    override fun getItem(position: Int): Any {
        return showLog[position]
    }

    override fun getItemId(position: Int): Long {
        return showLog[position].id.toLong()
    }

    override fun getCount(): Int {
        return showLog.size
    }

}