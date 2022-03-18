package com.example.nimbbl.model

data class AuthPrincipal(
    val access_key: String,
    val active: Boolean,
    val id: Int,
    val sub_merchant_id: Int
)