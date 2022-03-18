package com.example.nimbbl.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nimbbl.MainActivity
import com.example.nimbbl.R
import com.example.nimbbl.model.CatalogModel

class Catalog_Adapter(private val list: List<CatalogModel>, private val context: MainActivity) :
    RecyclerView.Adapter<ProejectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProejectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProejectViewHolder(
            inflater,
            parent
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProejectViewHolder, position: Int) {
        val assesment : CatalogModel = list[position]
        holder.bind(assesment,context)
    }
}

class ProejectViewHolder (inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.catalog_item,parent,false)){
    private var txt_title: TextView?=null
    private var txt_rupees: TextView?=null
    private var txt_desc: TextView?=null
    private var img: ImageView?=null
    private var btn_buynow: Button?=null




    init {
        txt_title = itemView.findViewById(R.id.txt_title)
        txt_rupees = itemView.findViewById(R.id.txt_ruppes)
        txt_desc = itemView.findViewById(R.id.txt_dscription)
        img = itemView.findViewById(R.id.img)
        btn_buynow = itemView.findViewById(R.id.btn_buynow)



    }
    fun bind(assesment: CatalogModel, context: MainActivity){
        txt_title!!.setText(assesment.title)
        txt_rupees!!.setText(assesment.price)
        txt_desc!!.text = assesment.description
        if (assesment.price.equals("₹ 2")){
            img!!.setImageDrawable(context.resources.getDrawable(R.drawable.img_1))
        }else{
            img!!.setImageDrawable(context.resources.getDrawable(R.drawable.img_2))
        }

        btn_buynow!!.setOnClickListener {
            context.buyNow(assesment.id)


        }

    }
}