package com.example.nimbbl.model

data class Result(
    val auth_principal: AuthPrincipalX,
    val expires_at: String,
    val token: String
)