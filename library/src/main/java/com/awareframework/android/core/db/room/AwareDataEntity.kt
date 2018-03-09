package com.awareframework.android.core.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.awareframework.android.core.model.AwareData

/**
 * Room data entity
 *
 * @author  sercant
 * @date 02/03/2018
 */
@Entity(tableName = "awareData")
class AwareDataEntity(
        @PrimaryKey var id: Long? = null,
        data: AwareData = AwareData()
) : AwareData(data)