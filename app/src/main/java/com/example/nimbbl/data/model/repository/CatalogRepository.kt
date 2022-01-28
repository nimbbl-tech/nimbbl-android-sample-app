package com.example.nimbbl.data.model.repository

import com.example.nimbbl.data.model.model.GenerateTokenResponse
import com.example.nimbbl.data.model.model.createoder.CreateOrder_Model
import com.example.nimbbl.data.model.model.postbody.Catlogbody
import com.example.nimbbl.data.model.model.postbody.GenerateTokenbody
import com.example.nimbbl.data.model.network.ApiCall
import retrofit2.Response

class CatalogRepository {
    suspend fun CreateOrder(
        url: String,
        id: Catlogbody
    ): Response<CreateOrder_Model> {

        return ApiCall()!!.creatOrder(url, id)

    }

    suspend fun generateToken(
        url: String,
        body: GenerateTokenbody
    ): Response<GenerateTokenResponse> {
        return ApiCall()!!.generateToken(url, body)
    }
}