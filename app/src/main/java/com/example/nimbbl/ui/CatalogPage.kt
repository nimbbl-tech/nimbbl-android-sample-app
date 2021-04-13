package com.example.nimbbl.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimbbl.R
import com.example.nimbbl.data.model.model.Catalog_Model
import kotlinx.android.synthetic.main.activity_catalog_page.*
import tech.nimbbl.checkout.sdk.NimbblCheckoutOptions
import tech.nimbbl.checkout.sdk.NimbblCheckoutSDK
import tech.nimbbl.checkout.sdk.NimbblCheckoutPaymentListener

class CatalogPage : AppCompatActivity(), NimbblCheckoutPaymentListener  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog_page)
        var list:List<Catalog_Model> =
            listOf(
                Catalog_Model(
                    "Colourful Mandalas.",
                    "₹ 2",
                    "Convert your dreary device into a bright happy place with this wallpaper by Speedy McVroom",
                    1
                ),
                Catalog_Model(
                    "Designer Triangles.",
                    "₹ 4",
                    "Bold blue and deep black triangle designer wallpaper to give your device a hypnotic effect by  chenspec from Pixabay",2)
            )
    setUpRecycelrvView(list)
    }
    fun setUpRecycelrvView(items: List<Catalog_Model>) {
        var ctx = this
        recyclerview_catalog.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                Catalog_Adapter(
                    items,
                    ctx
                ) } }

    override fun onPaymentFailed(p0: String) {
        //to do
    }

    override fun onPaymentSuccess(p0: MutableMap<String, Any>?) {
        Log.d("Nimbbl demo", Integer.toString(p0!!.size))
        //val payload: MutableMap<String, Any>? =
        //    p0.get("payload") as MutableMap<String, Any>?
        Toast.makeText(
            this,
            "OrderId=" + p0.get("order_id") + ", Status=" + p0.get("status"),
            Toast.LENGTH_LONG
        ).show()
        var intent = Intent(this,OrderSucessPageAcitivty::class.java)
        intent.putExtra("orderid", p0.get("order_id").toString())
        intent.putExtra("status",p0.get("status").toString())
        startActivity(intent)

    }

    fun makePayment(orderId: String){
        val b = NimbblCheckoutOptions.Builder()
        val options =
            b.setKey("access_key_1MwvMkKkweorz0ry").setOrderId(orderId).build()
        NimbblCheckoutSDK.getInstance().init(this)
        NimbblCheckoutSDK.getInstance().checkout(options)
    }
}