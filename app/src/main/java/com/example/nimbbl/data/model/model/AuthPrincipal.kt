package com.example.nimbbl.data.model.model

data class AuthPrincipal(

    val access_key: String,
    val active: Boolean,
    val id: Int,
    val sub_merchant_id: Int
)