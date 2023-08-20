package com.example.nimbbl.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nimbbl.ui.adapters.BankSpinAdapter
import com.example.nimbbl.ui.adapters.FastPaymentModeAdapter
import com.example.nimbbl.ui.adapters.ListOfWalletAdapter
import com.example.nimbbl.ui.adapters.ListOfbankAdapter
import com.example.nimbbl.utils.AppPayloads
import com.example.nimbbl.utils.displayToast
import com.example.nimbbl.utils.hidePhoneNum
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutBaseSDK
import com.zl.nimbblpaycoresdk.interfaces.NimbblPayNativeCheckoutPaymentListener
import com.zl.nimbblpaycoresdk.models.Data
import com.zl.nimbblpaycoresdk.models.Item
import com.zl.nimbblpaycoresdk.models.NimbblCheckoutOptions
import com.zl.nimbblpaycoresdk.utils.PayloadKeys
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_completePayment
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_getBinData
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_initiateOrder
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_initiatePayment
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_paymentEnquiry
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_paymentModes
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_resolveUser
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_validateCard
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.event_display_loader
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.event_exception_occured
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.event_hide_loader
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.event_process_result
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_OrderID
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_action
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_errorCode
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_errorMessage
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_event
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_message
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_mobileNumber
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_next_step
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_nimbblPayload
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_paymentModes
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_payment_mode
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_signature
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_status
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_sub_payment_name
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_transaction_id
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_user_name
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_vpa_account_holder
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_vpa_id
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_vpa_valid
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_card
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_icici_paylater
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_lazypay
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_netbanking
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_olamoney
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_upi
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_wallet
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_status_failed
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_status_success
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_type_otp
import com.zl.nimbblpaycoresdk.utils.printLog
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject
import tech.nimbbl.checkout.sdk.RestApiUtils
import tech.nimbbl.example.R
import tech.nimbbl.example.databinding.ActivityNimbblConfigBinding
import tech.nimbbl.example.databinding.ActivityOrderCreateBinding
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class NimbblNativePaymentMethodsActivity : AppCompatActivity(),
    NimbblPayNativeCheckoutPaymentListener {
    private var paymentView: View? = null
    private var upiPollingDialog: AlertDialog? = null
    private var displayCountDownTimer: CountDownTimer? = null
    private var pollingCountDownTimer: CountDownTimer? = null
    private val tagOption = "options"
    private lateinit var options: NimbblCheckoutOptions
    private lateinit var cardCategory: String
    private var paymentDialog: AlertDialog? = null

    var cv_card_container: CardView? = null
    var cv_netbanking_container: CardView? = null
    var cv_upi_container: CardView? = null
    var cv_wallet_container: CardView? = null
    var progressBarHolder: FrameLayout? = null
    var rv_fast_payment_methods: RecyclerView? = null
    var tv_header_Fast: TextView? = null
    var tv_header_others: TextView? = null
    var inc_layout_others: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        setContentView(R.layout.activity_nimbbl_native_payment_methods)
        val tv_amount = findViewById<TextView>(R.id.tv_amount)
         cv_card_container = findViewById(R.id.cv_card_container)
        cv_netbanking_container = findViewById(R.id.cv_netbanking_container)
        cv_upi_container = findViewById(R.id.cv_upi_container)
        cv_wallet_container = findViewById(R.id.cv_wallet_container)
        progressBarHolder = findViewById(R.id.progressBarHolder)
        rv_fast_payment_methods = findViewById(R.id.rv_fast_payment_methods)
        tv_header_Fast = findViewById(R.id.tv_header_Fast)
        tv_header_others = findViewById(R.id.tv_header_others)
        inc_layout_others = findViewById(R.id.inc_layout_others)
       // NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.initialisedNimbblSDK(this)
        if (intent.hasExtra(tagOption)) {
            options = intent.getParcelableExtra(tagOption)!!
            tv_amount.text = "₹ ${options.amount.toDouble()}"
            val tokenKey = options.packageName
            if (TextUtils.isEmpty(tokenKey)) {
                displayToast(getString(tech.nimbbl.checkout.sdk.R.string.key_not_set))
            } else {
                if (!RestApiUtils.isNetConnected(this)) {
                    displayToast(getString(com.zl.nimmblecoresdk.R.string.no_internet))
                }
            }
        } else {
            displayToast(getString(tech.nimbbl.checkout.sdk.R.string.input_sent_invalid))
        }
        try {
            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.registerCallback(this,this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val resolveUserPayload = AppPayloads.resolveUserPayload(
            options.token.toString(),
            options.orderId.toString()
        )
        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.process(resolveUserPayload)
        setListeners()

    }

    private fun setListeners() {

        cv_card_container?.setOnClickListener {
            openPaymentDialog(value_payment_mode_card)
        }

        cv_netbanking_container?.setOnClickListener {
            openPaymentDialog(value_payment_mode_netbanking)
        }

        cv_upi_container?.setOnClickListener {
            openPaymentDialog(value_payment_mode_upi)
        }

        cv_wallet_container?.setOnClickListener {
            openPaymentDialog(value_payment_mode_wallet)
        }
    }

    fun upiInitiatePayment(paymentMode: String, upiId: String, drawable: Drawable) {
        setPaymentDialogInitialSetting(paymentMode, drawable)
        val btnPayment = paymentView?.findViewById<Button>(R.id.btn_pd_pay)
        btnPayment?.text = "Pay ₹ ${options.amount.toDouble()}"
        btnPayment?.setOnClickListener {
            val initiatePaymentPayload = AppPayloads.upiInitiatePaymentRequestPayload(
                upiId,
                options.token.toString(),
                options.orderId.toString()
            )
            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                ?.process(initiatePaymentPayload)
        }


    }

    override fun onEventReceived(data: JSONObject) {
        printLog(this,"SAN", "event-->" + data.getString(key_event))
        when (data.getString(key_event)) {

            event_display_loader -> {
                progressBarHolder?.visibility = View.VISIBLE

                if(paymentView != null) {
                    paymentView?.findViewById<ProgressBar>(R.id.pd_progressbar)?.visibility = View.VISIBLE
                }
            }
            event_hide_loader -> {
                progressBarHolder?.visibility = View.GONE
                if(paymentView != null) {
                    paymentView?.findViewById<ProgressBar>(R.id.pd_progressbar)?.visibility = View.GONE
                }

            }
            event_exception_occured -> {
                val errorCode = data.getJSONObject(key_nimbblPayload).getString(key_errorCode)
                val errorMessage = data.getJSONObject(key_nimbblPayload).getString(key_errorMessage)
                //printLog("SAN", "errorCode-->$errorCode/errormessage-->$errorMessage")
                displayToast("$errorCode : $errorMessage")
            }

            event_process_result -> {
                when (data.getJSONObject(key_nimbblPayload).getString(key_action)) {
                    action_resolveUser -> {
                        val nextStep =
                            data.getJSONObject(key_nimbblPayload).getString(key_next_step)
                        val userName =
                            data.getJSONObject(key_nimbblPayload).getString(key_user_name)
                        val mobileNo =
                            data.getJSONObject(key_nimbblPayload).getString(key_mobileNumber)
                        findViewById<TextView>(R.id.tv_mob_number).text = hidePhoneNum(mobileNo)
                        val strUserName = "Hello $userName,"
                        findViewById<TextView>(R.id.tv_user_name).text = strUserName.split(' ').joinToString(" ") { it ->
                            it.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                        }
                        if (nextStep.equals(PayloadKeys.value_step_payment_mode)) {

                            val initiateOrderPayload = AppPayloads.initialiseOrderPayload(
                                options.token.toString(),
                                options.orderId.toString()
                            )
                            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                                ?.process(initiateOrderPayload)
                        }
                    }
                    action_initiateOrder -> {
                        val paymentModePayload = AppPayloads.paymentModesPayload(
                            options.token.toString(),
                            options.orderId.toString()
                        )
                        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                            ?.process(paymentModePayload)
                    }
                    action_paymentModes -> {
                        printLog(this,
                            "SAN",
                            "paymentMode-->" + data.getJSONObject(key_nimbblPayload)
                                .getJSONArray(key_paymentModes)
                        )
                        val paymentDialogBuilder = AlertDialog.Builder(this,android.R.style.Theme_Light_NoTitleBar_Fullscreen)
                        paymentDialog = paymentDialogBuilder.create()
                        paymentDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        paymentDialog!!.setCancelable(false)
                        paymentView =
                            layoutInflater.inflate(R.layout.dialog_layout_common_payment, null)
                        paymentDialog!!.setView(paymentView)

                        val gson = Gson()
                        val listType = object : TypeToken<List<Data>>() {}.type
                        val paymentList: List<Data> = gson.fromJson(
                            data.getJSONObject(key_nimbblPayload).getJSONArray(
                                key_paymentModes
                            ).toString(), listType
                        )
                        paymentList[0].items?.let { setUpRecyclerView(it) }
                        for (i in 0 until (paymentList[1].items?.size ?: 0)) {
                            val item = paymentList[1].items?.get(i)
                            if (item?.payment_mode_code.equals("net_banking")) {
                                item?.itemCol?.let {
                                    setUpListOfBanks(
                                        item.payment_mode!!,
                                        it
                                    )
                                }
                            } else if (item?.payment_mode_code.equals("wallet", true)) {
                                setUpListOfWallet(item?.itemCol)
                            }

                        }
                    }
                    action_getBinData -> {
                        cardCategory = data.getJSONObject(key_nimbblPayload).getString(key_sub_payment_name)
                    }
                    action_validateCard -> {
                        val initiatePaymentCardPayload = AppPayloads.initiateCardPaymentPayload(
                            paymentView?.findViewById<AppCompatEditText>(R.id.edt_card_number)?.text.toString().trim(),
                            paymentView?.findViewById<AppCompatEditText>(R.id.edt_expiry)?.text.toString().trim(),
                            paymentView?.findViewById<AppCompatEditText>(R.id.edt_cvv)?.text.toString().trim(),
                            paymentView?.findViewById<AppCompatEditText>(R.id.edt_card_holder_name)?.text.toString().trim(),
                            "card",
                            cardCategory,
                            options.token.toString(),
                            options.orderId.toString()
                        )
                        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                            ?.process(initiatePaymentCardPayload)

                    }
                    action_initiatePayment -> {
                        printLog(this,"SAN", "action_initiatePayment")
                        val paymentMode =
                            data.getJSONObject(key_nimbblPayload).getString(key_payment_mode)
                        if (paymentMode.equals(value_payment_mode_lazypay) ||
                            paymentMode.equals(value_payment_mode_icici_paylater) ||
                            paymentMode.equals(value_payment_mode_olamoney)
                        ) {
                            val transactionId = data.getJSONObject(key_nimbblPayload).getString(key_transaction_id)
                            val drawable = resources.getDrawable(R.drawable.ic_wallet)
                            payLaterCompletePayment(paymentMode,transactionId,true,drawable)
                        } else if (paymentMode.equals(value_payment_mode_upi)) {
                            val isVpaValid = data.getJSONObject(key_nimbblPayload).getInt(key_vpa_valid)
                            if(paymentView?.findViewById<RelativeLayout>(R.id.upi_detail_container)?.isVisible == true){
                                if(isVpaValid ==1) {
                                    val vpaId= data.getJSONObject(key_nimbblPayload).getString(key_vpa_id)
                                    val vpaAccountHolder= data.getJSONObject(key_nimbblPayload).getString(key_vpa_account_holder)
                                    paymentView?.findViewById<TextView>(R.id.tv_user_name)!!.visibility = View.VISIBLE
                                    paymentView?.findViewById<TextView>(R.id.tv_user_name)!!.text  ="Name: $vpaAccountHolder"
                                    paymentView?.findViewById<TextView>(R.id.tv_upi_id)!!.visibility = View.VISIBLE
                                    paymentView?.findViewById<TextView>(R.id.tv_upi_id)!!.text ="UPI ID: $vpaId"
                                    paymentView?.findViewById<TextView>(R.id.tv_upi_message)!!.visibility = View.VISIBLE
                                    paymentView?.findViewById<AppCompatButton>(R.id.btn_pd_pay)!!.text = "Pay ₹ ${options.amount.toDouble()}"
                                }else {
                                    displayToast("Please enter valid upi id.")
                                }
                            }else{
                                if(isVpaValid ==1) {
                                    val completePaymentRequestPayload =
                                        AppPayloads.getUpiCompletePaymentRequestPayload(
                                            options.token.toString(),
                                            options.orderId.toString(),
                                            data.getJSONObject(key_nimbblPayload).getString(key_vpa_id)
                                        )
                                    NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                                        ?.process(completePaymentRequestPayload)
                                }else{
                                    displayToast("UPI id is invalid. Please contact support.")
                                }
                            }

                        }
                    }
                    action_completePayment -> {
                        val transactionId =
                            data.getJSONObject(key_nimbblPayload).getString(key_transaction_id)
                        val paymentMode =
                            data.getJSONObject(key_nimbblPayload).getString(key_payment_mode)

                        if (paymentMode.equals(value_payment_mode_upi)) {
                            openUPIPollingDialog(transactionId, paymentMode)

                        } else {
                            val paymentEnqRequestPayload =
                                AppPayloads.getTransactionEnquiryRequestPayload(
                                    options.token.toString(),
                                    options.orderId.toString(),
                                    paymentMode,
                                    transactionId
                                )
                            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                                ?.process(paymentEnqRequestPayload)
                        }

                    }
                    action_paymentEnquiry -> {
                        val orderId = data.getJSONObject(key_nimbblPayload).getString(key_OrderID)
                        val transactionId =
                            data.getJSONObject(key_nimbblPayload).getString(key_transaction_id)
                        val signature =
                            data.getJSONObject(key_nimbblPayload).getString(key_signature)
                        val message = data.getJSONObject(key_nimbblPayload).getString(key_message)
                        val status = data.getJSONObject(key_nimbblPayload).getString(key_status)
                        val paymentMode = data.getJSONObject(key_nimbblPayload).getString(
                            key_payment_mode
                        )
                        Log.d(
                            "SAN",
                            "orderId-->$orderId/transactionId-->$transactionId/signature-->$signature/message-->$message/status-->$status"
                        )

                        if (paymentMode.equals(value_payment_mode_upi)) {
                            if (status.equals(value_payment_status_success) || status.equals(
                                    value_payment_status_failed
                                )
                            ) {
                                if (upiPollingDialog != null && upiPollingDialog!!.isShowing) {
                                    upiPollingDialog!!.dismiss()
                                    upiPollingDialog = null
                                    displayCountDownTimer?.cancel()
                                    displayCountDownTimer = null
                                    pollingCountDownTimer?.cancel()
                                    pollingCountDownTimer = null
                                }
                                openTransactionEnquiryDialog(
                                    paymentMode,
                                    orderId,
                                    transactionId,
                                    signature,
                                    message,
                                    status
                                )
                            } else {
                                if (upiPollingDialog == null) {
                                    openTransactionEnquiryDialog(
                                        paymentMode,
                                        orderId,
                                        transactionId,
                                        signature,
                                        message,
                                        status
                                    )
                                }
                            }
                        } else {
                            openTransactionEnquiryDialog(
                                paymentMode,
                                orderId,
                                transactionId,
                                signature,
                                message,
                                status
                            )
                        }
                        printLog(this,"SAN", "Enquiry")
                    }
                }

            }
        }
    }

    private fun openUPIPollingDialog(transactionId: String, paymentMode: String) {

        val upiPollingDialogBuilder = AlertDialog.Builder(this)

        upiPollingDialog = upiPollingDialogBuilder.create()
        upiPollingDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        upiPollingDialog!!.setCancelable(false)
        //upiPollingDialog!!.setTitle("Pay with UPI")
        val view = layoutInflater.inflate(R.layout.upi_polling_dialog, null)
        upiPollingDialog!!.setView(view)
        val tvProgress = view.findViewById<TextView>(R.id.tv_progressText)
        val ciProgress = view.findViewById<CircularProgressIndicator>(R.id.ci_progress_indicator)
        val ivClose = view.findViewById<ImageView>(R.id.iv_close)
        upiPollingDialog!!.show()
        val totalTime = 4 * 60 * 1000
        displayCountDownTimer = object : CountDownTimer(totalTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val strTime = String.format(
                    "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )
                tvProgress.text = strTime
                val timeElapsed = totalTime - millisUntilFinished
                val percentage =
                    ((timeElapsed.toDouble() / totalTime.toDouble()) * 100).roundToInt()
                Log.d(
                    "SAN",
                    "millisUntilFinished-->$millisUntilFinished/totalTime-->$totalTime/percentage-->$percentage"
                )
                ciProgress.progress = percentage

                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {

            }
        }.start()

        pollingCountDownTimer = object : CountDownTimer(totalTime.toLong(), 4000) {
            override fun onTick(millisUntilFinished: Long) {
                val paymentEnqRequestPayload =
                    AppPayloads.getTransactionEnquiryRequestPayload(
                        options.token.toString(),
                        options.orderId.toString(),
                        paymentMode,
                        transactionId
                    )
                NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                    ?.process(paymentEnqRequestPayload)

            }

            override fun onFinish() {
                if (upiPollingDialog != null && upiPollingDialog!!.isShowing) {
                    upiPollingDialog!!.dismiss()
                    upiPollingDialog = null
                    displayCountDownTimer?.cancel()
                    displayCountDownTimer = null
                    pollingCountDownTimer?.cancel()
                    pollingCountDownTimer = null

                    val paymentEnqRequestPayload =
                        AppPayloads.getTransactionEnquiryRequestPayload(
                            options.token.toString(),
                            options.orderId.toString(),
                            paymentMode,
                            transactionId
                        )
                    NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                        ?.process(paymentEnqRequestPayload)
                }
            }
        }.start()

        ivClose.setOnClickListener {
            if (upiPollingDialog != null && upiPollingDialog!!.isShowing) {
                upiPollingDialog!!.dismiss()
                upiPollingDialog = null
                displayCountDownTimer?.cancel()
                displayCountDownTimer = null
                pollingCountDownTimer?.cancel()
                pollingCountDownTimer = null

                val paymentEnqRequestPayload =
                    AppPayloads.getTransactionEnquiryRequestPayload(
                        options.token.toString(),
                        options.orderId.toString(),
                        paymentMode,
                        transactionId
                    )
                NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                    ?.process(paymentEnqRequestPayload)
            }
        }
    }

    private fun openTransactionEnquiryDialog(
        paymentMode: String,
        orderId: String,
        transactionId: String,
        signature: String,
        message: String,
        status: String
    ) {
        setPaymentDialogInitialSetting(getString(R.string.lbl_transaction_enquiry),resources.getDrawable(R.drawable.ic_baseline_info_24))
        val paymentEnquiryContainer =  paymentView?.findViewById<LinearLayout>(R.id.paymentEnquiry_container)
        paymentEnquiryContainer?.visibility =  View.VISIBLE
        val tvPdTextView = paymentView?.findViewById<TextView>(R.id.tv_pd_title)
        val btnClose = paymentView?.findViewById<Button>(R.id.btn_pd_cancel)
        btnClose?.visibility = View.GONE
        tvPdTextView?.text = getString(R.string.lbl_transaction_enquiry)
        if (orderId.isNotEmpty()) {
            paymentView?.findViewById<TextView>(R.id.tv_val_order_id)?.text = orderId
        } else {
            paymentView?.findViewById<LinearLayout>(R.id.ll_order_id_container)?.visibility = View.GONE
        }

        if (transactionId.isNotEmpty()) {
            paymentView?.findViewById<TextView>(R.id.tv_val_transaction_id)?.text = transactionId
        } else {
            paymentView?.findViewById<LinearLayout>(R.id.ll_transaction_id_container)?.visibility = View.GONE
        }
        if (signature.isNotEmpty()) {
            paymentView?.findViewById<TextView>(R.id.tv_val_signature)?.text = signature
        } else {
            paymentView?.findViewById<LinearLayout>(R.id.ll_signature_container)?.visibility = View.GONE
        }

        if (status.isNotEmpty()) {
            paymentView?.findViewById<TextView>(R.id.tv_val_status)?.text = status
        } else {
            paymentView?.findViewById<LinearLayout>(R.id.ll_status_container)?.visibility = View.GONE
        }
        if (message.isNotEmpty()) {
            paymentView?.findViewById<TextView>(R.id.tv_val_message)?.text = message
        } else {
            paymentView?.findViewById<LinearLayout>(R.id.ll_message_container)?.visibility = View.GONE
        }

        val btnPayment = paymentView?.findViewById<Button>(R.id.btn_pd_pay)
        btnPayment?.visibility = View.VISIBLE
        btnPayment?.text = getString(R.string.lbl_done)
        btnPayment?.setOnClickListener {
            if (paymentDialog != null && paymentDialog!!.isShowing) {
                NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.terminate()
                paymentDialog!!.dismiss()
                paymentDialog = null
            }
            this.finish()
        }
        if (!isFinishing) {
            paymentDialog?.show()
        }
    }


    private fun setPaymentDialogInitialSetting(paymentMode: String, drawable: Drawable) {
        val ivPdTitle = paymentView?.findViewById<ImageView>(R.id.iv_pd_title)
        val tvPdTextView = paymentView?.findViewById<TextView>(R.id.tv_pd_title)
        val cardDetailContainer =
            paymentView?.findViewById<RelativeLayout>(R.id.card_detail_container)
        val bankDetailContainer = paymentView?.findViewById<RelativeLayout>(R.id.netBanking_detail_container)
        val upiDetailContainer = paymentView?.findViewById<RelativeLayout>(R.id.upi_detail_container)
        val walletDetailContainer = paymentView?.findViewById<RelativeLayout>(R.id.wallet_detail_container)
        val payLaterOtpContainer =  paymentView?.findViewById<LinearLayout>(R.id.paylater_otp_container)
        val paymentEnquiryContainer =  paymentView?.findViewById<LinearLayout>(R.id.paymentEnquiry_container)

        cardDetailContainer?.visibility = View.GONE
        bankDetailContainer?.visibility = View.GONE
        upiDetailContainer?.visibility = View.GONE
        walletDetailContainer?.visibility = View.GONE
        payLaterOtpContainer?.visibility = View.GONE
        paymentEnquiryContainer?.visibility = View.GONE
        if (tvPdTextView != null) {
            if(paymentMode.isNotEmpty()) {
                tvPdTextView.visibility = View.VISIBLE
                ivPdTitle?.visibility = View.VISIBLE
                tvPdTextView.text = "Pay with $paymentMode"
            }else{
                tvPdTextView.visibility = View.GONE
                ivPdTitle?.visibility = View.GONE
            }
        }
        if (ivPdTitle != null) {
            ivPdTitle.background = drawable
        }
        val btnClose = paymentView?.findViewById<Button>(R.id.btn_pd_cancel)
        btnClose?.visibility = View.VISIBLE
        btnClose?.setOnClickListener {
            paymentDialog?.dismiss()
        }
        paymentDialog?.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun openPaymentDialog(paymentMode: String) {
        val ivPdTitle = paymentView?.findViewById<ImageView>(R.id.iv_pd_title)
        val tvPdTextView = paymentView?.findViewById<TextView>(R.id.tv_pd_title)
        val cardDetailContainer =
            paymentView?.findViewById<RelativeLayout>(R.id.card_detail_container)
        val bankDetailContainer =
            paymentView?.findViewById<RelativeLayout>(R.id.netBanking_detail_container)
        val upiDetailContainer = paymentView?.findViewById<RelativeLayout>(R.id.upi_detail_container)
        val walletDetailContainer =
            paymentView?.findViewById<RelativeLayout>(R.id.wallet_detail_container)
        cardDetailContainer?.visibility = View.GONE
        bankDetailContainer?.visibility = View.GONE
        upiDetailContainer?.visibility = View.GONE
        walletDetailContainer?.visibility = View.GONE
        val btnPayment = paymentView?.findViewById<Button>(R.id.btn_pd_pay)
        btnPayment?.text = "Pay ₹ ${options.amount.toDouble()}"
        btnPayment?.visibility = View.GONE

        if (tvPdTextView != null) {
            tvPdTextView.text = "Pay with $paymentMode"
        }
        when (paymentMode) {
            value_payment_mode_card -> {
                try {
                    paymentView?.findViewById<AppCompatEditText>(R.id.edt_card_number)?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                            try {
                                if (!isInputCorrect(
                                        s,
                                        CARD_NUMBER_TOTAL_SYMBOLS,
                                        CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER
                                    )
                                ) {
                                    try {
                                        s.replace(
                                            0, s.length, concatString(
                                                getDigitArray(
                                                    s,
                                                    CARD_NUMBER_TOTAL_DIGITS
                                                ), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER
                                            )
                                        )
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }

                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            if (s.toString().length == 7) {
                                val cardBinDataPayload = AppPayloads.binDataRequestPayload(
                                    s.toString(),
                                    options.token.toString(),
                                    options.orderId.toString()
                                )
                                NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                                    ?.process(cardBinDataPayload)
                            }

                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }
                    })

                    paymentView?.findViewById<AppCompatEditText>(R.id.edt_cvv)?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                            if (s.length > CARD_CVC_TOTAL_SYMBOLS) {
                                s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length)
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }
                    })

                    paymentView?.findViewById<AppCompatEditText>(R.id.edt_expiry)?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                            if (!isInputCorrect(
                                    s,
                                    CARD_DATE_TOTAL_SYMBOLS,
                                    CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER
                                )
                            ) {
                                s.replace(
                                    0, s.length, concatString(
                                        getDigitArray(
                                            s,
                                            CARD_DATE_TOTAL_DIGITS
                                        ), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER
                                    )
                                )
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (cardDetailContainer != null) {
                    cardDetailContainer.visibility = View.VISIBLE
                }
                if (ivPdTitle != null) {
                    ivPdTitle.background = resources.getDrawable(R.drawable.credit_card, null)
                }
                btnPayment?.visibility = View.VISIBLE
            }
            value_payment_mode_netbanking -> {

                if (bankDetailContainer != null) {
                    bankDetailContainer.visibility = View.VISIBLE
                }
                if (ivPdTitle != null) {
                    ivPdTitle.background = resources.getDrawable(R.drawable.bank, null)
                }
                btnPayment?.visibility = View.VISIBLE
            }
            value_payment_mode_upi -> {

                if (upiDetailContainer != null) {
                    upiDetailContainer.visibility = View.VISIBLE
                }
                if (ivPdTitle != null) {
                    ivPdTitle.background = resources.getDrawable(R.drawable.bhim, null)
                }
                btnPayment?.visibility = View.VISIBLE
                btnPayment?.text = "Verify"
            }
            value_payment_mode_wallet -> {
                if (walletDetailContainer != null) {
                    walletDetailContainer.visibility = View.VISIBLE
                }
                if (ivPdTitle != null) {
                    ivPdTitle.background = resources.getDrawable(R.drawable.ic_wallet, null)
                }
            }
        }
        paymentDialog?.show()

        val btnClose = paymentView?.findViewById<Button>(R.id.btn_pd_cancel)
        btnClose?.setOnClickListener {
            paymentDialog?.dismiss()
        }

        btnPayment?.setOnClickListener {
            when (paymentMode) {
                value_payment_mode_card -> {
                    val validateCardPayload = AppPayloads.validateCardDetailPayload(
                        options.token.toString(),
                        options.orderId.toString(),
                        paymentView?.findViewById<AppCompatEditText>(R.id.edt_card_number)?.text.toString().trim(),
                        paymentView?.findViewById<AppCompatEditText>(R.id.edt_expiry)?.text.toString().trim(),
                        paymentView?.findViewById<AppCompatEditText>(R.id.edt_cvv)?.text.toString().trim(),
                        paymentView?.findViewById<AppCompatEditText>(R.id.edt_card_holder_name)?.text.toString().trim()
                    )
                    NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                        ?.process(validateCardPayload)
                }
                value_payment_mode_upi -> {
                    val upiId = paymentView?.findViewById<AppCompatEditText>(R.id.edt_upi_id)?.text.toString()
                    if(btnPayment.text.toString().equals("Verify",true)) {
                        if(upiId.isEmpty()) {
                            displayToast("Please enter upi id to proceed.")
                        }else{
                            val initiatePaymentPayload = AppPayloads.upiInitiatePaymentRequestPayload(
                                upiId,
                                options.token.toString(),
                                options.orderId.toString()
                            )
                            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                                ?.process(initiatePaymentPayload)
                        }
                    }else{
                        val completePaymentRequestPayload =
                            AppPayloads.getUpiCompletePaymentRequestPayload(
                                options.token.toString(),
                                options.orderId.toString(),
                                upiId
                            )
                        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                            ?.process(completePaymentRequestPayload)
                    }

                }

            }
        }
    }

    fun payLaterCompletePayment(
        paymentMode: String,
        transactionId: String,
        isOtpRequired: Boolean,
        drawable: Drawable
    ) {
        setPaymentDialogInitialSetting(paymentMode, drawable)
        var paymentType = PayloadKeys.value_payment_type_auto_debit
        val payLaterOtpContainer =  paymentView?.findViewById<LinearLayout>(R.id.paylater_otp_container)
        if(isOtpRequired) {
         paymentType =  value_payment_type_otp

            payLaterOtpContainer?.visibility  = View.VISIBLE
        }else{
            payLaterOtpContainer?.visibility  = View.GONE
        }

        val tvResendOtp = paymentView?.findViewById<TextView>(R.id.tv_resend_otp)
        val edtOtp = paymentView?.findViewById<EditText>(R.id.edt_otp)
        edtOtp?.setText("")
        tvResendOtp?.setOnClickListener {
            val resendOtpRequestPayload = AppPayloads.getResendOtpRequestPayload(
                options.token.toString(),
                options.orderId.toString(),
                paymentMode,
                transactionId
            )
            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                ?.process(resendOtpRequestPayload)
        }

        val btnPayment = paymentView?.findViewById<Button>(R.id.btn_pd_pay)
        btnPayment?.text = "Pay ₹ ${options.amount.toDouble()}"
        btnPayment?.setOnClickListener {
            val completePaymentRequestPayload = AppPayloads.getCompletePaymentRequestPayload(
                options.token.toString(),
                options.orderId.toString(),
                paymentMode,
                paymentType,
                transactionId,
                edtOtp?.text.toString()
            )
            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                ?.process(completePaymentRequestPayload)
        }

    }

    private fun setUpListOfWallet(itemCol: List<Item>?) {
        val rwWallet = paymentView?.findViewById<RecyclerView>(R.id.rw_wallet)
        val customAdapter = itemCol?.let { ListOfWalletAdapter(this, it) }
        if (rwWallet != null) {
            rwWallet.adapter = customAdapter
        }
        rwWallet?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        rwWallet?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.HORIZONTAL
            )
        )
    }

    private fun setUpRecyclerView(items: List<Item>) {
        if(items.isNotEmpty()) {
            val ctx = this
            rv_fast_payment_methods?.apply {
                layoutManager = LinearLayoutManager(context)
                adapter =
                    FastPaymentModeAdapter(
                        items,
                        ctx
                    )
            }
            tv_header_Fast!!.visibility = View.VISIBLE
        }
        tv_header_others!!.visibility = View.VISIBLE
        inc_layout_others!!.visibility =  View.VISIBLE
    }

    private fun setUpListOfBanks(paymentMode: String, items: List<Item>) {

        val rwBank = paymentView?.findViewById<RecyclerView>(R.id.rw_banks)
        val spnBank = paymentView?.findViewById<Spinner>(R.id.spn_bank)
        val defaultItem = Item(
            null, null, null, null, null, null, null, "-1", "Select Bank", null, null, null, null,
            null, null, null
        )
        (items as ArrayList<Item>).add(0, defaultItem)
        var customAdapter = ListOfbankAdapter(this, paymentMode, items.subList(1, items.size))
        if (items.size > 6) {
            customAdapter = ListOfbankAdapter(this, paymentMode, items.subList(1, 7))
        }
        if (rwBank != null) {
            rwBank.adapter = customAdapter
        }
        rwBank?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        rwBank?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.HORIZONTAL
            )
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val adapter = BankSpinAdapter(
                this,
                R.layout.spinner_dropdown,
                R.id.text1,
                items,
                this.getColor(android.R.color.black),
                this.getColor(R.color.transparent_bg),
            )
            if (spnBank != null) {
                spnBank.adapter = adapter
            }
        } else {
            val adapter = BankSpinAdapter(
                this,
                R.layout.spinner_dropdown,
                R.id.text1,
                items,
                this.resources.getColor(android.R.color.black),
                this.resources.getColor(R.color.transparent_bg)
            )
            if (spnBank != null) {
                spnBank.adapter = adapter
            }
        }
    }

    fun initiatePayment(paymentMode: String, item: Item) {
        val initiatePaymentPayload = AppPayloads.initiatePaymentPayload(
            paymentMode, item,
            options.token.toString(),
            options.orderId.toString()
        )
        NimbblPayCheckoutBaseSDK.getInstance(applicationContext)?.process(initiatePaymentPayload)
    }


    private fun isInputCorrect(
        s: Editable,
        size: Int,
        dividerPosition: Int,
        divider: Char
    ): Boolean {
        var isCorrect = s.length <= size
        for (i in s.indices) {
            isCorrect = if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect and (divider == s[i])
            } else {
                isCorrect and Character.isDigit(s[i])
            }
        }
        return isCorrect
    }

    private fun concatString(digits: CharArray, dividerPosition: Int, divider: Char): String? {
        val formatted = StringBuilder()
        for (i in digits.indices) {
            if (digits[i].code != 0) {
                formatted.append(digits[i])
                if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                    formatted.append(divider)
                }
            }
        }
        return formatted.toString()
    }

    private fun getDigitArray(s: Editable, size: Int): CharArray {
        val digits = CharArray(size)
        var index = 0
        var i = 0
        while (i < s.length && index < size) {
            val current = s[i]
            if (Character.isDigit(current)) {
                digits[index] = current
                index++
            }
            i++
        }
        return digits
    }

    fun upiIntentCompletePayment(
        paymentMode: String,
        subPaymentMode: String,
        flow: String,
        appPackageName: String,
        drawable: Drawable
    ) {
        setPaymentDialogInitialSetting(paymentMode, drawable)
        val btnPayment = paymentView?.findViewById<Button>(R.id.btn_pd_pay)
        btnPayment?.text = "Pay ₹ ${options.amount.toDouble()}"
        btnPayment?.setOnClickListener {
            val completePaymentRequestPayload =
                AppPayloads.getUpiIntentCompletePaymentRequestPayload(
                    options.token.toString(),
                    options.orderId.toString(),
                    paymentMode,
                    subPaymentMode,
                    flow,
                    appPackageName
                )
            NimbblPayCheckoutBaseSDK.getInstance(applicationContext)
                ?.process(completePaymentRequestPayload)
        }

    }

    companion object {
        // size of pattern 0000-0000-0000-0000
        private const val CARD_NUMBER_TOTAL_SYMBOLS = 19

        // max numbers of digits in pattern: 0000 x 4
        private const val CARD_NUMBER_TOTAL_DIGITS = 16

        // means divider position is every 5th symbol beginning with 1
        private const val CARD_NUMBER_DIVIDER_MODULO = 5

        // means divider position is every 4th symbol beginning with 0
        private const val CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1

        private const val CARD_NUMBER_DIVIDER = ' '

        // max numbers of digits in pattern: MM + YY
        private const val CARD_DATE_TOTAL_DIGITS = 4

        // size of pattern MM/YY
        private const val CARD_DATE_TOTAL_SYMBOLS = 5

        // means divider position is every 3rd symbol beginning with 1
        private const val CARD_DATE_DIVIDER_MODULO = 3

        // means divider position is every 2nd symbol beginning with 0
        private const val CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1
        private const val CARD_DATE_DIVIDER = '/'
        private const val CARD_CVC_TOTAL_SYMBOLS = 3
    }
}
