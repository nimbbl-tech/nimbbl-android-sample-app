package com.example.nimbbl.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nimbbl.R
import com.example.nimbbl.ui.NimbblNativePaymentActivity
import com.zl.nimbblpaycoresdk.models.Item


/*
Created by Sandeep Yadav on 28/02/22.
Copyright (c) 2022 Bigital Technologies Pvt Ltd. All rights reserved.
*/
class ListOfWalletAdapter(private var context: NimbblNativePaymentActivity, private val itemCol: List<Item>) : RecyclerView.Adapter<WalletListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WalletListViewHolder(
            inflater,
            parent
        )
    }

    override fun onBindViewHolder(holder: WalletListViewHolder, position: Int) {
        val item : Item = itemCol[position]
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
       return  itemCol.size
    }

}

class WalletListViewHolder (inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.gridview_item,parent,false)){
    private var tvName: TextView?=null
    private var ivIcon: ImageView?=null



    init {
        tvName = itemView.findViewById(R.id.tv_name)
        ivIcon = itemView.findViewById(R.id.iv_icon)



    }
    fun bind(item: Item, context: NimbblNativePaymentActivity){
        tvName!!.text = item.sub_payment_name
        ivIcon?.let {
            Glide.with(context).load(item.logo_url)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(context.getDrawable(R.drawable.credit_card))
                .into(it)
        }
        ivIcon?.setOnClickListener {
            context.initiatePayment("Wallet",item)
        }


    }
}