package com.example.nimbbl.model.createoder

data class OrderDetailVo(
    val additional_charges: Int,
    val amount_before_tax: Any,
    val attempts: Int,
    val browser_name: String,
    val callback_mode: Any,
    val callback_url: Any,
    val cancellation_reason: Any,
    val currency: String,
    val description: Any,
    val device_name: String,
    val device_user_agent: String,
    val fingerprint: Any,
    val grand_total_amount: String,
    val invoice_id: Any,
    val max_retries: Int,
    val merchant_shopfront_domain: String,
    val message: String,
    val order: Order,
    val order_date: String,
    val order_from_ip: Any,
    val order_id: String,
    val order_line_item: List<Any>,
    val order_transac_type: Any,
    val os_name: String,
    val partner_id: Any,
    val referrer_platform: Any,
    val referrer_platform_version: Any,
    val status: String,
    val sub_merchant: SubMerchant,
    val sub_merchant_id: Int,
    val tax: Int,
    val total_amount: String,
    val user: User
)