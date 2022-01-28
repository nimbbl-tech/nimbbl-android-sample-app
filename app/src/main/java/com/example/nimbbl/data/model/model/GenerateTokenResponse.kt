package com.example.nimbbl.data.model.model

data class GenerateTokenResponse(
    val auth_principal: AuthPrincipal,

    val expires_at: String,

    val token: String
)