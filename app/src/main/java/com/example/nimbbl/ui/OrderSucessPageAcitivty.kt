package com.example.nimbbl.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import tech.nimbbl.example.R

class OrderSucessPageAcitivty : AppCompatActivity() {
    lateinit  var order_id :String
    lateinit  var status :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_sucess_page)
        var txt_orderid = findViewById<TextView>(R.id.txt_orderid)
        var txt_status = findViewById<TextView>(R.id.txt_status)
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