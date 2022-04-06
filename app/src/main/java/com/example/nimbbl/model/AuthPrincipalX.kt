package com.example.nimbbl.model

data class AuthPrincipalX(
    val access_key: String,
    val active: Boolean,
    val id: Int,
    val number_of_token_per_minute: Int,
    val skip_device_verification: Boolean,
    val sub_merchant_id: Int,
    val type: String
)