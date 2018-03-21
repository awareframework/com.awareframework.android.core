package com.awareframework.android.core.model

import android.content.Context
import com.awareframework.android.core.db.Engine

/**
 * Class decription
 *
 * @author  sercant
 * @date 07/03/2018
 */

open class SensorConfig(
    /**
     * Sensor is enabled or not. (optional)
     */
    var enabled: Boolean = false,

    /**
     * Enables logging. (optional)
     */
    var debug: Boolean = false,

    /**
     * Label for the data. (optional)
     */
    var label: String = "",

    /**
     * User given deviceId. (optional)
     */
    var deviceId: String = "",

        /**
     * Encryption key for the database. (optional)
     */
    var dbEncryptionKey: String? = null,

    /**
     * Which database to use. (optional)
     * defaults to NONE, which doesn't preserve any data.
     */
    var dbType: Engine.DatabaseType = Engine.DatabaseType.NONE,

    /**
     * Database name/path. (optional)? TODO (sercant): discuss
     */
    var dbPath: String = "aware",

    /**
     * Database sync host. (optional)
     */
    var dbHost: String? = null
) {
     abstract class Builder<T: SensorConfig>(val context: Context) {

        /**
         * @param label collected data will be labeled accordingly. (default = "")
         */
        fun setLabel(label: String) = apply { getConfig().label = label }

        /**
         * @param debug enable/disable logging to Logcat. (default = false)
         */
        fun setDebug(debug: Boolean) = apply { getConfig().debug = debug }

         /**
          * @param key encryption key for the database. (default = no encryption)
          */
         fun setDatabaseEncryptionKey(key: String) = apply { getConfig().dbEncryptionKey = key }

         /**
          * @param host host for syncing the database. (default = null)
          */
         fun setDatabaseHost(host: String) = apply { getConfig().dbHost = host }

         /**
          * @param type which db engine to use for saving data. (default = NONE)
          */
         fun setDatabaseType(type: Engine.DatabaseType) = apply { getConfig().dbType = type }

         protected abstract fun getConfig(): T
    }
}