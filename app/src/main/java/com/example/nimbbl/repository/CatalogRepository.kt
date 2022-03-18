package com.example.nimbbl.repository

import com.example.nimbbl.model.GenerateTokenResponse
import com.example.nimbbl.model.createoder.OrderDetailVo
import com.example.nimbbl.model.postbody.GenerateTokenbody
import com.example.nimbbl.network.ApiCall
import com.example.nimbbl.utils.AppPayloads
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response

class CatalogRepository {
    suspend fun CreateOrder(
        url: String,
        id: Int,
        token: String
    ): Response<OrderDetailVo> {
        val createOrderObj = AppPayloads.createOrderPayload(id)
        val body: RequestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            createOrderObj.toString()
        )
        return ApiCall()!!.creatOrder(url,"Bearer $token", body)

    }

    suspend fun generateToken(
        url: String,
        body: GenerateTokenbody
    ): Response<GenerateTokenResponse> {
        return ApiCall()!!.generateToken(url, body)
    }
}