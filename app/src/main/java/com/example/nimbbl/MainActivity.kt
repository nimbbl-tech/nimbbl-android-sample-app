package com.example.nimbbl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimbbl.model.CatalogModel
import com.example.nimbbl.model.postbody.GenerateTokenbody
import com.example.nimbbl.network.ApiCall.Companion.BASE_URL
import com.example.nimbbl.repository.CatalogRepository
import com.example.nimbbl.ui.NimbblConfigActivity
import com.example.nimbbl.ui.NimbblNativePaymentActivity
import com.example.nimbbl.ui.OrderSucessPageAcitivty
import com.example.nimbbl.ui.adapters.Catalog_Adapter
import com.example.nimbbl.utils.AppPayloads
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutSDK
import com.zl.nimbblpaycoresdk.api.ServiceConstants
import com.zl.nimbblpaycoresdk.interfaces.NimbblCheckoutPaymentListener
import com.zl.nimbblpaycoresdk.interfaces.NimbblInItResourceListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.nimbbl.checkout.sdk.NimbblCheckoutSDK

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity(), NimbblCheckoutPaymentListener, NimbblInItResourceListener {

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        NimbblPayCheckoutSDK.getInstance(this@MainActivity)?.isInitialised(this)
        val preferences = getSharedPreferences("nimmbl_configs_prefs", MODE_PRIVATE)
        val appMode = preferences.getString("sample_app_mode", "")
        if (appMode != null) {
            if(appMode.isEmpty()) {
                val intent = Intent(this, NimbblConfigActivity::class.java)
                startActivity(intent)
            }
        }
        BASE_URL = preferences.getString("shop_base_url", BASE_URL).toString()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                var apiUrl = ""
                when {
                    BASE_URL.equals("https://devshop.nimbbl.tech/api/") -> {
                        apiUrl = "https://devapi.nimbbl.tech/api/v2/"
                    }
                    BASE_URL.equals("https://uatshop.nimbbl.tech/api/") -> {
                        apiUrl = "https://uatapi.nimbbl.tech/api/v2/"
                    }
                    BASE_URL.equals("https://shoppp.nimbbl.tech/api/") -> {
                        apiUrl = "https://apipp.nimbbl.tech/api/v2/"
                    }
                    BASE_URL.equals("https://shop.nimbbl.tech/api/") -> {
                        apiUrl = "https://api.nimbbl.tech/api/v2/"
                    }
                }


                val body = GenerateTokenbody(
                    getString(R.string.access_key),
                    getString(R.string.secret_key)
                )
                val response = CatalogRepository().generateToken(
                    apiUrl + "generate-token",
                    body
                )
                if (response.isSuccessful) {
                    token = response.body()?.token.toString()
                    Log.i("SAN", "response.body().token-->" + (response.body()?.token ?: ""))
                    val inputInItPayload = AppPayloads.initResourcePayload(
                        response.body()?.token.toString(),
                        response.body()?.auth_principal?.sub_merchant_id.toString(),
                        getString(R.string.access_key),
                        application.packageName
                    )
                    NimbblPayCheckoutSDK.getInstance(this@MainActivity)?.initResource(
                        this@MainActivity,
                        inputInItPayload,
                        this@MainActivity
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        val list: List<CatalogModel> =
            listOf(
                CatalogModel(
                    "Colourful Mandalas.",
                    "₹ 2",
                    "Convert your dreary device into a bright happy place with this wallpaper by Speedy McVroom",
                    1
                ),
                CatalogModel(
                    "Designer Triangles.",
                    "₹ 4",
                    "Bold blue and deep black triangle designer wallpaper to give your device a hypnotic effect by  chenspec from Pixabay",
                    2
                )
            )
        setUpRecycelrvView(list)

        tv_settings.setOnClickListener {
            val intent = Intent(this, NimbblConfigActivity::class.java)
            startActivity(intent)
        }
    }

    fun setUpRecycelrvView(items: List<CatalogModel>) {
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


    override fun onPaymentFailed(data: String) {
        Toast.makeText(
            this,
            "Failed: $data",
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

    override fun onPaymentSuccess(data: MutableMap<String, Any>) {
        Log.d("Nimbbl demo", Integer.toString(data.size))
        //val payload: MutableMap<String, Any>? =
        //    p0.get("payload") as MutableMap<String, Any>?
        Toast.makeText(
            this,
            "OrderId=" + data.get("order_id") + ", Status=" + data.get("status"),
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this, OrderSucessPageAcitivty::class.java)
        intent.putExtra("orderid", data.get("order_id").toString())
        intent.putExtra("status", data.get("status").toString())
        startActivity(intent)

    }

    fun makePayment(orderId: String, subMerchantId: String) {
        val b = com.zl.nimbblpaycoresdk.models.NimbblCheckoutOptions.Builder()
        val preferences = getSharedPreferences("nimmbl_configs_prefs", MODE_PRIVATE)
        val baseUrl = preferences.getString("shop_base_url", BASE_URL).toString()
        var accessKey = "access_key_1MwvMkKkweorz0ry"

        when {
            baseUrl.equals("https://devshop.nimbbl.tech/api/") -> {
                accessKey = preferences.getString("access_key_dev", "access_key_1MwvMkKkweorz0ry")
                    .toString()
            }
            baseUrl.equals("https://uatshop.nimbbl.tech/api/") -> {
                accessKey = preferences.getString("access_key_uat", "access_key_1MwvMkKkweorz0ry")
                    .toString()
            }
            baseUrl.equals("https://shoppp.nimbbl.tech/api/") -> {
                accessKey =
                    preferences.getString("access_key_preprod", "access_key_1MwvMkKkweorz0ry")
                        .toString()
            }
            baseUrl.equals("https://shop.nimbbl.tech/api/") -> {
                accessKey = preferences.getString("access_key_prod", "access_key_1MwvMkKkweorz0ry")
                    .toString()
            }
        }

        val appMode = preferences.getString("sample_app_mode", "")
        if (appMode.equals("browser")) {
            val options = b.setKey(accessKey).setOrderId(orderId).build()
            NimbblCheckoutSDK.instance?.init(this)
            NimbblCheckoutSDK.instance?.checkout(options)
        } else {
            val options = b.setKey(accessKey).setOrderId(orderId).setToken(token).setSubMerchantId(subMerchantId).build()
            val intent = Intent(this, NimbblNativePaymentActivity::class.java)
            intent.putExtra("options", options)
            startActivity(intent)

        }

    }

    override fun onResourceLoaded(isLoaded: Boolean, message: String) {
        Log.d("Nimbbl", "isLoaded-->$isLoaded/message-->$message")

    }

    fun buyNow(id: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                var apiUrl = ""
                when {
                    BASE_URL.equals("https://devshop.nimbbl.tech/api/") -> {
                        apiUrl = "https://devapi.nimbbl.tech/api/v2/"
                    }
                    BASE_URL.equals("https://uatshop.nimbbl.tech/api/") -> {
                        apiUrl = "https://uatapi.nimbbl.tech/api/v2/"
                    }
                    BASE_URL.equals("https://shoppp.nimbbl.tech/api/") -> {
                        apiUrl = "https://apipp.nimbbl.tech/api/v2/"
                    }
                    BASE_URL.equals("https://shop.nimbbl.tech/api/") -> {
                        apiUrl = "https://api.nimbbl.tech/api/v2/"
                    }
                }
                val body = GenerateTokenbody(
                    getString(R.string.access_key),
                    getString(R.string.secret_key)
                )
                val tokenResponse = CatalogRepository().generateToken(
                    apiUrl + "generate-token",
                    body
                )
                if (tokenResponse.isSuccessful) {
                    token = tokenResponse.body()?.token.toString()
                    val response = CatalogRepository().CreateOrder(ServiceConstants.BASE_URL +"create-order",id,token.toString())
                    if (response.isSuccessful) {
                        Log.i("response",response.body()!!.order_id)
                        makePayment(response.body()!!.order_id,response.body()!!.sub_merchant_id.toString())
                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(this@MainActivity,"Unable to create order,",Toast.LENGTH_SHORT).show()
            }

        }
    }
}