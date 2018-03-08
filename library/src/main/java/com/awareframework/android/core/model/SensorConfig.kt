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
        var dbKey: String? = null,

        /**
         * Which database to use. (optional)
         * defaults to NONE, which doesn't preserve any data.
         */
        var dbType: Engine.DatabaseType = Engine.DatabaseType.NONE,

        /**
         * Database name/path. (optional)? TODO (sercant): discuss
         */
        var dbName: String = "aware.db"
)