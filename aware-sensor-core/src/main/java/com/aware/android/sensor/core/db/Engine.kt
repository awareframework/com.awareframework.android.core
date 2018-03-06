package com.aware.android.sensor.core.db

import android.content.Context
import com.aware.android.sensor.core.model.AwareObject


/**
 * Base interface for implementing database engines.
 *
 * @author  sercant
 * @date 15/02/2018
 */
abstract class Engine(
        private val context: Context,
        private val encryptionKey: String?,
        private val dbName: String
) {

    enum class DatabaseType {
        ROOM,
        NONE
    }

    abstract class Builder(val context: Context) {
        protected var type: DatabaseType = DatabaseType.NONE
        protected var encryptionKey: String? = null
        protected var dbName: String = "aware_database.db"

        fun setDatabaseType(type: DatabaseType) = apply { this.type = type }
        fun setEncryptionKey(encryptionKey: String?) = apply { this.encryptionKey = encryptionKey }
        fun setDatabaseName(name: String) = apply { this.dbName = name }

        abstract fun build(): Engine?
    }

    abstract fun <T> getAll(klass: Class<T>): List<T>?

    abstract fun <T> save(datas: Array<T>) : Thread where T: AwareObject
    abstract fun <T> save(data: T): Thread where T: AwareObject
    abstract fun removeAll(): Thread
    abstract fun close()
}
