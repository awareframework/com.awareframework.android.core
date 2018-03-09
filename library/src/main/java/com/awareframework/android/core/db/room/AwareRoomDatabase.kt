package com.awareframework.android.core.db.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.commonsware.cwac.saferoom.SafeHelperFactory

/**
 * Room database class.
 * Note that creating new instances of Room db is costly.
 *
 * @author  sercant
 * @date 23/02/2018
 */
@Database(entities = arrayOf(AwareDataEntity::class), version = 1)
abstract class AwareRoomDatabase : RoomDatabase() {

    abstract fun AwareDataDao(): AwareDataDao

    companion object {
        /**
         * This creating instance is expensive and should be closed by calling `close()` after db is no
         * longer needed.
         */
        fun getInstance(context: Context, encryptionKey: String?, dbName: String): AwareRoomDatabase {
            val builder = Room.databaseBuilder(context.applicationContext,
                    AwareRoomDatabase::class.java, dbName)
            if (encryptionKey != null) {
                builder.openHelperFactory(SafeHelperFactory(encryptionKey.toCharArray()))
            }

            return builder
                    // TODO (sercant): handle migrations!
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}