package com.awareframework.android.core.manager

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log
import com.awareframework.android.core.AwareSensor

/**
 * Class to broadcast sync events
 * to the modules to sync data to the server
 *
 * @author  sercant
 * @date 15/03/2018
 */
class DbSyncManager private constructor(
        var config: DbSyncManagerConfig,
        var context: Context
) {

    companion object {
        const val TAG: String = "com.aware.manager.sync"

        internal fun startService(context: Context) {
            val intent = Intent(context, DbSyncManagerService::class.java)
            context.startService(intent)
        }

        internal fun stopService(context: Context) {
            context.stopService(Intent(context, DbSyncManagerService::class.java))
        }
    }

    fun start() {
        DbSyncManagerService.CONFIG = config

        logd("Starting DbSyncManagerService with config:\n" + config.toString())

        startService(context)
    }

    fun syncDb(force: Boolean = false) {
        DbSyncManagerService.instance?.onHandle(force)
    }


    fun stop() {
        logd("Stopping DbSyncManagerService.")

        stopService(context)
    }

    data class DbSyncManagerConfig(
            /**
             * Sync interval in minutes (default = 1)
             */
            var syncInterval: Float = 1f,

            /**
             * Sync only while connected to wifi (default = true)
             */
            var wifiOnly: Boolean = true,

            /**
             * Sync only while device is charging (default = false)
             */
            var batteryChargingOnly: Boolean = false,

            /**
             * Enable/disable logs to logcat. (default = false)
             */
            var debug: Boolean = false
    ) {
        fun isWifiOnly(): Boolean = wifiOnly
        fun isBatteryChargingOnly(): Boolean = batteryChargingOnly
        fun intervalInMiliseconds(): Long = (syncInterval * 1000 * 60).toLong()

    }

    class Builder(val context: Context) {
        val config = DbSyncManagerConfig()

        fun setSyncInterval(interval: Float) = apply { config.syncInterval = interval }
        fun setWifiOnly(wifiOnly: Boolean) = apply { config.wifiOnly = wifiOnly }
        fun setBatteryChargingOnly(batteryChargingOnly: Boolean) = apply { config.batteryChargingOnly = batteryChargingOnly }
        fun setDebug(debug: Boolean) = apply { config.debug = debug }
        fun build(): DbSyncManager = DbSyncManager(config, context)
    }

    class DbSyncManagerService: Service() {

        companion object {
            var instance: DbSyncManagerService? = null
            var CONFIG: DbSyncManagerConfig = DbSyncManagerConfig()
        }

        private var handler: Handler? = null
        private var runnable: () -> Unit = {
            onHandle()
        }

        override fun onBind(p0: Intent?): IBinder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            return Service.START_STICKY
        }

        override fun onCreate() {
            super.onCreate()

            if (instance != null) {
                logw("Sync instance is already running?")
            }

            logd( "Sync service with interval: " + CONFIG.intervalInMiliseconds() + " ms created.")

            instance = this
            handler = Handler()
            handler!!.postDelayed(runnable, CONFIG.intervalInMiliseconds())
        }

        internal fun onHandle(force: Boolean = false) {
            // TODO (sercant): put configuration into the intent

            logd( "Attempting to sync.")

            var shouldBroadcast = true
            if (CONFIG.wifiOnly && !isWifiConnected()) {
                shouldBroadcast = false

                logd( "Wifi is not connected aborting sync.")
            }


            if (CONFIG.batteryChargingOnly && !isCharging()) {
                shouldBroadcast = false

                logd( "Battery is not charging aborting sync.")
            }

            if (shouldBroadcast) {
                sendBroadcast(Intent(AwareSensor.SensorSyncReceiver.SYNC))

                logd("Sending sync broadcast!")
            }

            handler?.postDelayed(runnable, CONFIG.intervalInMiliseconds())
        }

        private fun isCharging(): Boolean {
            val batt = applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val plugged = batt.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB
        }

        @SuppressLint("MissingPermission")
        private fun isWifiConnected(): Boolean {
            return if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                val connManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connManager.activeNetworkInfo
                activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected
            } else {
                // TODO (sercant): if we don't have the permission just try syncing anyways?
                logw("Can't get wifi state. Required permission is not granted!")
                true
            }
        }
    }
}

private fun logd(msg: String){
    if (DbSyncManager.DbSyncManagerService.CONFIG.debug) Log.d(DbSyncManager.TAG, msg)
}

private fun logw(msg: String){
    Log.w(DbSyncManager.TAG, msg)
}