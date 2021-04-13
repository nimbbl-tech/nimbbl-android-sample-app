package com.example.nimbbl.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nimbbl.R
import kotlinx.android.synthetic.main.activity_order_sucess_page_acitivty.*
import java.lang.Exception

class OrderSucessPageAcitivty : AppCompatActivity() {
    lateinit  var order_id :String
    lateinit  var status :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_sucess_page_acitivty)
        try {

            order_id = intent.getStringExtra("orderid")!!
            status = intent.getStringExtra("status")!!
        }catch (e:Exception){
            e.printStackTrace()
        }
        if (order_id!=null)
            txt_orderid.setText("Order id:- $order_id")
        txt_status.setText("Status:- $status")

    }
}