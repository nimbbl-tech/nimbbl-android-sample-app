package com.example.nimbbl.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nimbbl.R
import com.example.nimbbl.data.model.model.Catalog_Model
import com.example.nimbbl.data.model.model.postbody.Catlogbody
import com.example.nimbbl.data.model.network.ApiCall.Companion.BASE_URL
import com.example.nimbbl.data.model.repository.CatalogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Catalog_Adapter(private val list: List<Catalog_Model>, private val context: CatalogPage) :
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
        val assesment : Catalog_Model = list[position]
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
    fun bind(assesment: Catalog_Model, context: CatalogPage){
        txt_title!!.setText(assesment.title)
        txt_rupees!!.setText(assesment.price)
        txt_desc!!.text = assesment.description
        if (assesment.price.equals("₹ 2")){
            img!!.setImageDrawable(context.resources.getDrawable(R.drawable.img_1))
        }else{
            img!!.setImageDrawable(context.resources.getDrawable(R.drawable.img_2))
        }

        btn_buynow!!.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val id = Catlogbody(assesment.id)
                try {
                    val preferences = btn_buynow!!.context.getSharedPreferences("nimmbl_configs_prefs",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    val baseUrl   = preferences.getString("shop_base_url",BASE_URL).toString()
                    val response = CatalogRepository().CreateOrder(baseUrl+"orders/create",id)
                    if (response.isSuccessful) {
                        Log.i("response",response.body()!!.result.item.order_id)
                        context.makePayment(response.body()!!.result.item.order_id)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    Toast.makeText(context,"Unable to create order,",Toast.LENGTH_SHORT).show()
                }

            }

        }

    }
}