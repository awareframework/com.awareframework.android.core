package com.aware.android.sensor.core.model

import com.google.gson.Gson
import java.util.*

/**
 * Holds the generic data of all aware related entries
 *
 * @author  sercant
 * @date 17/02/2018
 */
open class AwareObject(
        var timestamp: Long = 0L,
        var timezone: Int = TimeZone.getDefault().rawOffset,
        var deviceId: String = "",
        var label: String = "",
        var os: String = "android",
        var jsonVersion: Int = 0
) {

    open fun toJson(): String {
        return Gson().toJson(this)
    }
}