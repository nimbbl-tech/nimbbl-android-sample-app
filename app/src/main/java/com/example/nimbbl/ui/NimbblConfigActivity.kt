package com.example.nimbbl.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nimbbl.R
import com.example.nimbbl.network.ApiCall.Companion.BASE_URL
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutSDK
import kotlinx.android.synthetic.main.activity_nimbbl_config.*


class NimbblConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_nimbbl_config)
        val preferences = getSharedPreferences("nimmbl_configs_prefs", MODE_PRIVATE)
        NimbblPayCheckoutSDK.getInstance(this)?.setEnvironmentUrl("")
        val base_url = preferences.getString("shop_base_url", "")
        var accessKey = getString(R.string.access_key)
        when {
            base_url.equals("https://devshop.nimbbl.tech/api/") -> {
                radio_sandbox_group.check(R.id.radio_dev)
                tv_title_accesskey.text = " Update a dev environment access key"
                accessKey = preferences.getString("access_key_dev", getString(R.string.access_key))
                    .toString()
            }
            base_url.equals("https://uatshop.nimbbl.tech/api/") -> {
                radio_sandbox_group.check(R.id.radio_uat)
                tv_title_accesskey.text = " Update a uat environment access key"
                accessKey = preferences.getString("access_key_uat", getString(R.string.access_key))
                    .toString()
            }
            base_url.equals("https://shoppp.nimbbl.tech/api/") -> {
                radio_sandbox_group.check(R.id.radio_preprod)
                tv_title_accesskey.text = " Update a preprod environment access key"
                accessKey =
                    preferences.getString("access_key_preprod",getString(R.string.access_key))
                        .toString()
            }
            base_url.equals("https://shop.nimbbl.tech/api/") -> {
                radio_sandbox_group.check(R.id.radio_prod)
                tv_title_accesskey.text = " Update a prod environment access key"
                accessKey = preferences.getString("access_key_prod", getString(R.string.access_key))
                    .toString()
            }
        }
        edt_access_key.setText(accessKey)

        val sampleApp = preferences.getString("sample_app_mode", "browser")
        if (sampleApp.equals("browser")) {
            radio_sampleapp_group.check(R.id.radio_custom_browser)
        } else {
            radio_sampleapp_group.check(R.id.radio_native)
        }




        radio_sandbox_group.setOnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked = checkedRadioButton.isChecked
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                // Changes the textview's text to "Checked: example radiobutton text"
                if (checkedRadioButton.id == R.id.radio_dev) {
                    tv_title_accesskey.text = " Update a dev environment access key"
                    edt_access_key.setText(
                        preferences.getString(
                            "access_key_dev",
                            getString(R.string.access_key)
                        ).toString()
                    )
                } else if (checkedRadioButton.id == R.id.radio_uat) {
                    tv_title_accesskey.text = " Update a uat environment access key"
                    edt_access_key.setText(
                        preferences.getString(
                            "access_key_uat",
                            getString(R.string.access_key)
                        ).toString()
                    )
                } else if (checkedRadioButton.id == R.id.radio_preprod) {
                    tv_title_accesskey.text = " Update a preprod environment access key"
                    edt_access_key.setText(
                        preferences.getString(
                            "access_key_preprod",
                            getString(R.string.access_key)
                        ).toString()
                    )
                } else if (checkedRadioButton.id == R.id.radio_prod) {
                    tv_title_accesskey.text = " Update a prod environment access key"
                    edt_access_key.setText(
                        preferences.getString(
                            "access_key_prod",
                            getString(R.string.access_key)
                        ).toString()
                    )
                }
            }
        }

        btn_done.setOnClickListener {
            var baseUrl = "https://devshop.nimbbl.tech/api/"
            val editor: SharedPreferences.Editor = preferences.edit()
            when (radio_sandbox_group.checkedRadioButtonId) {
                R.id.radio_dev -> {
                    baseUrl = "https://devshop.nimbbl.tech/api/"
                    editor.putString("access_key_dev", edt_access_key.text.toString().trim())
                }
                R.id.radio_uat -> {
                    baseUrl = "https://uatshop.nimbbl.tech/api/"
                    editor.putString("access_key_uat", edt_access_key.text.toString().trim())
                }
                R.id.radio_preprod -> {
                    baseUrl = "https://shoppp.nimbbl.tech/api/"
                    editor.putString("access_key_preprod", edt_access_key.text.toString().trim())
                }
                R.id.radio_prod -> {
                    baseUrl = "https://shop.nimbbl.tech/api/"
                    editor.putString("access_key_prod", edt_access_key.text.toString().trim())
                }
            }
            when (radio_sampleapp_group.checkedRadioButtonId) {
                R.id.radio_native -> {
                    editor.putString("sample_app_mode", "native")

                }
                R.id.radio_custom_browser -> {
                    editor.putString("sample_app_mode", "browser")
                }
            }
            BASE_URL = baseUrl
            var apiUrl = ""
            when {
                baseUrl.equals("https://devshop.nimbbl.tech/api/") -> {
                    apiUrl = "https://devapi.nimbbl.tech/api/v2/"
                }
                baseUrl.equals("https://uatshop.nimbbl.tech/api/") -> {
                    apiUrl = "https://uatapi.nimbbl.tech/api/v2/"
                }
                baseUrl.equals("https://shoppp.nimbbl.tech/api/") -> {
                    apiUrl = "https://apipp.nimbbl.tech/api/v2/"
                }
                baseUrl.equals("https://shop.nimbbl.tech/api/") -> {
                    apiUrl = "https://api.nimbbl.tech/api/v2/"
                }
            }
            NimbblPayCheckoutSDK.getInstance(this)?.setEnvironmentUrl(apiUrl)

            editor.putString("shop_base_url", baseUrl)
            val isSuccess = editor.commit()

            if (isSuccess) {

                Toast.makeText(this, "Environment selected successfully !", Toast.LENGTH_SHORT)
                    .show()
                onBackPressed()
            }

        }
    }
}