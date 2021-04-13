package com.example.nimbbl.data.model.model.createoder

data class Item(
    val address: Address,
    val amount_before_tax: Int,
    val attempts: Int,
    val callback_mode: Any,
    val callback_url: Any,
    val cancellation_reason: Any,
    val currency: String,
    val custom_attributes: Any,
    val description: Any,
    val device_user_agent: Any,
    val invoice_id: String,
    val merchant_shopfront_domain: Any,
    val order_date: String,
    val order_from_ip: Any,
    val order_id: String,
    val order_line_item: List<Any>,
    val order_metadata: Any,
    val partner_id: Any,
    val referrer_platform: Any,
    val status: String,
    val sub_merchant_id: Int,
    val tax: Int,
    val total_amount: Int,
    val user: User
)