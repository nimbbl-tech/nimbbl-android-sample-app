package com.example.nimbbl.data.model.repository

import com.example.nimbbl.data.model.model.createoder.CreateOrder_Model
import com.example.nimbbl.data.model.model.postbody.Catlogbody
import com.example.nimbbl.data.model.network.ApiCall
import retrofit2.Response

class CatalogRepository  {
    suspend   fun CreateOrder(
        id: Catlogbody
    ): Response<CreateOrder_Model> {

        val response= ApiCall()!!.creatOrder(id)

        return response

    }
}