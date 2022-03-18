package com.example.nimbbl.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast

/*
Created by Sandeep Yadav on 23/02/22.
Copyright (c) 2022 Bigital Technologies Pvt. Ltd. All rights reserved.
*/


fun Activity.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.printLog(tag: String, message: String) {
    Log.d(tag, message)
}

/**
 * Hide some cell phone numbers
 * @param phone
 * @return
 */
fun hidePhoneNum(phone: String): String {
    return if(phone.length>9) {
        phone.substring(0, 3) + "XXXX" + phone.substring(7)
    }else{
        phone
    }
}


