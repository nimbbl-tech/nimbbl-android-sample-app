package com.example.nimbbl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimbbl.model.CatalogModel
import com.example.nimbbl.network.ApiCall.Companion.BASE_URL
import com.example.nimbbl.repository.CatalogRepository
import com.example.nimbbl.ui.NimbblConfigActivity
import com.example.nimbbl.ui.NimbblNativePaymentActivity
import com.example.nimbbl.ui.OrderSucessPageAcitivty
import com.example.nimbbl.ui.adapters.Catalog_Adapter
import com.example.nimbbl.utils.AppPayloads
import com.example.nimbbl.utils.AppPreferenceKeys.APP_PREFERENCE
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutBaseSDK
import com.zl.nimbblpaycoresdk.api.ServiceConstants
import com.zl.nimbblpaycoresdk.interfaces.NimbblCheckoutPaymentListener
import com.zl.nimbblpaycoresdk.interfaces.NimbblInItResourceListener
import com.zl.nimbblpaycoresdk.utils.PayloadKeys
import com.zl.nimbblpaycoresdk.utils.getAPIRequestBody
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import tech.nimbbl.checkout.sdk.NimbblCheckoutSDK

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity(), NimbblCheckoutPaymentListener,
    NimbblInItResourceListener {

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.isInitialised(this)
        val preferences = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
        val appMode = preferences.getString("sample_app_mode", "").toString()
        if (appMode.isEmpty()) {
            val intent = Intent(this, NimbblConfigActivity::class.java)
            startActivity(intent)
        } else {
            BASE_URL = preferences.getString("shop_base_url", BASE_URL).toString()
            val productId =  preferences.getString("app_product_id","5").toString()
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


                    val jsonObject = JSONObject()
                    jsonObject.put(PayloadKeys.key_product_id, productId)
                    val body: RequestBody = getAPIRequestBody(jsonObject)
                    val response = CatalogRepository().generateToken(
                        apiUrl + "generate-token",
                        body
                    )
                    if (response.isSuccessful) {
                        token = response.body()?.result?.token.toString()
                        Log.i("SAN", "response.body().token-->" + (response.body()?.result?.token ?: ""))
                        val inputInItPayload = AppPayloads.initResourcePayload(
                            response.body()?.result?.token.toString(),
                            response.body()?.result?.auth_principal?.sub_merchant_id.toString(),
                            application.packageName
                        )
                        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.initResource(
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

        }
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
        val preferences = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
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
            val options = b.setPackageName(accessKey).setOrderId(orderId).build()
            NimbblCheckoutSDK.instance?.init(this)
            NimbblCheckoutSDK.instance?.checkout(options)
        } else {
            val options = b.setPackageName(accessKey).setOrderId(orderId).setToken(token)
                .setSubMerchantId(subMerchantId).build()
            val intent = Intent(this, NimbblNativePaymentActivity::class.java)
            intent.putExtra("options", options)
            startActivity(intent)

        }

    }

    override fun onResourceLoaded(isLoaded: Boolean, message: String) {
        Log.d("Nimbbl", "isLoaded-->$isLoaded/message-->$message")

    }

    fun buyNow(id: Int) {
        showMobileNoEntryScreen(id)
    }

    private fun showMobileNoEntryScreen(id: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.lbl_enter_mobile_number))
        builder.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.mobile_no_dialog, null);
        builder.setView(view)

        val edtMobileNo = view.findViewById<EditText>(R.id.edt_mobileNo)

        builder.setPositiveButton("Continue") { _, _ ->
            val mobileNo = edtMobileNo.text.toString().trim()
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
                    val preferences = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
                    val productId =  preferences.getString("app_product_id","5").toString()
                    val jsonObject = JSONObject()
                    jsonObject.put(PayloadKeys.key_product_id, productId)
                    val body: RequestBody = getAPIRequestBody(jsonObject)
                    val tokenResponse = CatalogRepository().generateToken(
                        apiUrl + "generate-token",
                        body
                    )
                    if (tokenResponse.isSuccessful) {
                        token = tokenResponse.body()?.result?.token.toString()
                        val response = CatalogRepository().createOrder(
                            ServiceConstants.BASE_URL + "create-order",
                            id,
                            token.toString(),
                            mobileNo,
                            "skuAmount",
                            "skuDesc",
                            "userFirstName",
                            "userLastName",
                            "userEmailId",
                            mobileNo,
                            "useraddressLine1",
                            "userAddrStreet",
                            "userAddrLandmark",
                            "userAddrArea",
                            "userAddrCity",
                            "userAddrState",
                            "userAddrPin"
                        )
                        if (response.isSuccessful) {
                            Log.i("response", response.body()!!.order_id)
                            makePayment(
                                response.body()!!.order_id,
                                response.body()!!.sub_merchant_id.toString()
                            )
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Unable to create order,", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}