package com.example.nimbbl.repository

import com.example.nimbbl.model.GenerateDemoTokenResponse
import com.example.nimbbl.model.createoder.OrderDetailVo
import com.example.nimbbl.network.ApiCall
import com.example.nimbbl.utils.AppPayloads
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response

class CatalogRepository {
    suspend fun createOrder(
        url: String,
        id: Int,
        token: String,
        skuTitle: String,
        skuAmount: String,
        skuDesc: String,
        userFirstName: String,
        userLastName: String,
        userEmailId: String,
        userMobileNumber: String,
        useraddressLine1: String,
        userAddrStreet: String,
        userAddrLandmark: String,
        userAddrArea: String,
        userAddrCity: String,
        userAddrState: String,
        userAddrPin: String
    ): Response<OrderDetailVo> {
        val createOrderObj = AppPayloads.createOrderPayload(id,skuAmount,userEmailId,userFirstName,userLastName,userMobileNumber,useraddressLine1,userAddrStreet,userAddrLandmark,userAddrArea,userAddrCity,userAddrState,userAddrPin, skuTitle,skuDesc)
        val body: RequestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            createOrderObj.toString()
        )
        return ApiCall()!!.creatOrder(url,"Bearer $token", body)
    }

    suspend fun generateToken(
        url: String,
        body: RequestBody
    ): Response<GenerateDemoTokenResponse> {
        return ApiCall()!!.generateToken(url, body)
    }
}