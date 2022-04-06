package com.example.nimbbl.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.nimbbl.R
import com.example.nimbbl.network.ApiCall.Companion.BASE_URL
import com.example.nimbbl.repository.CatalogRepository
import com.example.nimbbl.utils.AppPayloads
import com.example.nimbbl.utils.AppPreferenceKeys.APP_PREFERENCE
import com.example.nimbbl.utils.AppPreferenceKeys.APP_TEST_MERCHANT
import com.example.nimbbl.utils.AppPreferenceKeys.SAMPLE_APP_MODE
import com.example.nimbbl.utils.AppPreferenceKeys.SHOP_BASE_URL
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutBaseSDK
import com.zl.nimbblpaycoresdk.api.ServiceConstants
import com.zl.nimbblpaycoresdk.interfaces.NimbblCheckoutPaymentListener
import com.zl.nimbblpaycoresdk.interfaces.NimbblInItResourceListener
import com.zl.nimbblpaycoresdk.utils.PayloadKeys
import com.zl.nimbblpaycoresdk.utils.getAPIRequestBody
import kotlinx.android.synthetic.main.activity_main.tv_settings
import kotlinx.android.synthetic.main.activity_order_create.*
import kotlinx.android.synthetic.main.activity_order_create.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import tech.nimbbl.checkout.sdk.NimbblCheckoutSDK

