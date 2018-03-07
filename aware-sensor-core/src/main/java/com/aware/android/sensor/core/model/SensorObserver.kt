package com.aware.android.sensor.core.model

/**
 * Class decription
 *
 * @author  sercant
 * @date 07/03/2018
 */
interface SensorObserver {
    fun onDataChanged(type: String, data: Any?, error: Any?)
}