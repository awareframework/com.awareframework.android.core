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
class RoomEngine(context: Context, encryptionKey: String?, dbName: String) : Engine(context, encryptionKey, dbName) {

    var db: AwareRoomDatabase? = AwareRoomDatabase.getInstance(context, encryptionKey, dbName)

    override fun save(datas: List<AwareObject>, tableName: String): Thread {
        return thread {
            try {
                val data = arrayListOf<AwareDataEntity>()
                datas.forEach {
                    data.add(AwareDataEntity(data = AwareData(it,  tableName)))
                }
                db!!.AwareDataDao().insertAll(data.toTypedArray())
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun save(data: AwareObject, tableName: String): Thread {
        return thread {
            try {
                db!!.AwareDataDao().insert(AwareDataEntity(data = AwareData(data,  tableName)))
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