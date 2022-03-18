package com.example.nimbbl.utils

import com.zl.nimbblpaycoresdk.models.Item
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_completePayment
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_getBinData
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_initiateOrder
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_initiatePayment
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_paymentEnquiry
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_paymentModes
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_resendOtp
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_resolveUser
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_validateCard
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.action_verifyUser
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_OrderID
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_accessKey
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_action
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_app_package_name
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_card_cvv
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_card_expiry
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_card_holder_name
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_card_number
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_flow
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_mobileNumber
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_nimbblPayload
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_otp
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_packageName
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_payment_mode
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_payment_type
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_subMerchantId
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_sub_payment_mode
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_token
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_transaction_id
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.key_upi_id
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_mode_upi
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_upi_flow_mode_collect
import org.json.JSONArray
import org.json.JSONObject

/*
Created by Sandeep Yadav on 23/02/22.
Copyright (c) 2022 Bigital Technologies Pvt. Ltd. All rights reserved.
*/

object AppPayloads {

    fun createOrderPayload(
        productID: Int
    ): JSONObject {
        val createOrderPayload = JSONObject()
        try {
            createOrderPayload.put("id", productID)
            createOrderPayload.put("currency", "INR")
            createOrderPayload.put("total_amount", "2")

            val userJsonObj = JSONObject()
            userJsonObj.put("email", "ydv.sandeep24@gmail.com")
            userJsonObj.put("first_name", "Sandeep")
            userJsonObj.put("last_name", "Yadav")
            userJsonObj.put("country_code", "+91")
            userJsonObj.put("mobile_number", "8454906089")
            createOrderPayload.put("user", userJsonObj)

            val shippingAddrJsonObj = JSONObject()
            shippingAddrJsonObj.put("address_1", "My address")
            shippingAddrJsonObj.put("street", "My street")
            shippingAddrJsonObj.put("landmark", "My landmark")
            shippingAddrJsonObj.put("area", "My area")
            shippingAddrJsonObj.put("city", "Mumbai")
            shippingAddrJsonObj.put("state", "Maharashtra")
            shippingAddrJsonObj.put("pincode", "400018")
            shippingAddrJsonObj.put("address_type", "residential")
            createOrderPayload.put("shipping_address", shippingAddrJsonObj)

            val orderLineItemJsonArray = JSONArray()
            val orderLineItem = JSONObject()
            orderLineItem.put("sku_id", "sku1")
            orderLineItem.put("title", "sku1")
            orderLineItem.put("description", "sku1")
            orderLineItem.put("quantity", "1")
            orderLineItem.put("rate", "5")
            orderLineItem.put("amount_before_tax", "5")
            orderLineItem.put("tax", "1")
            orderLineItem.put("total_amount", "6")
            orderLineItem.put(
                "image_url",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR7Ay5KWSTviUsTHZ7m_-YJvOlPMwGhZIPuzobqynBqQbQP1_KWCuc8qlwREOiTP38Hs_fLTJYl&usqp=CAc"
            )
            orderLineItemJsonArray.put(orderLineItem)
            createOrderPayload.put("order_line_items", orderLineItemJsonArray)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return createOrderPayload
    }

    fun initResourcePayload(
        token: String,
        subMerchantId: String,
        accessKey: String,
        packageName: String
    ): JSONObject {
        val inputInItPayload = JSONObject()
        try {
            inputInItPayload.put(key_token, token)
            inputInItPayload.put(key_subMerchantId, subMerchantId)

            val nimbblJsonPayload = JSONObject()
            nimbblJsonPayload.put(key_accessKey, accessKey)
            nimbblJsonPayload.put(key_packageName, packageName)
            inputInItPayload.put(key_nimbblPayload, nimbblJsonPayload)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inputInItPayload
    }

    fun initialiseOrderPayload(
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val initiatePayload = JSONObject()
        try {
            initiatePayload.put(key_action, action_initiateOrder)
            initiatePayload.put(key_token, token)
            initiatePayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            initiatePayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return initiatePayload
    }

    fun paymentModesPayload(
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val initiatePayload = JSONObject()
        try {
            initiatePayload.put(key_action, action_paymentModes)
            initiatePayload.put(key_token, token)
            initiatePayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            initiatePayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return initiatePayload
    }

    fun initiatePaymentPayload(
        payment_mode: String,
        item: Item,
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val initiatePaymentPayload = JSONObject()
        try {
            initiatePaymentPayload.put(key_action, action_initiatePayment)
            initiatePaymentPayload.put(key_token, token)
            initiatePaymentPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, payment_mode)
            nimbblPayloadObj.put(key_sub_payment_mode, item.sub_payment_name)
            initiatePaymentPayload.put(key_nimbblPayload, nimbblPayloadObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return initiatePaymentPayload
    }

    fun resolveUserPayload(
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val resolveUserPayload = JSONObject()
        try {
            resolveUserPayload.put(key_action, action_resolveUser)
            resolveUserPayload.put(key_token, token)
            resolveUserPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            resolveUserPayload.put(key_nimbblPayload, nimbblPayloadObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resolveUserPayload
    }

    fun verifyUserPayload(
        token: String,
        subMerchantId: String,
        orderID: String,
        mobileNo: String,
        otp: String
    ): JSONObject {
        val resolveUserPayload = JSONObject()
        try {
            resolveUserPayload.put(key_action, action_verifyUser)
            resolveUserPayload.put(key_token, token)
            resolveUserPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_mobileNumber, mobileNo)
            nimbblPayloadObj.put(key_otp, otp)
            resolveUserPayload.put(key_nimbblPayload, nimbblPayloadObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resolveUserPayload
    }

    fun getBinDataCardDetailPayload(
        cardNumber: String,
        cardExpiry: String,
        cardCvv: String,
        cardHolderName: String,
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val cardDetailPayload = JSONObject()
        try {
            cardDetailPayload.put(key_action, action_getBinData)
            cardDetailPayload.put(key_token, token)
            cardDetailPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_card_number, cardNumber)
            nimbblPayloadObj.put(key_card_cvv, cardCvv)
            nimbblPayloadObj.put(key_card_expiry, cardExpiry)
            nimbblPayloadObj.put(key_card_holder_name, cardHolderName)
            cardDetailPayload.put(key_nimbblPayload, nimbblPayloadObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cardDetailPayload
    }

    fun validateCardDetailPayload(
        token: String,
        subMerchantId: String,
        orderID: String,
        cardNumber: String,
        cardExpiry: String,
        cardCvv: String,
        cardHolderName: String
    ): JSONObject {
        val cardDetailPayload = JSONObject()
        try {
            cardDetailPayload.put(key_action, action_validateCard)
            cardDetailPayload.put(key_token, token)
            cardDetailPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_card_number, cardNumber)
            nimbblPayloadObj.put(key_card_cvv, cardCvv)
            nimbblPayloadObj.put(key_card_expiry, cardExpiry)
            nimbblPayloadObj.put(key_card_holder_name, cardHolderName)
            cardDetailPayload.put(key_nimbblPayload, nimbblPayloadObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cardDetailPayload
    }

    fun initiateCardPaymentPayload(
        cardNumber: String,
        cardExpiry: String,
        cardCvv: String,
        cardHolderName: String,
        payment_mode: String,
        sub_payment_mode: String,
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val initiatePaymentPayload = JSONObject()
        try {
            initiatePaymentPayload.put(key_action, action_initiatePayment)
            initiatePaymentPayload.put(key_token, token)
            initiatePaymentPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, payment_mode)
            nimbblPayloadObj.put(key_sub_payment_mode, sub_payment_mode)
            nimbblPayloadObj.put(key_card_number, cardNumber)
            nimbblPayloadObj.put(key_card_cvv, cardCvv)
            nimbblPayloadObj.put(key_card_expiry, cardExpiry)
            nimbblPayloadObj.put(key_card_holder_name, cardHolderName)
            initiatePaymentPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return initiatePaymentPayload
    }

    fun binDataRequestPayload(
        cardNo: String,
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val cardBinDataPayload = JSONObject()
        try {
            cardBinDataPayload.put(key_action, action_getBinData)
            cardBinDataPayload.put(key_token, token)
            cardBinDataPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_card_number, cardNo)
            cardBinDataPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cardBinDataPayload
    }

    fun getResendOtpRequestPayload(
        token: String,
        subMerchantId: String,
        orderID: String,
        paymentMode: String,
        transactionId: String
    ): JSONObject {
        val resendOtpPayload = JSONObject()
        try {
            resendOtpPayload.put(key_action, action_resendOtp)
            resendOtpPayload.put(key_token, token)
            resendOtpPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, paymentMode)
            nimbblPayloadObj.put(key_transaction_id, transactionId)
            resendOtpPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resendOtpPayload
    }

    fun getTransactionEnquiryRequestPayload(
        token: String,
        subMerchantId: String,
        orderID: String,
        paymentMode: String,
        transactionId: String
    ): JSONObject {
        val transactionEnqPayload = JSONObject()
        try {
            transactionEnqPayload.put(key_action, action_paymentEnquiry)
            transactionEnqPayload.put(key_token, token)
            transactionEnqPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, paymentMode)
            nimbblPayloadObj.put(key_transaction_id, transactionId)
            transactionEnqPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return transactionEnqPayload
    }

    fun getCompletePaymentRequestPayload(
        token: String,
        subMerchantId: String,
        orderID: String,
        paymentMode: String,
        paymentType:String,
        transactionId: String,
        otp: String = "",
        upiId: String = "",
        flow: String = ""
    ): JSONObject {
        val completePaymentPayload = JSONObject()
        try {
            completePaymentPayload.put(key_action, action_completePayment)
            completePaymentPayload.put(key_token, token)
            completePaymentPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, paymentMode)
            nimbblPayloadObj.put(key_payment_type, paymentType)
            nimbblPayloadObj.put(key_transaction_id, transactionId)
            nimbblPayloadObj.put(key_otp,otp)
            nimbblPayloadObj.put(key_upi_id,upiId)
            nimbblPayloadObj.put(key_flow,flow)
            completePaymentPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return completePaymentPayload
    }

    fun getUpiIntentCompletePaymentRequestPayload(
        token: String,
        subMerchantId: String,
        orderID: String,
        paymentMode: String,
        subPaymentMode: String,
        flow: String,
        appPackageName: String
    ): JSONObject {
        val upiIntentCompletePaymentPayload = JSONObject()
        try {
            upiIntentCompletePaymentPayload.put(key_action, action_completePayment)
            upiIntentCompletePaymentPayload.put(key_token, token)
            upiIntentCompletePaymentPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, paymentMode)
            nimbblPayloadObj.put(key_sub_payment_mode, subPaymentMode)
            nimbblPayloadObj.put(key_app_package_name, appPackageName)
            nimbblPayloadObj.put(key_flow,flow)
            nimbblPayloadObj.put(key_payment_type, "")
            nimbblPayloadObj.put(key_transaction_id, "")
            nimbblPayloadObj.put(key_otp,"")
            nimbblPayloadObj.put(key_upi_id,"")
            upiIntentCompletePaymentPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return upiIntentCompletePaymentPayload
    }

    fun upiInitiatePaymentRequestPayload(
        vpaId: String,
        token: String,
        subMerchantId: String,
        orderID: String
    ): JSONObject {
        val upiReqPayload = JSONObject()
        try {
            upiReqPayload.put(key_action, action_initiatePayment)
            upiReqPayload.put(key_token, token)
            upiReqPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, value_payment_mode_upi)
            nimbblPayloadObj.put(key_flow,value_upi_flow_mode_collect)
            nimbblPayloadObj.put(key_upi_id,vpaId)
            upiReqPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return upiReqPayload


    }

    fun getUpiCompletePaymentRequestPayload(
        token: String,
        subMerchantId: String,
        orderID: String,
        vpaId: String
    ): JSONObject {
        val upiReqPayload = JSONObject()
        try {
            upiReqPayload.put(key_action, action_completePayment)
            upiReqPayload.put(key_token, token)
            upiReqPayload.put(key_subMerchantId, subMerchantId)
            val nimbblPayloadObj = JSONObject()
            nimbblPayloadObj.put(key_OrderID, orderID)
            nimbblPayloadObj.put(key_payment_mode, value_payment_mode_upi)
            nimbblPayloadObj.put(key_flow,value_upi_flow_mode_collect)
            nimbblPayloadObj.put(key_upi_id,vpaId)
            upiReqPayload.put(key_nimbblPayload, nimbblPayloadObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return upiReqPayload
    }


}