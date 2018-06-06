package com.awareframework.android.core.db

import android.content.Context
import com.awareframework.android.core.db.model.DbSyncConfig
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
        protected val path: String,
        protected val host: String?,
        protected val encryptionKey: String?
) {

    enum class DatabaseType {
        ROOM,
        NONE
    }

    class Builder(val context: Context) {
        protected var type: DatabaseType = DatabaseType.NONE
        protected var encryptionKey: String? = null
        protected var host: String? = null
        protected var path: String = "aware_database.db"

        fun setType(type: DatabaseType) = apply { this.type = type }
        fun setEncryptionKey(encryptionKey: String?) = apply { this.encryptionKey = encryptionKey }
        fun setPath(path: String) = apply { this.path = path }
        fun setHost(host: String?) = apply { this.host = host }

        fun build(): Engine? {
            return when (type) {
                DatabaseType.ROOM -> RoomEngine(context, path, host, encryptionKey)
                DatabaseType.NONE -> null
            }
        }
    }

    abstract fun get(tableName: String, batchSize: Int, callback: (List<AwareData>?) -> Unit): Thread
    abstract fun getAll(tableName: String, callback: (List<AwareData>?) -> Unit): Thread
    abstract fun getLatest(tableName: String, n: Int = 1, callback: (AwareData?) -> Unit): Thread

    abstract fun <T : AwareObject> save(data: Array<T>, tableName: String? = null) : Thread
    abstract fun <T : AwareObject> save(data: T, tableName: String? = null, id: Long? = null): Thread
    abstract fun <T : AwareData> update(data: T): Thread
    abstract fun removeAll(): Thread
    abstract fun remove(data: List<AwareData>): Thread
    abstract fun close()
    abstract fun startSync(tableName: String, config: DbSyncConfig = DbSyncConfig())
    abstract fun stopSync()
}

