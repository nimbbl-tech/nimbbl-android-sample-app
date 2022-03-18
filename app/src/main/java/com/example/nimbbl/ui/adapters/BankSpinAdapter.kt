package com.example.nimbbl.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.nimbbl.R
import com.zl.nimbblpaycoresdk.models.Item


class BankSpinAdapter(
    context: Context,
    resouceId: Int,
    textviewId: Int,
    val list: List<Item>,
    private val textColor: Int,
    private val textBackgroundColor: Int
) :
    ArrayAdapter<Item?>(context, resouceId, textviewId, list) {

    private var flater: LayoutInflater? = null


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
      //  val getrow: Any = list[position]
        //val t: LinkedTreeMap<*, *> = getrow as LinkedTreeMap<*, *>
       // view.text  = t["sub_payment_name"].toString()
        view.text  = list[position].sub_payment_name
        view.setTextColor(textColor)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return rowview(convertView, position, parent)
    }

    @SuppressLint("SetTextI18n")
    private fun rowview(
        convertView: View?,
        position: Int,
        parent: ViewGroup?
    ): View {
        val holder: ViewHolder
        var rowview: View? = convertView
        if (rowview == null) {
            holder = ViewHolder()
            flater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowview = flater!!.inflate(R.layout.spinner_dropdown, parent, false)
            holder.txtTitle = rowview.findViewById(R.id.text1)
            holder.txtTitle!!.setTextColor(textColor)
            holder.txtTitle!!.setBackgroundColor(textBackgroundColor)
            rowview?.tag = holder
        } else {
            holder = rowview.tag as ViewHolder
        }
     //   val getrow: Any = list[position]
      //  val t: LinkedTreeMap<*, *> = getrow as LinkedTreeMap<*, *>
        //holder.txtTitle?.text = t["sub_payment_name"].toString()
        holder.txtTitle?.text = list[position].sub_payment_name
        //holder.txtTitle?.text = rowItem?.code + "(" + rowItem?.country + ")"
        return rowview!!
    }

    private inner class ViewHolder {
        var txtTitle: TextView? = null
    }
}