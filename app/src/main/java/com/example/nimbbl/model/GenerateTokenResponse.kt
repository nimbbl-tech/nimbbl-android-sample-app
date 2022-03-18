package com.example.nimbbl.model

data class GenerateTokenResponse(
    val auth_principal: AuthPrincipal,
    val expires_at: String,
    val token: String
)