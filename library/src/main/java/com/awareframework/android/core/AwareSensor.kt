package com.awareframework.android.core

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


/**
 * Class decription
 *
 * @author  sercant
 * @date 05/03/2018
 */

abstract class AwareSensor : Service() {

    abstract class SensorBroadcastReceiver : BroadcastReceiver() {
        companion object {
            const val SENSOR_START_ENABLED = "com.aware.android.sensor.SENSOR_START"
            const val SENSOR_STOP_ALL = "com.aware.android.sensor.SENSOR_STOP"
        }

        abstract override fun onReceive(context: Context?, intent: Intent?)
    }
}