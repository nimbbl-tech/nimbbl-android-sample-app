package com.example.nimbbl.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimbbl.R
import com.example.nimbbl.network.ApiCall.Companion.BASE_URL
import com.example.nimbbl.utils.AppPreferenceKeys.APP_PREFERENCE
import com.example.nimbbl.utils.AppPreferenceKeys.APP_TEST_MERCHANT
import com.example.nimbbl.utils.AppPreferenceKeys.SAMPLE_APP_MODE
import com.example.nimbbl.utils.AppPreferenceKeys.SHOP_BASE_URL
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutBaseSDK
import kotlinx.android.synthetic.main.activity_nimbbl_config.*


class NimbblConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_nimbbl_config)
        val preferences = getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)
        val baseUrl = preferences.getString(SHOP_BASE_URL, "")
        when {
            baseUrl.equals("https://devshop.nimbbl.tech/api/") -> {
                spn_environments.setSelection(3)
            }
            baseUrl.equals("https://uatshop.nimbbl.tech/api/") -> {
                spn_environments.setSelection(2)
            }
            baseUrl.equals("https://shoppp.nimbbl.tech/api/") -> {
                spn_environments.setSelection(1)
            }
            baseUrl.equals("https://shop.nimbbl.tech/api/") -> {
                spn_environments.setSelection(0)
            }
        }

        val sampleApp = preferences.getString(SAMPLE_APP_MODE, getString(R.string.value_native))
        if (sampleApp.equals(getString(R.string.value_native))) {
            spn_app_experience.setSelection(0)
        } else {
            spn_app_experience.setSelection(1)
        }


        val testMerchant = preferences.getString(APP_TEST_MERCHANT, getString(R.string.value_native_config))
        when {
            testMerchant.equals(getString(R.string.value_native_config)) -> {
                spn_test_merchant.setSelection(0)
            }
            testMerchant.equals(getString(R.string.value_razorpay_config)) -> {
                spn_test_merchant.setSelection(1)
            }
            testMerchant.equals(getString(R.string.value_payu_config)) -> {
                spn_test_merchant.setSelection(2)
            }
            testMerchant.equals(getString(R.string.value_cash_free_config)) -> {
                spn_test_merchant.setSelection(3)
            }
        }

        btn_done.setOnClickListener {
            var tempBaseUrl = "https://shop.nimbbl.tech/api/"
            val editor: SharedPreferences.Editor = preferences.edit()
            when (spn_environments.selectedItem.toString()) {
                getString(R.string.value_dev) -> {
                    tempBaseUrl = "https://devshop.nimbbl.tech/api/"
                }
                getString(R.string.value_uat) -> {
                    tempBaseUrl = "https://uatshop.nimbbl.tech/api/"
                }
                getString(R.string.value_pp)-> {
                    tempBaseUrl = "https://shoppp.nimbbl.tech/api/"
                }
                getString(R.string.value_prod) -> {
                    tempBaseUrl = "https://shop.nimbbl.tech/api/"
                }
            }

            BASE_URL = tempBaseUrl
            var apiUrl = ""
            when {
                tempBaseUrl.equals("https://devshop.nimbbl.tech/api/") -> {
                    apiUrl = "https://devapi.nimbbl.tech/api/v2/"
                }
                tempBaseUrl.equals("https://uatshop.nimbbl.tech/api/") -> {
                    apiUrl = "https://uatapi.nimbbl.tech/api/v2/"
                }
                tempBaseUrl.equals("https://shoppp.nimbbl.tech/api/") -> {
                    apiUrl = "https://apipp.nimbbl.tech/api/v2/"
                }
                tempBaseUrl.equals("https://shop.nimbbl.tech/api/") -> {
                    apiUrl = "https://api.nimbbl.tech/api/v2/"
                }
            }

            editor.putString(SAMPLE_APP_MODE, spn_app_experience.selectedItem.toString())
            editor.putString(APP_TEST_MERCHANT, spn_test_merchant.selectedItem.toString())
            editor.putString(SHOP_BASE_URL, tempBaseUrl)
            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.setEnvironmentUrl(apiUrl)

            val isSuccess = editor.commit()

            if (isSuccess) {

                Toast.makeText(this, "Environment selected successfully !", Toast.LENGTH_SHORT)
                    .show()
               onBackPressed()
            }

        }
    }
}