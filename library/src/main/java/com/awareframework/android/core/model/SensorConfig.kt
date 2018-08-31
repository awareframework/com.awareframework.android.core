package com.awareframework.android.core.model

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
         * defaults to [Engine.DatabaseType.ROOM].
         */
        var dbType: Engine.DatabaseType = Engine.DatabaseType.ROOM,

        /**
         * Database name/path. (optional)? TODO (sercant): discuss
         */
        var dbPath: String = "aware",

        /**
         * Database sync host. (optional)
         */
        var dbHost: String? = null
) {
    open fun <T : SensorConfig> replaceWith(config: T) {
        enabled = config.enabled
        debug = config.debug
        label = config.label
        deviceId = config.deviceId
        dbEncryptionKey = config.dbEncryptionKey
        dbType = config.dbType
        dbPath = config.dbPath
        dbHost = config.dbHost
    }
}