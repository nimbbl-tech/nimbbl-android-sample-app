package com.example.nimbbl.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimbbl.NimmblConfigActivity
import com.example.nimbbl.R
import com.example.nimbbl.data.model.model.Catalog_Model
import com.example.nimbbl.data.model.model.postbody.GenerateTokenbody
import com.example.nimbbl.data.model.network.ApiCall.Companion.BASE_URL
import com.example.nimbbl.data.model.repository.CatalogRepository
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutSDK
import com.zl.nimbblpaycoresdk.interfaces.NimbblCheckoutPaymentListener
import com.zl.nimbblpaycoresdk.interfaces.NimbblInItResourceListener
import kotlinx.android.synthetic.main.activity_catalog_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import tech.nimbbl.checkout.sdk.NimbblCheckoutSDK

@DelicateCoroutinesApi
class CatalogPage : AppCompatActivity(), NimbblCheckoutPaymentListener, NimbblInItResourceListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog_page)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val body =  GenerateTokenbody("access_key_KO7dM2Y6qaQj6vdl","access_secret_WKO7dkzGerjWe0dl")
                val response = CatalogRepository().generateToken("https://devapi.nimbbl.tech/api/v2/generate-token",body)
                if (response.isSuccessful) {
                    Log.i("SAN","response.body().token-->"+ (response.body()?.token ?: ""))

                    val inputInItPayload = JSONObject()
                    inputInItPayload.put("token",response.body()?.token ?: "")
                    val nimbblJsonPayload = JSONObject()
                    nimbblJsonPayload.put("access_key","access_key_KO7dM2Y6qaQj6vdl")
                    nimbblJsonPayload.put("packageName", applicationContext.packageName)
                    inputInItPayload.put("nimbblPayload",nimbblJsonPayload)

                    NimbblPayCheckoutSDK.instance?.initResource(this@CatalogPage,inputInItPayload.toString(),this@CatalogPage)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
        var list: List<Catalog_Model> =
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
                    "Bold blue and deep black triangle designer wallpaper to give your device a hypnotic effect by  chenspec from Pixabay",
                    2
                )
            )
        setUpRecycelrvView(list)
        val preferences = getSharedPreferences("nimmbl_configs_prefs", MODE_PRIVATE)
        BASE_URL = preferences.getString("shop_base_url",BASE_URL).toString()
        tv_settings.setOnClickListener {
            val intent = Intent(this,NimmblConfigActivity::class.java)
            startActivity(intent)
        }
    }

    fun setUpRecycelrvView(items: List<Catalog_Model>) {
        val ctx = this
        recyclerview_catalog.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                Catalog_Adapter(
                    items,
                    ctx
                )
        }
    }


    override fun onPaymentFailed(p0: String) {
        Toast.makeText(
            this,
            "Failed: $p0",
            Toast.LENGTH_LONG
        ).show()
    }

/*    override fun onEventReceived(data: JSONObject) {
        try {
            val event = data.getString ("event");
            when {
                event.equals("display_loader") -> {
                    // Show some loader here
                }
                event.equals("hide_loader") -> {
                    // Hide Loader
                }
                event.equals("initiate_result") -> {
                    // Get the response
                    val response = data.optJSONObject ("nimbblPayload");
                }
                event.equals("process_result") -> {
                    // Get the response
                    val response = data.optJSONObject ("nimbblPayload");
                    //Merchant handling
                }
                event.equals("exception_occured") -> {
                    // merchant code...

                }
            }
        } catch (e:Exception) {
            // merchant code...
        }
    }*/

    override fun onPaymentSuccess(p0: MutableMap<String, Any>) {
        Log.d("Nimbbl demo", Integer.toString(p0.size))
        //val payload: MutableMap<String, Any>? =
        //    p0.get("payload") as MutableMap<String, Any>?
        Toast.makeText(
            this,
            "OrderId=" + p0.get("order_id") + ", Status=" + p0.get("status"),
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this, OrderSucessPageAcitivty::class.java)
        intent.putExtra("orderid", p0.get("order_id").toString())
        intent.putExtra("status", p0.get("status").toString())
        startActivity(intent)

    }

    fun makePayment(orderId: String) {
        val b = com.zl.nimbblpaycoresdk.models.NimbblCheckoutOptions.Builder()
        val preferences = getSharedPreferences("nimmbl_configs_prefs", MODE_PRIVATE)
        val baseUrl   = preferences.getString("shop_base_url", BASE_URL).toString()
        var accessKey =  "access_key_1MwvMkKkweorz0ry"

        var apiUrl = ""
        var webViewUrl = ""
        var webViewRespUrl = ""
        when {
            baseUrl.equals("https://devshop.nimbbl.tech/api/") -> {
               apiUrl = "https://devapi.nimbbl.tech/api/v2/"
                webViewUrl ="https://devcheckout.nimbbl.tech/?modal=false&order_id="
                webViewRespUrl ="https://devcheckout.nimbbl.tech/mobile/redirect"
                accessKey =  preferences.getString("access_key_dev","access_key_1MwvMkKkweorz0ry").toString()
            }
            baseUrl.equals("https://uatshop.nimbbl.tech/api/") -> {
                apiUrl = "https://uatapi.nimbbl.tech/api/v2/"
                webViewUrl ="https://uatcheckout.nimbbl.tech/?modal=false&order_id="
                webViewRespUrl ="https://uatcheckout.nimbbl.tech/mobile/redirect"
                accessKey =  preferences.getString("access_key_uat","access_key_1MwvMkKkweorz0ry").toString()
            }
            baseUrl.equals("https://shoppp.nimbbl.tech/api/") -> {
                apiUrl = "https://apipp.nimbbl.tech/api/v2/"
                webViewUrl ="https://checkoutpp.nimbbl.tech/?modal=false&order_id="
                webViewRespUrl ="https://checkoutpp.nimbbl.tech/mobile/redirect"
                accessKey =  preferences.getString("access_key_preprod","access_key_1MwvMkKkweorz0ry").toString()
            }
            baseUrl.equals("https://shop.nimbbl.tech/api/") -> {
                apiUrl = "https://api.nimbbl.tech/api/v2/"
                webViewUrl ="https://checkout.nimbbl.tech/?modal=false&order_id="
                webViewRespUrl ="https://checkout.nimbbl.tech/mobile/redirect"
                accessKey =  preferences.getString("access_key_prod","access_key_1MwvMkKkweorz0ry").toString()
            }
        }
        val appMode = preferences.getString("sample_app_mode","browser")
        if(appMode.equals("browser")) {
            val options = b.setKey(accessKey).setOrderId(orderId).build()
            NimbblCheckoutSDK.instance?.init(this, apiUrl, webViewUrl, webViewRespUrl)
            NimbblCheckoutSDK.instance?.checkout(options)
        }else {
            //val intent =  Intent(this,NimbblNativePaymentActivity::class.java)
            //startActivity(intent)


        }

    }

    override fun onResourceLoaded(isLoaded: Boolean) {
        Log.d("Nimbbl", "isLoaded-->$isLoaded")
    }
}