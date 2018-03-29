package com.awareframework.android.core.db

import android.content.Context
import com.awareframework.android.core.db.model.DbSyncConfig
import com.awareframework.android.core.db.room.AwareDataEntity
import com.awareframework.android.core.db.room.AwareRoomDatabase
import com.awareframework.android.core.model.AwareData
import com.awareframework.android.core.model.AwareObject
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import java.util.*
import kotlin.concurrent.thread

/**
 * Database engine implementation using Room.
 *
 * @author  sercant
 * @date 19/02/2018
 */
class RoomEngine(
        context: Context,
        path: String,
        host: String?,
        encryptionKey: String?
) : Engine(
        context,
        path,
        host,
        encryptionKey
) {

    fun db(): AwareRoomDatabase = AwareRoomDatabase.getInstance(context, encryptionKey, path)

    override fun <T : AwareObject> save(data: Array<T>, tableName: String?): Thread {
        return thread {
            try {
                val table = tableName ?: path

                val awareData = arrayListOf<AwareDataEntity>()
                data.forEach {
                    awareData.add(AwareDataEntity(data = AwareData(it,  table)))
                }
                db().AwareDataDao().insertAll(awareData.toTypedArray())
            } catch (e: Exception) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun <T : AwareObject> save(data: T, tableName: String?, id: Long?): Thread {
        return thread {
            try {
                val table = tableName ?: path
                db().AwareDataDao().insert(AwareDataEntity(id = id, data = AwareData(data,  table)))
            } catch (e: Exception) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun getAll(tableName: String): List<AwareData>? {
        return db().AwareDataDao().getAll(tableName)
    }

    override fun get(tableName: String, batchSize: Int): List<AwareData>? {
        return db().AwareDataDao().get(tableName, batchSize)
    }

    override fun remove(data: List<AwareData>): Thread {
        return thread {
            try {
                if (data.all {
                            it is AwareDataEntity
                        }) {
                    db().AwareDataDao().deleteData(data as List<AwareDataEntity>)
                }
            } catch (e: Exception) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun removeAll(): Thread {
        return thread {
            try {
                db().AwareDataDao().deleteAll()
            } catch (e: Exception) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun close() {
        // TODO (sercant): disabled the close on db instance because of multithreading trouble. need a solution.
//        syncTasks.forEach { _, dataSyncHelper -> dataSyncHelper.interrupt() }
//        db?.close()
//        db = null
    }

    private var syncTasks: WeakHashMap<String, DataSyncHelper> = WeakHashMap()

    override fun startSync(tableName: String, config: DbSyncConfig) {
        val syncHelper = syncTasks[tableName]

        syncHelper?.stopSync()

        host ?: return

        val newSyncHelper = DataSyncHelper(this, host, tableName, config)
        newSyncHelper.start()

        syncTasks[tableName] = newSyncHelper
    }

    override fun stopSync() {
        syncTasks.keys.forEach {
            val syncHelper = syncTasks[it]
            syncHelper?.stopSync()

            syncTasks[it] = null
        }
    }

    private class DataSyncHelper(
            val engine: RoomEngine,
            host: String,
            var tableName: String,
            var config: DbSyncConfig
    ) : Thread() {

        var syncing: Boolean = true

        var activeRequest: Request = host.httpPost()

        override fun run() {
            val entryCount = engine.db().AwareDataDao().count(tableName)
            val syncCount =  if (entryCount > config.batchSize) entryCount / config.batchSize else 1
//            Log.d("test", "Will sync table $tableName, $syncCount times.")

            for (i in 0..syncCount) {
                if (!syncing) break

                val data = engine.db().AwareDataDao().get(tableName, config.batchSize)

                if (data.isEmpty()) break

                activeRequest.header(Pair("Content-Type", "application/json"))
                activeRequest.body(Gson().toJson(data))

                // waits for the response
                val (_, _, result) = activeRequest.responseString()

                // TODO (sercant): check the result comes from the server as content as well.
                result.fold({
                    if (config.removeAfterSync) {
                        val rowCount = engine.db().AwareDataDao().deleteData(data)
                        if (rowCount != data.size) {
                            //TODO (sercant): log that there is something wrong.
                        }
                    }
                },{
                    it.printStackTrace()
                })
            }

            syncing = false
        }

        fun stopSync() {
            syncing = false
            activeRequest.cancel()
        }

        override fun interrupt() {
            stopSync()
            super.interrupt()
        }
    }
}