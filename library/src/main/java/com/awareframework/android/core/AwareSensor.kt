package com.awareframework.android.core

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.awareframework.android.core.db.Engine


/**
 * Class decription
 *
 * @author  sercant
 * @date 05/03/2018
 */

abstract class AwareSensor : Service() {

    protected var dbEngine: Engine? = null
    protected var sensorSyncReceiver: SensorSyncReceiver? = null

    override fun onCreate() {
        super.onCreate()

        sensorSyncReceiver = SensorSyncReceiver(this)
        val syncFilter = IntentFilter()
        syncFilter.addAction(SensorSyncReceiver.SYNC)
        applicationContext.registerReceiver(sensorSyncReceiver, syncFilter)
    }

    override fun onDestroy() {
        super.onDestroy()

        applicationContext.unregisterReceiver(sensorSyncReceiver)
        sensorSyncReceiver = null
    }

    fun onSync(intent: Intent?) {
        // TODO (sercant): pass parameters directly to this function instead of passing intent
        // TODO (sercant): parameters needs to be passed to the engine too.
//        dbEngine?.sync()
    }

    abstract class SensorBroadcastReceiver : BroadcastReceiver() {
        companion object {
            const val SENSOR_START_ENABLED = "com.aware.android.sensor.SENSOR_START"
            const val SENSOR_STOP_ALL = "com.aware.android.sensor.SENSOR_STOP"
//            const val AWARE_SYNC = "com.aware.android.sensor.AWARE_SYNC"
        }

        abstract override fun onReceive(context: Context?, intent: Intent?)
    }

    class SensorSyncReceiver(val sensor: AwareSensor) : BroadcastReceiver() {

        companion object {
            const val SYNC = "com.aware.android.sensor.SYNC"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SYNC) {
                // TODO (sercant): take the arguments from intent and pass them to the sensor.
                sensor.onSync(intent)
            }
        }
    }
}