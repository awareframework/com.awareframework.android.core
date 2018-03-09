package com.awareframework.android.core.db.room

import android.arch.persistence.room.*

/**
 * Room data DAO
 *
 * @author  sercant
 * @date 22/02/2018
 */
@Dao
interface AwareDataDao {

    @Query("select * from awareData")
    fun getAll(): List<AwareDataEntity>

    @Query("select * from awareData where tableName = :tableName")
    fun getAll(tableName: String): List<AwareDataEntity>

    @Query("select * from awareData where id = :id")
    fun findById(id: Long): AwareDataEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: AwareDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: Array<AwareDataEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(data: AwareDataEntity)

    @Delete
    fun delete(data: AwareDataEntity)

    @Query("DELETE FROM awareData")
    fun deleteAll()
}