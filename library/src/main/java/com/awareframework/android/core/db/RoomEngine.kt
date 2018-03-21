package com.awareframework.android.core.db

import android.content.Context
import android.database.sqlite.SQLiteException
import com.awareframework.android.core.db.room.AwareDataEntity
import com.awareframework.android.core.db.room.AwareRoomDatabase
import com.awareframework.android.core.model.AwareData
import com.awareframework.android.core.model.AwareObject
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

    var db: AwareRoomDatabase? = AwareRoomDatabase.getInstance(context, encryptionKey, path)

    override fun <T : AwareObject> save(data: Array<T>, tableName: String?): Thread {
        return thread {
            try {
                val table = if (tableName != null) tableName else path

                val awareData = arrayListOf<AwareDataEntity>()
                data.forEach {
                    awareData.add(AwareDataEntity(data = AwareData(it,  table)))
                }
                db!!.AwareDataDao().insertAll(awareData.toTypedArray())
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun <T : AwareObject> save(data: T, tableName: String?, id: Long?): Thread {
        return thread {
            try {
                val table = if (tableName != null) tableName else path
                db!!.AwareDataDao().insert(AwareDataEntity(id = id, data = AwareData(data,  table)))
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun getAll(tableName: String): List<AwareData>? {
        return db!!.AwareDataDao().getAll(tableName)
    }

    override fun removeAll(): Thread {
        return thread {
            try {
                db!!.AwareDataDao().deleteAll()
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun close() {
        db?.close()
        db = null
    }
}