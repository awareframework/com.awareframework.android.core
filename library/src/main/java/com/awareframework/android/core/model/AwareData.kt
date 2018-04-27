package com.awareframework.android.core.model

import com.google.gson.Gson

/**
 * Class decription
 *
 * @author  sercant
 * @date 09/03/2018
 */
open class AwareData (
        var timestamp: Long = 0L,
        var data: String = "",
        var tableName: String = "",
        var deviceId: String = ""
) {
    constructor(data: AwareObject, tableName: String) : this(
            data.timestamp,
            data.toJson(),
            tableName,
            data.deviceId
    )

    constructor(other: AwareData) : this (
            other.timestamp,
            other.data,
            other.tableName,
            other.deviceId
    )

    inline fun <reified T: AwareObject> alterData(block: (data: T) -> Unit) {
        val temp = Gson().fromJson(data, T::class.java)
        block(temp)
        this.data = Gson().toJson(temp)
    }

    inline fun <reified T: AwareObject> withData(block: (data: T) -> Unit) {
        block(Gson().fromJson(data, T::class.java))
    }
}