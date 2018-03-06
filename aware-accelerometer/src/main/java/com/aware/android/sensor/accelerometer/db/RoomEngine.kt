package com.aware.android.sensor.accelerometer.db

import android.content.Context
import com.aware.android.sensor.core.model.AwareObject
import com.aware.android.sensor.accelerometer.db.room.AccelerometerRoomDatabase
import com.aware.android.sensor.accelerometer.db.room.DeviceRoomEntity
import com.aware.android.sensor.accelerometer.db.room.EventRoomEntity
import com.aware.android.sensor.accelerometer.model.AccelerometerDevice
import com.aware.android.sensor.accelerometer.model.AccelerometerEvent
import net.sqlcipher.database.SQLiteException
import kotlin.concurrent.thread

/**
 * Database engine implementation using Room.
 *
 * @author  sercant
 * @date 19/02/2018
 */
class RoomEngine(context: Context, encryptionKey: String?, dbName: String) : DbEngine(context, encryptionKey, dbName) {

    // TODO (sercant): We should hold a reference to the database object here since this class is instantiated now.
    init {
        AccelerometerRoomDatabase.init(context, encryptionKey, dbName)
    }

    override fun <T> save(datas: Array<T>): Thread where T: AwareObject {
        return thread {
            try {
                if (datas.all { it is AccelerometerEvent }) {
                    @Suppress("UNCHECKED_CAST")
                    datas as Array<AccelerometerEvent>

                    val db = AccelerometerRoomDatabase.instance
                    val data = arrayListOf<EventRoomEntity>()
                    datas.forEach { event: AccelerometerEvent ->
                        data.add(EventRoomEntity(event = event))
                    }
                    db!!.AccelerometerEventDao().insertAll(data.toTypedArray())
                }
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun <T> save(data: T): Thread where T: AwareObject {
        return thread {
            try {
                if (data is AccelerometerDevice) {
                    val db = AccelerometerRoomDatabase.instance
                    // TODO (sercant): We don't expect to have several sensors in one device right?
                    val device = DeviceRoomEntity(0, data)
                    db!!.AccelerometerDeviceDao().insert(device)
                }
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun <T> getAll(klass: Class<T>): List<T>? {
        var result: List<T>? = null
        val db = AccelerometerRoomDatabase.instance


        when (klass) {
            AccelerometerEvent::class.java -> {
                @Suppress("UNCHECKED_CAST")
                result = db!!.AccelerometerEventDao().getAll() as List<T>
            }
            AccelerometerDevice::class.java -> {
                @Suppress("UNCHECKED_CAST")
                result = db!!.AccelerometerDeviceDao().getAll() as List<T>
            }
        }

        return result
    }

    override fun removeAll(): Thread {
        return thread {
            try {
                val db = AccelerometerRoomDatabase.instance
                db!!.clearAllData()
            } catch (e: SQLiteException) {
                // TODO (sercant): user changed the password for the db. Handle it!
                e.printStackTrace()
            }
        }
    }

    override fun close() {
        AccelerometerRoomDatabase.destroyInstance()
    }
}