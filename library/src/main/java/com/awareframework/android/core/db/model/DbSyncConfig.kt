package com.awareframework.android.core.db.model

/**
 * Class decription
 *
 * @author  sercant
 * @date 23/03/2018
 */
data class DbSyncConfig(
        val removeAfterSync: Boolean = true,
        val batchSize: Int = 100
)