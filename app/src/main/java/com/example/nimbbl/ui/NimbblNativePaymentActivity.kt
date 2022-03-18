package com.example.nimbbl.ui

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
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimbbl.R
import com.example.nimbbl.ui.adapters.BankSpinAdapter
import com.example.nimbbl.ui.adapters.FastPaymentModeAdapter
import com.example.nimbbl.ui.adapters.ListOfWalletAdapter
import com.example.nimbbl.ui.adapters.ListOfbankAdapter
import com.example.nimbbl.utils.AppPayloads
import com.example.nimbbl.utils.displayToast
import com.example.nimbbl.utils.hidePhoneNum
import com.example.nimbbl.utils.printLog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zl.nimbblpaycoresdk.NimbblPayCheckoutSDK
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
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_verifyUser
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
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_vpa_id
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_icici_paylater
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_lazypay
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_olamoney
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_upi
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_status_failed
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_status_success
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_type_otp
import kotlinx.android.synthetic.main.activity_nimbbl_native_payment.*
import kotlinx.android.synthetic.main.layout_fast.*
import kotlinx.android.synthetic.main.layout_others.*
import kotlinx.android.synthetic.main.layout_user.*
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject
import tech.nimbbl.checkout.sdk.RestApiUtils
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@DelicateCoroutinesApi
class NimbblNativePaymentActivity : AppCompatActivity(),
    NimbblPayNativeCheckoutPaymentListener {
    private var upiPollingDialog: AlertDialog? = null
    private var displayCountDownTimer: CountDownTimer? = null
    private var pollingCountDownTimer: CountDownTimer? = null
    private val tagOption = "options"
    private lateinit var options: NimbblCheckoutOptions
    private lateinit var cardCategory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        setContentView(R.layout.activity_nimbbl_native_payment)
        NimbblPayCheckoutSDK.getInstance(this)?.isInitialised(this)
        if (intent.hasExtra(tagOption)) {
            options = intent.getParcelableExtra(tagOption)!!
            val tokenKey = options.key
            if (TextUtils.isEmpty(tokenKey)) {
                displayToast(getString(tech.nimbbl.checkout.sdk.R.string.key_not_set))
            } else {
                if (!RestApiUtils.isNetConnected(this)) {
                    displayToast(getString(tech.nimbbl.checkout.sdk.R.string.no_internet))
                }
            }
        } else {
            displayToast(getString(tech.nimbbl.checkout.sdk.R.string.input_sent_invalid))
        }
        try {
            NimbblPayCheckoutSDK.getInstance(this)?.registerCallback(this, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val initiateOrderPayload = AppPayloads.initialiseOrderPayload(
            options.token.toString(),
            options.subMerchantId.toString(),
            options.orderId.toString()
        )
        NimbblPayCheckoutSDK.getInstance(this)?.process(initiateOrderPayload)

        /*       val resolveUserPayload = AppPayloads.resolveUserPayload(
                   options.token.toString(),
                   options.subMerchantId.toString(),
                   options.orderId.toString()
               )

               NimbblPayCheckoutSDK.getInstance(this)?.process(resolveUserPayload)*/

        setListeners()

    }

    private fun setListeners() {
        user_card.setOnExpandedListener { _, isExpanded ->
            if (isExpanded) {
                val resolveUserPayload = AppPayloads.resolveUserPayload(
                    options.token.toString(),
                    options.subMerchantId.toString(),
                    options.orderId.toString()
                )
                NimbblPayCheckoutSDK.getInstance(this)?.process(resolveUserPayload)
            }
        }
        try {
            rl_card_container.setOnClickListener {
                if (card_detail_container.isVisible) {
                    card_detail_container.visibility = View.GONE
                } else {
                    card_detail_container.visibility = View.VISIBLE
                }

            }
            rl_netbanking_container.setOnClickListener {
                if (netBanking_detail_container.isVisible) {
                    netBanking_detail_container.visibility = View.GONE
                } else {
                    netBanking_detail_container.visibility = View.VISIBLE
                    others_card.expand()

                }
            }

            rl_upi_container.setOnClickListener {
                if (upi_detail_container.isVisible) {
                    upi_detail_container.visibility = View.GONE
                } else {
                    upi_detail_container.visibility = View.VISIBLE
                }
            }

            rl_wallet_container.setOnClickListener {
                if (wallet_detail_container.isVisible) {
                    wallet_detail_container.visibility = View.GONE
                } else {
                    wallet_detail_container.visibility = View.VISIBLE
                    others_card.expand()

                }
            }
            edt_card_number.addTextChangedListener(object : TextWatcher {
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
                            options.subMerchantId.toString(),
                            options.orderId.toString()
                        )
                        NimbblPayCheckoutSDK.getInstance(this@NimbblNativePaymentActivity)
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

            edt_cvv.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (s.length > CARD_CVC_TOTAL_SYMBOLS) {
                        s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length);
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

            edt_expiry.addTextChangedListener(object : TextWatcher {
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
                        );
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
        btn_cc_dc_pay.setOnClickListener {
            val validateCardPayload = AppPayloads.validateCardDetailPayload(
                options.token.toString(),
                options.subMerchantId.toString(),
                options.orderId.toString(),
                edt_card_number.text.toString().trim(),
                edt_expiry.text.toString().trim(),
                edt_cvv.text.toString().trim(),
                edt_card_holder_name.text.toString().trim()
            )
            NimbblPayCheckoutSDK.getInstance(this)?.process(validateCardPayload)
        }
        btn_verify.setOnClickListener {
            upiInitiatePayment(edt_upi_id.text.toString().trim())
        }
    }

    fun upiInitiatePayment(upiId: String) {
        val initiatePaymentPayload = AppPayloads.upiInitiatePaymentRequestPayload(
            upiId,
            options.token.toString(),
            options.subMerchantId.toString(),
            options.orderId.toString()
        )
        NimbblPayCheckoutSDK.getInstance(this)?.process(initiatePaymentPayload)
    }

    override fun onEventReceived(data: JSONObject) {
        printLog("SAN", "event-->" + data.getString(key_event))
        when (data.getString(key_event)) {

            event_display_loader -> {
                val inAnimation = AlphaAnimation(0f, 1f)
                inAnimation.duration = 200

                progressBarHolder.animation = inAnimation
                pg_card_fast_holder.animation = inAnimation
                pg_card_others_holder.animation = inAnimation
                progressBarHolder.visibility = View.VISIBLE
                pg_card_fast_holder.visibility = View.VISIBLE
                pg_card_others_holder.visibility = View.VISIBLE
            }
            event_hide_loader -> {
                val outAnimation = AlphaAnimation(1f, 0f)
                outAnimation.duration = 200

                progressBarHolder.animation = outAnimation
                pg_card_fast_holder.animation = outAnimation
                pg_card_others_holder.animation = outAnimation
                progressBarHolder.visibility = View.GONE
                pg_card_fast_holder.visibility = View.GONE
                pg_card_others_holder.visibility = View.GONE

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
                        if (nextStep.equals(PayloadKeys.value_step_payment_mode)) {
                            val initiateOrderPayload = AppPayloads.initialiseOrderPayload(
                                options.token.toString(),
                                options.subMerchantId.toString(),
                                options.orderId.toString()
                            )
                            NimbblPayCheckoutSDK.getInstance(this)?.process(initiateOrderPayload)
                        } else {
                            val userName =
                                data.getJSONObject(key_nimbblPayload).getString(key_user_name)
                            val mobileNo =
                                data.getJSONObject(key_nimbblPayload).getString(key_mobileNumber)
                            tv_mob_number.text = hidePhoneNum(mobileNo)
                            user_card.setTitle(titleText = "Hello $userName")
                            btn_done.setOnClickListener {
                                val verifyUserPayload = AppPayloads.verifyUserPayload(
                                    options.token.toString(),
                                    options.subMerchantId.toString(),
                                    options.orderId.toString(),
                                    mobileNo,
                                    edt_otp.text.toString().trim()
                                )
                                NimbblPayCheckoutSDK.getInstance(this)?.process(verifyUserPayload)
                            }

                        }
                    }
                    action_verifyUser -> {
                        user_card.collapse()
                    }
                    action_initiateOrder -> {
                        val paymentModePayload = AppPayloads.paymentModesPayload(
                            options.token.toString(),
                            options.subMerchantId.toString(),
                            options.orderId.toString()
                        )
                        NimbblPayCheckoutSDK.getInstance(this)?.process(paymentModePayload)
                    }
                    action_paymentModes -> {
                        printLog(
                            "SAN",
                            "paymentMode-->" + data.getJSONObject(key_nimbblPayload)
                                .getJSONArray(key_paymentModes)
                        )
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
                        cardCategory =
                            data.getJSONObject(key_nimbblPayload).getString(key_sub_payment_name)
                    }
                    action_validateCard -> {
                        val initiatePaymentCardPayload = AppPayloads.initiateCardPaymentPayload(
                            edt_card_number.text.toString().trim(),
                            edt_expiry.text.toString().trim(),
                            edt_cvv.text.toString().trim(),
                            edt_card_holder_name.text.toString().trim(),
                            "card",
                            cardCategory,
                            options.token.toString(),
                            options.subMerchantId.toString(),
                            options.orderId.toString()
                        )
                        NimbblPayCheckoutSDK.getInstance(this)?.process(initiatePaymentCardPayload)

                    }
                    action_initiatePayment -> {
                        printLog("SAN", "action_initiatePayment")
                        val paymentMode =
                            data.getJSONObject(key_nimbblPayload).getString(key_payment_mode)
                        if (paymentMode.equals(value_payment_mode_lazypay) ||
                            paymentMode.equals(value_payment_mode_icici_paylater) ||
                            paymentMode.equals(value_payment_mode_olamoney)
                        ) {
                            val transactionId =
                                data.getJSONObject(key_nimbblPayload).getString(key_transaction_id)
                            openOTPDialog(paymentMode, transactionId)
                        } else if (paymentMode.equals(value_payment_mode_upi)) {
                            val completePaymentRequestPayload =
                                AppPayloads.getUpiCompletePaymentRequestPayload(
                                    options.token.toString(),
                                    options.subMerchantId.toString(),
                                    options.orderId.toString(),
                                    data.getJSONObject(key_nimbblPayload).getString(key_vpa_id)
                                )
                            NimbblPayCheckoutSDK.getInstance(this)
                                ?.process(completePaymentRequestPayload)
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
                                    options.subMerchantId.toString(),
                                    options.orderId.toString(),
                                    paymentMode,
                                    transactionId
                                )
                            NimbblPayCheckoutSDK.getInstance(this)
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
                        printLog("SAN", "Enquiry")
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
                        options.subMerchantId.toString(),
                        options.orderId.toString(),
                        paymentMode,
                        transactionId
                    )
                NimbblPayCheckoutSDK.getInstance(this@NimbblNativePaymentActivity)
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
                            options.subMerchantId.toString(),
                            options.orderId.toString(),
                            paymentMode,
                            transactionId
                        )
                    NimbblPayCheckoutSDK.getInstance(this@NimbblNativePaymentActivity)
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
                        options.subMerchantId.toString(),
                        options.orderId.toString(),
                        paymentMode,
                        transactionId
                    )
                NimbblPayCheckoutSDK.getInstance(this@NimbblNativePaymentActivity)
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.lbl_transaction_enquiry))
        val view = layoutInflater.inflate(R.layout.transaction_enquiry, null)
        builder.setView(view)
        if (orderId.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tv_val_order_id).text = orderId
        } else {
            view.findViewById<LinearLayout>(R.id.ll_order_id_container).visibility = View.GONE
        }

        if (transactionId.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tv_val_transaction_id).text = transactionId
        } else {
            view.findViewById<LinearLayout>(R.id.ll_transaction_id_container).visibility = View.GONE
        }
        if (signature.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tv_val_signature).text = signature
        } else {
            view.findViewById<LinearLayout>(R.id.ll_signature_container).visibility = View.GONE
        }

        if (status.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tv_val_status).text = status
        } else {
            view.findViewById<LinearLayout>(R.id.ll_status_container).visibility = View.GONE
        }
        if (message.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tv_val_message).text = message
        } else {
            view.findViewById<LinearLayout>(R.id.ll_message_container).visibility = View.GONE
        }

        builder.setNegativeButton(getString(R.string.lbl_retry)) { _, _ ->

            val paymentEnqRequestPayload =
                AppPayloads.getTransactionEnquiryRequestPayload(
                    options.token.toString(),
                    options.subMerchantId.toString(),
                    options.orderId.toString(),
                    paymentMode,
                    transactionId
                )
            NimbblPayCheckoutSDK.getInstance(this)?.process(paymentEnqRequestPayload)
        }
        builder.setPositiveButton(getString(R.string.lbl_done)) { _, _ ->
            this.finish()
        }
        if (!isFinishing) {
            builder.show()
        }

    }

    private fun openOTPDialog(paymentMode: String, transactionId: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.lbl_enter_otp))
        builder.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.send_otp, null);
        builder.setView(view)

        val tvResendOtp = view.findViewById<TextView>(R.id.tv_resend_otp)
        val edtOtp = view.findViewById<EditText>(R.id.edt_otp)
        tvResendOtp.setOnClickListener {
            val resendOtpRequestPayload = AppPayloads.getResendOtpRequestPayload(
                options.token.toString(),
                options.subMerchantId.toString(),
                options.orderId.toString(),
                paymentMode,
                transactionId
            )
            NimbblPayCheckoutSDK.getInstance(this)?.process(resendOtpRequestPayload)
        }

        builder.setPositiveButton("Complete Payment") { _, _ ->
            val strOtp = edtOtp.text.toString().trim()
            payLaterCompletePayment(paymentMode, transactionId, strOtp, value_payment_type_otp)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()

    }

    fun payLaterCompletePayment(
        paymentMode: String,
        transactionId: String,
        strOtp: String,
        paymentType: String
    ) {
        val completePaymentRequestPayload = AppPayloads.getCompletePaymentRequestPayload(
            options.token.toString(),
            options.subMerchantId.toString(),
            options.orderId.toString(),
            paymentMode,
            paymentType,
            transactionId,
            otp = strOtp
        )
        NimbblPayCheckoutSDK.getInstance(this)?.process(completePaymentRequestPayload)
    }

    private fun setUpListOfWallet(itemCol: List<Item>?) {
        val customAdapter = itemCol?.let { ListOfWalletAdapter(this, it) }
        rw_wallet.adapter = customAdapter
        rw_wallet.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        rw_wallet.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.HORIZONTAL
            )
        )
    }

    private fun setUpRecyclerView(items: List<Item>) {
        val ctx = this
        rv_fast_payment_methods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                FastPaymentModeAdapter(
                    items,
                    ctx
                )
        }
    }

    private fun setUpListOfBanks(paymentMode: String, items: List<Item>) {
        val defaultItem = Item(
            null, null, null, null, null, null, null, "-1", "Select Bank", null, null, null, null,
            null, null, null
        )
        (items as ArrayList<Item>).add(0, defaultItem)
        var customAdapter = ListOfbankAdapter(this, paymentMode, items.subList(1, items.size))
        if (items.size > 6) {
            customAdapter = ListOfbankAdapter(this, paymentMode, items.subList(1, 7))
        }
        rw_banks.adapter = customAdapter
        rw_banks.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        rw_banks.addItemDecoration(
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
                this.getColor(R.color.black),
                this.getColor(R.color.transparent_bg),
            )
            spn_bank.adapter = adapter
        } else {
            val adapter = BankSpinAdapter(
                this,
                R.layout.spinner_dropdown,
                R.id.text1,
                items,
                this.resources.getColor(R.color.black),
                this.resources.getColor(R.color.transparent_bg)
            )
            spn_bank.adapter = adapter
        }
    }

    fun initiatePayment(paymentMode: String, item: Item) {
        val initiatePaymentPayload = AppPayloads.initiatePaymentPayload(
            paymentMode, item,
            options.token.toString(),
            options.subMerchantId.toString(),
            options.orderId.toString()
        )
        NimbblPayCheckoutSDK.getInstance(this)?.process(initiatePaymentPayload)
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
        appPackageName: String
    ) {
        val completePaymentRequestPayload = AppPayloads.getUpiIntentCompletePaymentRequestPayload(
            options.token.toString(),
            options.subMerchantId.toString(),
            options.orderId.toString(),
            paymentMode,
            subPaymentMode,
            flow,
            appPackageName
        )
        NimbblPayCheckoutSDK.getInstance(this)?.process(completePaymentRequestPayload)
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
