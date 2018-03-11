package com.awareframework.android.core.db

import android.content.Context
import com.awareframework.android.core.model.AwareData
import com.awareframework.android.core.model.AwareObject


/**
 * Base interface for implementing database engines.
 *
 * @author  sercant
 * @date 15/02/2018
 */
abstract class Engine(
        protected val context: Context,
        protected val encryptionKey: String?,
        protected val dbName: String
) {

    enum class DatabaseType {
        ROOM,
        NONE
    }

    class Builder(val context: Context) {
        protected var type: DatabaseType = DatabaseType.NONE
        protected var encryptionKey: String? = null
        protected var dbName: String = "aware_database.db"

        fun setDbType(type: DatabaseType) = apply { this.type = type }
        fun setDbKey(encryptionKey: String?) = apply { this.encryptionKey = encryptionKey }
        fun setDbName(name: String) = apply { this.dbName = name }

        fun build(): Engine? {
            return when (type) {
                DatabaseType.ROOM -> RoomEngine(context, encryptionKey, dbName)
                DatabaseType.NONE -> null
            }
        }
    }

    abstract fun getAll(tableName: String): List<AwareData>?

    abstract fun <T> save(datas: List<T>, tableName: String = this.dbName) : Thread where T : AwareObject
    abstract fun <T> save(data: T, tableName: String = this.dbName): Thread where T : AwareObject
    abstract fun removeAll(): Thread
    abstract fun close()
}