class OrderCreateActivity : AppCompatActivity(),
    NimbblCheckoutPaymentListener, NimbblInItResourceListener {

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_create)
        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.isInitialised(this)
        initialisation()
        setListners()
    }


    private fun initialisation() {
        val preferences = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
        val appMode = preferences.getString(SAMPLE_APP_MODE, "").toString()
        if (appMode.isEmpty()) {
            val intent = Intent(this, NimbblConfigActivity::class.java)
            resultLauncher.launch(intent)
        } else {
            BASE_URL = preferences.getString(SHOP_BASE_URL, BASE_URL).toString()
            val testMerchant =  preferences.getString(APP_TEST_MERCHANT,getString(R.string.value_native_config)).toString()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    var apiUrl = BASE_URL
                    var productId =5
                    when {
                        BASE_URL.equals("https://devshop.nimbbl.tech/api/") -> {
                            apiUrl = "https://deverp.nimbbl.tech/api/"
                        }
                        BASE_URL.equals("https://uatshop.nimbbl.tech/api/") -> {
                            apiUrl = "https://uaterp.nimbbl.tech/api/"
                        }
                        BASE_URL.equals("https://shoppp.nimbbl.tech/api/") -> {
                            apiUrl = "https://erppp.nimbbl.tech/api/"
                        }
                        BASE_URL.equals("https://shop.nimbbl.tech/api/") -> {
                            apiUrl = "https://erp.nimbbl.tech/api/"
                        }
                    }
                    when{
                        testMerchant.equals(getString(R.string.value_native_config),true) ->{
                            productId = 5
                        }
                        testMerchant.equals(getString(R.string.value_razorpay_config),true) ->{
                            productId = 1
                        }
                        testMerchant.equals(getString(R.string.value_payu_config),true) ->{
                            productId = 2
                        }
                        testMerchant.equals(getString(R.string.value_cash_free_config),true) ->{
                            productId = 3
                        }
                    }
                    val jsonObject = JSONObject()
                    jsonObject.put(PayloadKeys.key_product_id, productId)
                    val body: RequestBody = getAPIRequestBody(jsonObject)
                    val response = CatalogRepository().generateToken(
                        apiUrl + "generate-demo-token",
                        body
                    )
                    if (response.isSuccessful) {
                        token = response.body()?.result?.token.toString()
                        Log.i("SAN", "response.body().token-->" + (response.body()?.result?.token ?: ""))
                        Log.i("SAN", "response.body().auth_principal?.skip_device_verification-->" + (response.body()?.result?.auth_principal?.skip_device_verification ?: ""))
                        val inputInItPayload = AppPayloads.initResourcePayload(
                            response.body()?.result?.token.toString(),
                            response.body()?.result?.auth_principal?.sub_merchant_id.toString(),
                            application.packageName
                        )
                        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.initResource(
                            this@OrderCreateActivity,
                            inputInItPayload,
                            this@OrderCreateActivity
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }
        tv_settings.setOnClickListener {
            val intent = Intent(this, NimbblConfigActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun setListners() {
        btn_buy_now.setOnClickListener {
            val skuTitle = txt_title.text.toString().ifEmpty { "sku1" }
            val skuAmount = txt_amount.text.toString().ifEmpty { "2" }
            val skuDesc = txt_dscription.txt_dscription.text.toString().ifEmpty { "Convert your dreary device into a bright happy place with this wallpaper by Speedy McVroom" }
            val userFirstName =  txt_user_first_name.text.toString().ifEmpty { "Honey" }
            val userLastName =  txt_user_last_name.text.toString().ifEmpty { "Singh" }
            val userEmailId =  txt_user_email_id.text.toString().ifEmpty { "honey@gmail.com" }
            val userMobileNumber =  txt_user_mobile_number.text.toString().ifEmpty { "1234567890" }
            val useraddressLine1 =  txt_user_address_line_1.text.toString().ifEmpty { "My address" }
            val userAddrStreet =  txt_street.text.toString().ifEmpty { "My street" }
            val userAddrLandmark =  txt_landmark.text.toString().ifEmpty { "My landmark" }
            val userAddrArea =  txt_area.text.toString().ifEmpty { "My area" }
            val userAddrCity =  txt_city.text.toString().ifEmpty { "My area" }
            val userAddrState =  txt_state.text.toString().ifEmpty { "My area" }
            val userAddrPin =  txt_pin_code.text.toString().ifEmpty { "1234567" }

            if(txt_user_mobile_number.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter mobile number to proceed", Toast.LENGTH_SHORT)
                    .show()
            }else{
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        var apiUrl = BASE_URL
                        val preferences = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
                        val testMerchant =  preferences.getString(APP_TEST_MERCHANT,getString(R.string.value_native_config)).toString()
                        var productId =5
                        when {
                            BASE_URL.equals("https://devshop.nimbbl.tech/api/") -> {
                                apiUrl = "https://deverp.nimbbl.tech/api/"
                            }
                            BASE_URL.equals("https://uatshop.nimbbl.tech/api/") -> {
                                apiUrl = "https://uaterp.nimbbl.tech/api/"
                            }
                            BASE_URL.equals("https://shoppp.nimbbl.tech/api/") -> {
                                apiUrl = "https://erppp.nimbbl.tech/api/"
                            }
                            BASE_URL.equals("https://shop.nimbbl.tech/api/") -> {
                                apiUrl = "https://erp.nimbbl.tech/api/"
                            }
                        }
                        when{
                            testMerchant.equals(getString(R.string.value_native_config),true) ->{
                                productId = 5
                            }
                            testMerchant.equals(getString(R.string.value_razorpay_config),true) ->{
                                productId = 1
                            }
                            testMerchant.equals(getString(R.string.value_payu_config),true) ->{
                                productId = 2
                            }
                            testMerchant.equals(getString(R.string.value_cash_free_config),true) ->{
                                productId = 3
                            }
                        }

                        val jsonObject = JSONObject()
                        jsonObject.put(PayloadKeys.key_product_id, productId.toInt())
                        val body: RequestBody = getAPIRequestBody(jsonObject)
                        val tokenResponse = CatalogRepository().generateToken(
                            apiUrl + "generate-demo-token",
                            body
                        )
                        if (tokenResponse.isSuccessful) {
                            token = tokenResponse.body()?.result?.token.toString()
                            val response = CatalogRepository().createOrder(
                                ServiceConstants.BASE_URL + "create-order",
                                productId,
                                token.toString(),
                                skuTitle,
                                skuAmount,
                                skuDesc,
                                userFirstName,
                                userLastName,
                                userEmailId,
                                userMobileNumber,
                                useraddressLine1,
                                userAddrStreet,
                                userAddrLandmark,
                                userAddrArea,
                                userAddrCity,
                                userAddrState,
                                userAddrPin
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
                        Toast.makeText(this@OrderCreateActivity, "Unable to create order,", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        }
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("SAN", "resultCode-->"+result.resultCode+"data-->"+result.data)
      initialisation()
    }


    private fun makePayment(orderId: String, subMerchantId: String) {
        val b = com.zl.nimbblpaycoresdk.models.NimbblCheckoutOptions.Builder()
        val preferences = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)


        val appMode = preferences.getString(SAMPLE_APP_MODE, "")
        if (appMode.equals(getString(R.string.value_webview))) {
            val options = b.setToken(token).setPackageName(application.packageName).setOrderId(orderId).build()
            NimbblCheckoutSDK.instance?.init(this)
            NimbblCheckoutSDK.instance?.checkout(options)
        } else {
            val options = b.setPackageName(application.packageName).setOrderId(orderId).setToken(token)
                .setSubMerchantId(subMerchantId).build()
            val intent = Intent(this, NimbblNativePaymentActivity::class.java)
            intent.putExtra("options", options)
            startActivity(intent)

        }

    }


    override fun onResourceLoaded(isLoaded: Boolean, message: String) {

    }
    override fun onPaymentFailed(data: String) {
        Toast.makeText(
            this,
            "Failed: $data",
            Toast.LENGTH_LONG
        ).show()
    }


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
}