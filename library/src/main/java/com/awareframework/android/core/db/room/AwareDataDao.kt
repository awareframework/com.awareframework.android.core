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

    @Query("select * from awareData where tableName = :tableName order by timestamp desc limit :n")
    fun getLatest(tableName: String, n: Int): AwareDataEntity

    @Query("select * from awareData where tableName = :tableName order by timestamp asc limit :batchSize")
    fun get(tableName: String, batchSize: Int): List<AwareDataEntity>

    @Query("select count(*) from awareData where tableName = :tableName")
    fun count(tableName: String) : Int

    @Query("select * from awareData where id = :id")
    fun findById(id: Long): AwareDataEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: AwareDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: Array<AwareDataEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(data: AwareDataEntity)

    @Delete
    fun delete(data: AwareDataEntity) : Int

    @Delete
    fun deleteData(data: List<AwareDataEntity>) : Int

    @Query("DELETE FROM awareData")
    fun deleteAll()
}